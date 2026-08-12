package com.GMRP;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.GMRP.core.databaseManager.DatabaseManager;
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
	static void main(String[] args) {
		// Initialize Models
		// GMRP Repo
		BotConfig.init();
		try {
			GitManager repositoryManager = new GitManager(".");
			DatabaseManager databaseManager = new DatabaseManager();
			databaseManager.testConnection();

			// Slash Commands
			List<SlashCommandController> slashCommands = new ArrayList<>();

			AboutEmbedView aboutEmbedView = new AboutEmbedView();
			slashCommands.add(new AboutCommandController(aboutEmbedView, repositoryManager, databaseManager));

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
			loops.add(new BotBaitEventController(botBaitEmbedView, databaseManager));

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
			Guild guild;
			try (Connection connection = databaseManager.getConnection();
					PreparedStatement preparedStatement = connection
							.prepareStatement("SELECT CONFIG_VALUE FROM CONFIG WHERE CONFIG_KEY = ?")) {
				preparedStatement.setString(1, "SERVER");
				try (ResultSet resultSet = preparedStatement.executeQuery()) {
					if (!resultSet.next())
						throw new RuntimeException("Guild ID not found in the database.");
					guild = jda.getGuildById(resultSet.getString(1));
				}
			}

			// Push the list of command setups to the guild
			guild.updateCommands().addCommands(commandSetups).queue();
			Runtime.getRuntime().addShutdownHook(new Thread(databaseManager::close));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}