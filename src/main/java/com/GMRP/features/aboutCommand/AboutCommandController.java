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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class AboutCommandController extends ListenerAdapter implements ISlashCommandController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AboutCommandController.class);

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

		MDC.put("command", "about");
		MDC.put("userId", event.getUser().getId());
		MDC.put("guildId", event.getGuild() != null ? event.getGuild().getId() : "DM");
		MDC.put("channelId", event.getChannel().getId());

		try {
			LOGGER.debug("Executing /about command");

			String ownerId;
			try {
				ownerId = databaseManager.getConfigKey("OWNER");
			} catch (DatabaseManagerException e) {
				LOGGER.error("Failed to retrieve OWNER config key", e);
				event.reply("Owner ID not found in the database.").setEphemeral(true).queue();
				return;
			}
			String ownerMention = "<@" + ownerId + ">";
			String currentBranch = gitManager.getCurrentBranch();
			String version = VersionReader.getVersion();
			MessageEmbed aboutEmbed = view.formatAboutEmbed(ownerMention, currentBranch, version);
			event.replyEmbeds(aboutEmbed).queue();
			LOGGER.debug("Successfully replied to /about command");
		} finally {
			MDC.clear();
		}
	}
}