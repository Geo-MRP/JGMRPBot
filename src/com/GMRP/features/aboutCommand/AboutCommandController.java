package com.GMRP.features.aboutCommand;

import com.GMRP.BotConfig;
import com.GMRP.core.gitManager.GitManager;
import com.GMRP.features.SlashCommandController;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class AboutCommandController extends ListenerAdapter implements SlashCommandController{
	
	private AboutEmbedView view;
	private GitManager gitManager;
	// Constructor necessary due to having a View and/or Model.
	// We don't want to create a new instance of a View or Model each time the command is run.
	public AboutCommandController(AboutEmbedView view, GitManager gitManager) {
		this.view = view;
		this.gitManager = gitManager;
	}
	
	/// Command Description
	@Override
	public SlashCommandData getCommandSetup() {
		return Commands.slash("about", "Bot Details.");
	}
	
	/// Command Execution
	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (event.getFullCommandName().equalsIgnoreCase("about")) {
            String ownerID = BotConfig.getInstance().getOwnerId();
			String ownerMention = "<@" + ownerID + ">";
            String currentBranch = gitManager.getCurrentBranch();
            MessageEmbed aboutEmbed = view.formatAboutEmbed(ownerMention, currentBranch);
            event.replyEmbeds(aboutEmbed).queue();
		}
	}
}