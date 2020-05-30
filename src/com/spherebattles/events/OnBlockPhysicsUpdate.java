package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class OnBlockPhysicsUpdate implements Listener {

	@EventHandler
	public void blockUpdate(BlockPhysicsEvent event) {
		Material mat = event.getBlock().getType();
		if(mat.equals(Material.SAND) || mat.equals(Material.GRAVEL)) {
			event.setCancelled(true);
		}
	}
}
