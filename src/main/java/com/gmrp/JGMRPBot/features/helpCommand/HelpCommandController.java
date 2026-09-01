/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.gmrp.JGMRPBot.features.helpCommand;

import com.gmrp.JGMRPBot.features.SlashCommandController;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class HelpCommandController extends ListenerAdapter implements SlashCommandController {
	private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommandController.class);

	private final HelpEmbedView view;

	public HelpCommandController(HelpEmbedView view) {
		this.view = view;
	}

	@Override
	public SlashCommandData getCommandSetup() {
		return Commands.slash("help", "Get a list of Bot Commands.");
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (!event.getFullCommandName().equalsIgnoreCase("help"))
			return;

		MDC.put("command", "help");
		MDC.put("userId", event.getUser().getId());
		MDC.put("guildId", event.getGuild() != null ? event.getGuild().getId() : "DM");
		MDC.put("channelId", event.getChannel().getId());

		LOGGER.info("Executing /help command");

		event.getGuild().retrieveCommands().queue(commands -> {
			MessageEmbed helpEmbed = view.formatHelpEmbed(commands);
			event.replyEmbeds(helpEmbed).queue();
		});

		MDC.clear();
	}

}
