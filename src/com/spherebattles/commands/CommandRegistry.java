package com.spherebattles.commands;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandRegestry {

	public JavaPlugin plugin;
	
	public void regesterEvents() {   
		regester(ResetCommand.class, "Reset");
	}	
	
	public void regester(Class<? extends CommandExecutor> listener, String name) {
		try {
			this.plugin.getCommand(name).setExecutor(listener.newInstance());
		} catch (Exception o_o) {}
	}
	
	public CommandRegestry(JavaPlugin plugin) {
		this.plugin = plugin;
		this.regesterEvents();
	}	
}
