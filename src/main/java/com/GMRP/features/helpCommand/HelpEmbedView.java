/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.features.helpCommand;

import java.util.List;

import com.GMRP.views.shared.BotEmbedBuilder;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.Command;

public class HelpEmbedView {
	public MessageEmbed formatHelpEmbed(List<Command> commandList) {
		EmbedBuilder embed = BotEmbedBuilder.create();

		embed.setTitle("GMRP Help");
		StringBuilder descBuilder = new StringBuilder();
		descBuilder.append("**List of available commands:**\n");
		for (Command command : commandList) {
			descBuilder.append(command.getAsMention()).append(": ").append(command.getDescription()).append("\n");
		}
		String description = descBuilder.toString();

		embed.setDescription(description);

		return embed.build();
	}
}
