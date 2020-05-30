package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.utils.MessageUtils;

public class OnPlayerJoin implements Listener {

	@EventHandler
	public void playerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerInfo playerInfo = PlayerInfo.getInfo(player);
		
		Game[] array = Main.getInstance().games.values().toArray(new Game[] {});
		array[Main.getInstance().getRandom().nextInt(array.length)].getEvents().onPlayerJoin(player, playerInfo);
		
		event.setJoinMessage(playerInfo.getChatTag() + MessageUtils.m(" &7") + playerInfo.getDisplayName() + MessageUtils.m(" &fjoined."));
	}
}
