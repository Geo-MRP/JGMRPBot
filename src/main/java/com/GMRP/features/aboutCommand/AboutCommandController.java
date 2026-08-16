/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.features.aboutCommand;

import com.GMRP.core.databaseManager.IDatabaseManager;
import com.GMRP.core.databaseManager.exception.DatabaseManagerException;
import com.GMRP.core.gitManager.GitManager;
import com.GMRP.features.ISlashCommandController;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class AboutCommandController extends ListenerAdapter implements ISlashCommandController {
	private final AboutEmbedView view;
	private final GitManager gitManager;
	private final IDatabaseManager databaseManager;

	// Constructor necessary due to having a View and/or Model.
	// We don't want to create a new instance of a View or Model each time the
	// command is run.
	public AboutCommandController(AboutEmbedView view, GitManager gitManager, IDatabaseManager databaseManager) {
		this.view = view;
		this.gitManager = gitManager;
		this.databaseManager = databaseManager;
	}

	/// Command Description
	@Override
	public SlashCommandData getCommandSetup() {
		return Commands.slash("about", "Bot Details.");
	}

	/// Command Execution
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (!event.getFullCommandName().equalsIgnoreCase("about"))
			return;

		String ownerId;

		try {
			ownerId = databaseManager.getConfigKey("OWNER");
		} catch (DatabaseManagerException e) {
			event.reply("Owner ID not found in the database.").setEphemeral(true).queue();
			return;
		}
		String ownerMention = "<@" + ownerId + ">";
		String currentBranch = gitManager.getCurrentBranch();
		String version = VersionReader.getVersion();
		MessageEmbed aboutEmbed = view.formatAboutEmbed(ownerMention, currentBranch, version);
		event.replyEmbeds(aboutEmbed).queue();
	}
}