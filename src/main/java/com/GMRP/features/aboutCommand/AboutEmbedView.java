/* SPDX-License-Identifier: AGPL-3.0-or-later */

package com.GMRP.features.aboutCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import com.GMRP.views.shared.BotEmbedBuilder;

public class AboutEmbedView {
	/// Generates the bot's about Embed
	public MessageEmbed formatAboutEmbed(String owner, String branchName, String version) {
		EmbedBuilder embed = BotEmbedBuilder.create();

		StringBuilder descriptionBuilder = new StringBuilder();
		descriptionBuilder.append("# GeoFS Military Roleplay Bot\n\nOwned by ").append(owner)
				.append("\n\n[Dashboard](https://bot.geo-mrp.com/)\nBranch: `").append(branchName)
				.append("`\nVersion: `").append(version).append("`");
		String description = descriptionBuilder.toString();

		embed.setTitle("About GMRP");
		embed.setDescription(description);

		return embed.build();
	}
}