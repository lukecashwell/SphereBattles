package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;

public class OnBlockPlace implements Listener {

	@EventHandler
	public void blockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		PlayerInfo playerInfo = PlayerInfo.getInfo(player);
		Game game = Main.getInstance().games.get(playerInfo.getGame());
		if(game != null) {
			event.setCancelled(game.getEvents().onPlayerPlace(player, playerInfo, event.getBlock().getLocation(), event.getItemInHand()));
		}
	}
}
