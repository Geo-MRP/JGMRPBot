/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.features.moderation.unverifiedCommand;

import com.GMRP.core.databaseManager.IDatabaseManager;
import com.GMRP.core.databaseManager.exception.DatabaseManagerException;
import com.GMRP.features.ISlashCommandController;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.utils.FileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class UnverifiedCommandController extends ListenerAdapter implements ISlashCommandController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UnverifiedCommandController.class);

	private final IDatabaseManager databaseManager;

	public UnverifiedCommandController(IDatabaseManager databaseManager) {
		this.databaseManager = databaseManager;
	}

	@Override
	public SlashCommandData getCommandSetup() {
		return Commands.slash("unverified", "get a list of unverified users who have been in the server");
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
		if (!event.getFullCommandName().equalsIgnoreCase("unverified"))
			return;
		if (event.getGuild() == null)
			return;

		MDC.put("command", "unverified");
		MDC.put("userId", event.getUser().getId());
		MDC.put("guildId", event.getGuild().getId());
		MDC.put("channelId", event.getChannel().getId());

		try {
			LOGGER.debug("Executing /unverified command");

			// TODO: defer reply

			String unverifiedRoleId;

			try {
				unverifiedRoleId = databaseManager.getConfigKey("UNVERIFIED");
			} catch (DatabaseManagerException e) {
				LOGGER.error("Failed to get unverified role ID", e);
				event.reply("Unverified Role ID not found in the database").setEphemeral(true).queue();
				return;
			}

			Role role = event.getGuild().getRoleById(unverifiedRoleId);
			event.getGuild().findMembersWithRoles(role)
					.onSuccess(list -> {
						ArrayList<Member> filteredMembers = new ArrayList<>();
						list.forEach(member -> {
							if (member.hasTimeJoined()) {
								if (!member.getTimeJoined().plusMonths(6).isAfter(OffsetDateTime.now())) {
									filteredMembers.add(member);
								}
							}
						});
						if (filteredMembers.isEmpty()) {
							event.reply("No unverified members found").setEphemeral(true).queue();
						} else {
							StringBuilder stringBuilder = new StringBuilder();
							filteredMembers.forEach((member) -> {
								stringBuilder.append(member.getId()).append("\n");
							});
							String fileContents = stringBuilder.toString();
							byte[] data = fileContents.getBytes(StandardCharsets.UTF_8);
							FileUpload file = FileUpload.fromData(data, "output.txt");
							event.reply(filteredMembers.size() + " unverified members found")
									.addFiles(file).queue();
						}

					})
					.onError(err -> {
						LOGGER.error("Failed to get unverified members", err);
						event.reply("Failed to get unverified members").setEphemeral(true).queue();
					});

		} finally {
			MDC.clear();
		}
	}
}
