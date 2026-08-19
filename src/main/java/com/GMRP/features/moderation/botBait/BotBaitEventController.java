/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.features.moderation.botBait;

import com.GMRP.core.databaseManager.exception.DatabaseManagerException;
import com.GMRP.core.databaseManager.IDatabaseManager;
import com.GMRP.features.IEventListenerController;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.Permission;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.concurrent.TimeUnit;

public class BotBaitEventController extends ListenerAdapter implements IEventListenerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BotBaitEventController.class);

	BotBaitView view;
	IDatabaseManager databaseManager;

	public BotBaitEventController(BotBaitView view, IDatabaseManager databaseManager) {
		this.view = view;
		this.databaseManager = databaseManager;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		// Ensure the message occurs in a Guild.
		if (!event.isFromGuild())
			return;

		// Only applies to the Bot Bait channel.
		String botBaitChannelId;
		try {
			botBaitChannelId = databaseManager.getConfigKey("BAIT");
		} catch (DatabaseManagerException e) {
			LOGGER.error("Error occurred while fetching BAIT config key.", e);
			return;
		}
		if (!event.getChannel().getId().equals(botBaitChannelId))
			return;

		// Don't ban actual bots nor try to ban a webhook.
		if (event.getAuthor().isBot() || event.isWebhookMessage())
			return;

		MDC.put("event", "BotBaitHoneypot");
		MDC.put("userId", event.getAuthor().getId());

		try {

			LOGGER.debug("Processing honeypot message.");

			if (!event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
				LOGGER.error("Bot does not have permission to ban members.");
				event.getChannel().sendMessage("I don't have permission to ban members.").queue();
				return;
			}

			if (event.getMember() == null || !event.getGuild().getSelfMember().canInteract(event.getMember())) {
				LOGGER.error("Bot cannot ban the member. Deleting message.");
				event.getMessage().delete().queue();
				sendEmbedIfNecessary(event);
				return;
			}

			// Try to ban the self-bot user account, deleting the last hour of messages,
			// just in case they spammed other channels.
			LOGGER.info("Banning self-bot account.");
			event.getGuild().ban(event.getAuthor(), 1, TimeUnit.HOURS).reason("self-bot account").queue();
			sendEmbedIfNecessary(event);
		} finally {
			MDC.clear();
		}
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
