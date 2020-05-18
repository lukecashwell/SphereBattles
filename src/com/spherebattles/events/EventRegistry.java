package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class EventRegistry {

	public JavaPlugin plugin;
	
	public void registerEvents() {
		register(OnBlockBreak.class);	
		register(OnBlockPlace.class);	
		register(OnBlockDamage.class);	
		register(OnBlockPhysicsUpdate.class);	
		register(OnServerLoad.class);	
	}	
	
	public void register(Class<? extends Listener> listener) {
		try {
			Bukkit.getPluginManager().registerEvents(listener.newInstance(), this.plugin);
		} catch (Exception o_o) {}
	}
	
	public EventRegistry(JavaPlugin plugin) {
		this.plugin = plugin;
		this.registerEvents();
	}	
}
