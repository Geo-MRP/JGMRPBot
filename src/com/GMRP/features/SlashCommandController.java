package com.GMRP.features;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.hooks.EventListener;

public interface SlashCommandController extends EventListener {
    // Every controller must tell JDA what its command looks like
    SlashCommandData getCommandSetup(); 
}