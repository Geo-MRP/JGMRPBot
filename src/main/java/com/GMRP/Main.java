/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP;

import java.io.IOException;
import java.util.*;

import com.GMRP.core.databaseManager.DatabaseManagerFactory;
import com.GMRP.core.databaseManager.IDatabaseManager;
import com.GMRP.core.databaseManager.exception.DatabaseManagerException;
import com.GMRP.core.gitManager.GitManager;
import com.GMRP.features.ILoopController;
import com.GMRP.features.ISlashCommandController;
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
	static void main(String[] args) {
		// Initialize Models
		// GMRP Repo
		BotConfig.init();
		try (IDatabaseManager databaseManager = DatabaseManagerFactory.create();) {
			GitManager repositoryManager = new GitManager(".");
			databaseManager.testConnection();

			// Slash Commands
			List<ISlashCommandController> slashCommands = new ArrayList<>();

			AboutEmbedView aboutEmbedView = new AboutEmbedView();
			slashCommands.add(new AboutCommandController(aboutEmbedView, repositoryManager, databaseManager));

			HelpEmbedView helpEmbedView = new HelpEmbedView();
			slashCommands.add(new HelpCommandController(helpEmbedView));

			String myToken = BotConfig.getInstance().getToken();
			JDABuilder builder = JDABuilder.createDefault(myToken);

			for (ISlashCommandController controller : slashCommands) {
				builder.addEventListeners(controller); // Tell JDA to listen to them
			}

			// Loops
			List<ILoopController> loops = new ArrayList<>();

			BotBaitView botBaitEmbedView = new BotBaitView();
			loops.add(new BotBaitEventController(botBaitEmbedView, databaseManager));

			for (ILoopController loop : loops) {
				builder.addEventListeners(loop);
			}

			JDA jda = builder.build().awaitReady();

			// set bot of BotEmbedBuilder
			BotEmbedBuilder.setAvatarUrl(jda.getSelfUser().getAvatarUrl());

			// Batch Register the Slash Commands to the server
			List<SlashCommandData> commandSetups = new ArrayList<>();

			for (ISlashCommandController controller : slashCommands) {
				commandSetups.add(controller.getCommandSetup());
			}

			// Guild to save the commands to
			Guild guild;
			try {
				guild = jda.getGuildById(databaseManager.getConfigKey("SERVER"));
			} catch (DatabaseManagerException e) {
				throw new RuntimeException("Guild ID not found in the database.");
			}

			// Push the list of command setups to the guild
			guild.updateCommands().addCommands(commandSetups).queue();
			Runtime.getRuntime().addShutdownHook(new Thread(databaseManager::close));
		} catch (IOException | InterruptedException | DatabaseManagerException e) {
			e.printStackTrace();
		}
	}
}