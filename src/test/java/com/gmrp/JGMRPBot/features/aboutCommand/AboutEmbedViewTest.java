/* SPDX-License-Identifier: AGPL-3.0-or-later */
package com.gmrp.JGMRPBot.features.aboutCommand;

import net.dv8tion.jda.api.entities.MessageEmbed;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AboutEmbedViewTest {

	@Test
	void testFormatAboutEmbed() {
		// arrange
		AboutEmbedView view = new AboutEmbedView();
		// act
		MessageEmbed embed = view.formatAboutEmbed("A", "B", "C");
		// assert
		assertAll(
				() -> assertEquals("About GMRP", embed.getTitle()),
				() -> assertEquals(
						"# GeoFS Military Roleplay Bot\n\nOwned by A\n\n[Dashboard](https://bot.geo-mrp.com/)\nBranch: `B`\nVersion: `C`",
						embed.getDescription()),
				() -> assertEquals("Made by Denver", embed.getFooter().getText()),
				() -> assertEquals(embed.getColor(), java.awt.Color.BLUE));
	}
}
