package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.utils.MessageUtils;;

public class OnPlayerEnterNether implements Listener {

	@EventHandler
	public void playerLeave(PlayerTeleportEvent event) {
		if(event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)){
            event.setCancelled(true);
        }
	}
}
