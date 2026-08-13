/* SPDX-License-Identifier: AGPL-3.0-or-later */
package com.GMRP.features.helpCommand;

import com.GMRP.features.SlashCommandController;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class HelpCommandController extends ListenerAdapter implements SlashCommandController {
	private HelpEmbedView view;

	public HelpCommandController(HelpEmbedView view) {
		this.view = view;
	}

	@Override
	public SlashCommandData getCommandSetup() {
		return Commands.slash("help", "Get a list of Bot Commands.");
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().equalsIgnoreCase("help")) {
			event.getJDA().retrieveCommands().queue(commands -> {
				MessageEmbed helpEmbed = view.formatHelpEmbed(commands);
				event.replyEmbeds(helpEmbed).queue();
			});
		}
	}

}
