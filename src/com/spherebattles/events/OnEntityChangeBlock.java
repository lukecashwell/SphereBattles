package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

public class OnEntityChangeBlock implements Listener {

	@EventHandler
	public void blockChange(EntityChangeBlockEvent event) {
		event.setCancelled(true);
	}	
}
