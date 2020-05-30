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

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.utils.MessageUtils;


public class OnBlockBreak implements Listener {

	@EventHandler
	public void blockBreak(BlockBreakEvent event) {
		if(event.getBlock().getType().equals(Material.SPAWNER) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) event.setCancelled(true);
		if(event.getBlock().getType().equals(Material.GRAVEL)) { 
			event.setDropItems(false); 
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.FLINT, Main.getInstance().getRandom().nextInt(3) + 1));
		}
		Player player = event.getPlayer();
		PlayerInfo playerInfo = PlayerInfo.getInfo(player);
		Game game = Main.getInstance().games.get(playerInfo.getGame());
		if(game != null) {
			event.setDropItems(game.getEvents().onPlayerBreak(player, playerInfo, event.getBlock()) && event.isDropItems());
		}
	}
}
