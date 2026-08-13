/* SPDX-License-Identifier: AGPL-3.0-or-later */
package com.GMRP.views.shared;

import net.dv8tion.jda.api.EmbedBuilder;

public class BotEmbedBuilder {
	private static String avatarUrl;

	public static void setAvatarUrl(String avatarUrl) {
		BotEmbedBuilder.avatarUrl = avatarUrl;
	}

	// A static factory method
	public static EmbedBuilder create() {
		return new EmbedBuilder()
				// Universal Embed Format
				.setFooter("Made by Denver", avatarUrl)
				.setColor(java.awt.Color.BLUE);
	}

	// This is just a placeholder template for future reference
	public static EmbedBuilder createError() {
		return create()
				// Red Embed Color to represent something's gone wrong.
				.setColor(java.awt.Color.RED)
				// Error Embed Title
				.setTitle("❌ An Error Occurred");
	}
}