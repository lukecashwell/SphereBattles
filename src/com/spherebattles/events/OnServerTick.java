package com.spherebattles.events;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.utils.MessageUtils;


public class OnServerTick {
	
	public void onTick() {
		
		Collection<Game> games = Main.getInstance().games.values();
		for(Game game : games) {
			game.getEvents().onTick();
		}
	}
}
