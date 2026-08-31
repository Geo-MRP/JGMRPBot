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
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

public class UnverifiedCommandController extends ListenerAdapter implements ISlashCommandController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UnverifiedCommandController.class);
	private static final Period BUFFER = Period.ofMonths(6);

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

			event.deferReply(true).queue();

			String unverifiedRoleId;
			try {
				unverifiedRoleId = databaseManager.getConfigKey("UNVERIFIED");
			} catch (DatabaseManagerException e) {
				LOGGER.error("Failed to get unverified role ID", e);
				event.getHook().sendMessage("Unverified Role ID not found in the database").queue();
				return;
			}

			Role role = event.getGuild().getRoleById(unverifiedRoleId);
			if (role == null) {
				event.getHook().sendMessage("Unverified role not found in the guild").queue();
				return;
			}

			event.getGuild().findMembersWithRoles(role)
					.onSuccess(list -> {
						List<Member> filteredMembers = getUnverifiedMembers(list);

						if (filteredMembers.isEmpty()) {
							event.getHook().sendMessage("No unverified members found").queue();
							return;
						}

						byte[] data = createFileContents(filteredMembers);
						FileUpload file = FileUpload.fromData(data, "output.txt");

						event.getHook().sendMessage(filteredMembers.size() + " unverified members found")
								.addFiles(file)
								.queue();

					})
					.onError(err -> {
						LOGGER.error("Failed to get unverified members", err);
						event.getHook().sendMessage("Failed to get unverified members").queue();
					});

		} finally {
			MDC.clear();
		}
	}

	private List<Member> getUnverifiedMembers(List<Member> members) {
		OffsetDateTime cutoff = OffsetDateTime.now().minus(BUFFER);
		return members.stream()
				.filter(Member::hasTimeJoined)
				.filter(member -> member.getTimeJoined().isBefore(cutoff))
				.toList();
	}

	private byte[] createFileContents(List<Member> members) {
		return members.stream()
				.map(Member::getId)
				.collect(Collectors.joining("\n"))
				.getBytes(StandardCharsets.UTF_8);
	}
}
