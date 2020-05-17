package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EventRegestry {

	public JavaPlugin plugin;
	
	public void regesterEvents() {
		regester(OnBlockBreak.class);	
		regester(OnBlockPlace.class);	
		regester(OnBlockDamage.class);	
		regester(OnBlockPhysicsUpdate.class);	
		regester(OnServerLoad.class);	
	}	
	
	public void regester(Class<? extends Listener> listener) {
		try {
			Bukkit.getPluginManager().registerEvents(listener.newInstance(), this.plugin);
		} catch (Exception o_o) {}
	}
	
	public EventRegestry(JavaPlugin plugin) {
		this.plugin = plugin;
		this.regesterEvents();
	}	
}
