package com.GMRP.features.aboutCommand;

import net.dv8tion.jda.api.EmbedBuilder;
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
                () -> assertEquals(embed.getTitle(), "About GMRP"),
                () -> assertEquals(embed.getDescription(), "# GeoFS Military Roleplay Bot\n\nOwned by A\n\n[Dashboard](https://bot.geo-mrp.com/)\nBranch: `B`\nVersion: `C`"),
                () -> assertEquals(embed.getFooter().getText(), "Made by Denver"),
                () -> assertEquals(embed.getColor(), java.awt.Color.BLUE)
        );
    }
}
