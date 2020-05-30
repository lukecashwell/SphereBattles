package com.spherebattles.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegistry {

	public JavaPlugin plugin;
	
	public void registerEvents() {   
		register(ResetCommand.class, "Reset");
		register(WorldCommand.class, "World");
		register(RankCommand.class, "Rank");
		register(ListPlayers.class, "List");
		register(SetTagCommand.class, "Settag");
		
	}	
	
	public void register(Class<? extends CommandExecutor> listener, String name) {
		try {
			this.plugin.getCommand(name).setExecutor(listener.newInstance());
		} catch (Exception o_o) {}
	}
	
	public CommandRegistry(JavaPlugin plugin) {
		this.plugin = plugin;
		this.registerEvents();
	}	
}
