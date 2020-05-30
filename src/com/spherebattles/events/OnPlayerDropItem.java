package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.utils.MessageUtils;;

public class OnPlayerDropItem implements Listener {

	@EventHandler
	public void playerDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		PlayerInfo playerInfo = PlayerInfo.getInfo(player);
		
		Game game = Main.getInstance().games.get(playerInfo.getGame());
		if(game != null) {
			event.setCancelled(game.getEvents().onPlayerDropItem(player, playerInfo, event.getItemDrop().getItemStack()) || event.isCancelled());
		}
	}
}
