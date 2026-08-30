/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP;

import java.util.*;

import com.GMRP.core.databaseManager.exception.DatabaseManagerException;
import com.GMRP.core.databaseManager.DatabaseManagerFactory;
import com.GMRP.core.databaseManager.IDatabaseManager;
import com.GMRP.core.gitManager.GitManager;
import com.GMRP.core.gitManager.exception.GitManagerException;
import com.GMRP.features.moderation.botBait.BotBaitEventController;
import com.GMRP.features.aboutCommand.AboutCommandController;
import com.GMRP.features.helpCommand.HelpCommandController;
import com.GMRP.features.moderation.botBait.BotBaitView;
import com.GMRP.features.aboutCommand.AboutEmbedView;
import com.GMRP.features.aboutCommand.VersionReader;
import com.GMRP.features.helpCommand.HelpEmbedView;
import com.GMRP.features.ISlashCommandController;
import com.GMRP.features.IEventListenerController;
import com.GMRP.views.shared.BotEmbedBuilder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	private GitManager gitManager;
	private IDatabaseManager databaseManager;

	private final String version = VersionReader.getVersion();

	private final List<ISlashCommandController> slashCommands = new ArrayList<>();
	private final List<IEventListenerController> eventListeners = new ArrayList<>();

	static void main(String[] args) {
		new Main().start(args);
	}

	private void start(String[] args) {
		BotConfig.init();
		initGitManager();

		LOGGER.info("Starting JGMRPBot v{} (branch: {})", version, gitManager.getCurrentBranch());

		initDatabaseManager();

		LOGGER.info("Creating JDA instance");
		String myToken = BotConfig.getInstance().getToken();
		JDABuilder builder = JDABuilder.createDefault(myToken);

		addSlashCommands(builder);
		addEventListeners(builder);

		try {
			JDA jda = builder.build().awaitReady();

			BotEmbedBuilder.setAvatarUrl(jda.getSelfUser().getAvatarUrl());

			// Guild to save the commands to
			Guild guild;
			try {
				guild = jda.getGuildById(databaseManager.getConfigKey("SERVER"));
				if (guild == null) {
					throw new RuntimeException("Guild not found");
				}
			} catch (DatabaseManagerException e) {
				throw new RuntimeException("Guild ID not found in the database");
			}

			pushCommandsToGuild(guild);
		} catch (InterruptedException e) {
			LOGGER.error("JDA instance interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private void initGitManager() {
		try {
			gitManager = new GitManager();
			LOGGER.debug("GitManager initialized");
		} catch (GitManagerException e) {
			LOGGER.error("Failed to initialize GitManager", e);
			throw new RuntimeException(e);
		}
	}

	private void initDatabaseManager() {
		try {
			databaseManager = DatabaseManagerFactory.create();
			LOGGER.debug("DatabaseManager initialized");
		} catch (DatabaseManagerException e) {
			LOGGER.error("Failed to initialize DatabaseManager", e);
			throw new RuntimeException(e);
		}
		boolean connectionSucceeded = databaseManager.testConnection();
		if (!connectionSucceeded) {
			LOGGER.error("Failed to connect to the database");
		} else {
			LOGGER.debug("Connected to the database");
		}
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				databaseManager.close();
				LOGGER.info("Database connection closed gracefully");
			} catch (Exception e) {
				LOGGER.error("Failed to close database connection gracefully", e);
			}
		}));
	}

	private void addSlashCommands(JDABuilder builder) {
		AboutEmbedView aboutEmbedView = new AboutEmbedView();
		slashCommands.add(new AboutCommandController(aboutEmbedView, gitManager, databaseManager));

		HelpEmbedView helpEmbedView = new HelpEmbedView();
		slashCommands.add(new HelpCommandController(helpEmbedView));

		for (ISlashCommandController controller : slashCommands) {
			builder.addEventListeners(controller); // Tell JDA to listen to them
		}
		LOGGER.debug("Added {} slash commands", slashCommands.size());
	}

	private void addEventListeners(JDABuilder builder) {
		BotBaitView botBaitEmbedView = new BotBaitView();
		eventListeners.add(new BotBaitEventController(botBaitEmbedView, databaseManager));

		for (IEventListenerController eventListener : eventListeners) {
			builder.addEventListeners(eventListener);
		}
		LOGGER.debug("Added {} event listeners", eventListeners.size());
	}

	private void pushCommandsToGuild(Guild guild) {
		List<SlashCommandData> commandSetups = new ArrayList<>(slashCommands.size());
		for (ISlashCommandController controller : slashCommands) {
			commandSetups.add(controller.getCommandSetup());
		}

		guild.updateCommands().addCommands(commandSetups).queue();
	}
}