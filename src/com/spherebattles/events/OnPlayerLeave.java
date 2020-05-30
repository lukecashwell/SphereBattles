package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.utils.MessageUtils;;

public class OnPlayerLeave implements Listener {

	@EventHandler
	public void playerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		PlayerInfo playerInfo = PlayerInfo.getInfo(player);
		
		Game game = Main.getInstance().games.get(playerInfo.getGame());
		if(game != null) {
			game.getEvents().onPlayerQuit(player, playerInfo);
		}
		
		event.setQuitMessage(playerInfo.getChatTag() + MessageUtils.m(" &7") + playerInfo.getDisplayName() + MessageUtils.m(" &fleft."));
		
		playerInfo.saveData();
		PlayerInfo.unloadPlayer(player);
	}
}
