/* SPDX-License-Identifier: AGPL-3.0-or-later */
package com.GMRP.features.moderation.botBait;

import com.GMRP.core.databaseManager.DatabaseManager;
import com.GMRP.features.LoopController;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.Permission;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class BotBaitEventController extends ListenerAdapter implements LoopController {
	BotBaitView view;
	DatabaseManager databaseManager;

	public BotBaitEventController(BotBaitView view, DatabaseManager databaseManager) {
		this.view = view;
		this.databaseManager = databaseManager;
	}

	@Override
	public void onMessageReceived(@NotNull MessageReceivedEvent event) {
		// Ensure the message occurs in a Guild.
		if (!event.isFromGuild())
			return;

		// Only applies to the Bot Bait channel.
		try (Connection connection = databaseManager.getConnection();
				PreparedStatement preparedStatement = connection
						.prepareStatement("SELECT CONFIG_VALUE FROM CONFIG WHERE CONFIG_KEY = ?")) {
			preparedStatement.setString(1, "BAIT");
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				// there is no bait channel
				if (!resultSet.next())
					return;
				if (!event.getChannel().getId().equals(resultSet.getString(1)))
					return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Don't ban actual bots nor try to ban a webhook.
		if (event.getAuthor().isBot() || event.isWebhookMessage())
			return;

		if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
			event.getChannel().sendMessage("I don't have permission to ban members.").queue();
			return;
		}

		if (event.getMember() == null || !event.getGuild().getSelfMember().canInteract(event.getMember())) {
			event.getMessage().delete().queue();
			sendEmbedIfNecessary(event);
			return;
		}

		// Try to ban the self-bot user account, deleting the last hour of messages,
		// just in case they spammed other channels.
		event.getGuild().ban(event.getAuthor(), 1, TimeUnit.HOURS).reason("self-bot account").queue();
		sendEmbedIfNecessary(event);
	}

	private void sendEmbedIfNecessary(MessageReceivedEvent event) {
		// get message count in channel
		int count = event.getChannel().getHistory().retrievePast(100).complete().size();
		if (count == 0) {
			MessageEmbed botBaitEmbed = view.formatBotBaitEmbed();
			event.getChannel().sendMessageEmbeds(botBaitEmbed).queue();
		}
	}
}
