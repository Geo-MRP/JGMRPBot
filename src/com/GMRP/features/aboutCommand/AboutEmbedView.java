package com.GMRP.features.aboutCommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import com.GMRP.views.shared.BotEmbedBuilder;

public class AboutEmbedView {
    /// Generates the bot's about Embed
    public MessageEmbed formatAboutEmbed(String owner, String branchName) {
        EmbedBuilder embed = BotEmbedBuilder.create();
        
        embed.setTitle("About GMRP");
        embed.setDescription("# GeoFS Military Roleplay Bot\n\nOwned by "+owner+"\n\n[Dashboard](https://bot.geo-mrp.com/)\nBranch: `"+branchName+"`\nVersion: `V0`");//\n\n-# Powered by [Lighthouse](https://github.com/NickFury001/Lighthouse), Console API (S.H.I.E.L.D. Research and Development)");
        
        return embed.build();
    }
}