package com.GMRP;

import java.io.IOException;
import java.util.*;

import com.GMRP.core.gitManager.GitManager;
import com.GMRP.features.LoopController;
import com.GMRP.features.SlashCommandController;
import com.GMRP.features.aboutCommand.AboutEmbedView;
import com.GMRP.features.helpCommand.HelpCommandController;
import com.GMRP.features.helpCommand.HelpEmbedView;
import com.GMRP.features.moderation.botBait.BotBaitEventController;
import com.GMRP.features.moderation.botBait.BotBaitView;
import com.GMRP.views.shared.BotEmbedBuilder;
import com.GMRP.features.aboutCommand.AboutCommandController;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class Main {
	public static void main(String[] args) {
		// Initialize Models
		// GMRP Repo
		BotConfig.init();
		try {
			GitManager GMRPRepoManager = new GitManager(".");

			// Slash Commands
			List<SlashCommandController> slashCommands = new ArrayList<>();

			AboutEmbedView aboutEmbedView = new AboutEmbedView();
			slashCommands.add(new AboutCommandController(aboutEmbedView, GMRPRepoManager));

			HelpEmbedView helpEmbedView = new HelpEmbedView();
			slashCommands.add(new HelpCommandController(helpEmbedView));

			String myToken = BotConfig.getInstance().getToken();
			JDABuilder builder = JDABuilder.createDefault(myToken);

			for (SlashCommandController controller : slashCommands) {
				builder.addEventListeners(controller); // Tell JDA to listen to them
			}

			// Loops
			List<LoopController> loops = new ArrayList<>();

			BotBaitView botBaitEmbedView = new BotBaitView();
			loops.add(new BotBaitEventController(botBaitEmbedView));

			for (LoopController loop : loops) {
				builder.addEventListeners(loop);
			}

			JDA jda = builder.build().awaitReady();

			// set bot of BotEmbedBuilder
			BotEmbedBuilder.setAvatarUrl(jda.getSelfUser().getAvatarUrl());

			// Batch Register the Slash Commands to the server
			List<SlashCommandData> commandSetups = new ArrayList<>();

			for (SlashCommandController controller : slashCommands) {
				commandSetups.add(controller.getCommandSetup());
			}

			// Guild to save the commands to
			Guild guild = jda.getGuildById(BotConfig.getInstance().getServerID());
			// Push the list of command setups to the guild
			guild.updateCommands().addCommands(commandSetups).queue();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
