package com.spherebattles.events;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.spherebattles.utils.MessageUtils;


public class OnBlockBreak implements Listener {

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		MessageUtils.successMessage(event.getPlayer(), "Success!! you broke a block!");
	}
}
