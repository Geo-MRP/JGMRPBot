package com.GMRP.features.moderation.botBait;

import com.GMRP.views.shared.BotEmbedBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class BotBaitView {
	public MessageEmbed formatBotBaitEmbed() {
		EmbedBuilder embed = BotEmbedBuilder.create();
		embed.setTitle("DO NOT SEND MESSAGES HERE")
				.setDescription(
						"This channel is intended to bait bots or hacked accounts into sending messages here, which will then trigger me into automaticly banning them, deleting recent messages, and alerting cabinet")
				.addField("What if I accidently do",
						"Contact a cabinet member and wait for them to check your message and unban you", true);
		return embed.build();
	}
}
