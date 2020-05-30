package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;

public class OnPlayerDamage implements Listener {

	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent  event) {
		if(event.getEntity() instanceof Player) {
			if(event.getDamager() instanceof Player) {
				Player victim = (Player)event.getEntity();
				Player killer = (Player)event.getDamager();
				PlayerInfo victimInfo = PlayerInfo.getInfo(victim);
				PlayerInfo killerInfo = PlayerInfo.getInfo(killer);
				Game game = Main.getInstance().games.get(victimInfo.getGame());
				if(game != null) {
					event.setCancelled(game.getEvents().onPlayerDamagePlayer(killer, killerInfo, victim, victimInfo) || event.isCancelled());
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent  event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player)event.getEntity();
			PlayerInfo playerInfo = PlayerInfo.getInfo(player);
			
			Game game = Main.getInstance().games.get(playerInfo.getGame());
			if(game != null) {
				event.setCancelled(game.getEvents().onPlayerTakeDamage(player, playerInfo) || event.isCancelled());
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if(event.getEntity() instanceof Player) {
			Player victim = (Player)event.getEntity();
			Player killer = victim.getKiller();
			if(killer != null) {
				PlayerInfo victimInfo = PlayerInfo.getInfo(victim);
				PlayerInfo killerInfo = PlayerInfo.getInfo(killer);
				Game game = Main.getInstance().games.get(victimInfo.getGame());
				if(game != null) {
					game.getEvents().onPlayerKillPlayer(killer, killerInfo, victim, victimInfo);
				}
			} else {
				PlayerInfo victimInfo = PlayerInfo.getInfo(victim);
				Game game = Main.getInstance().games.get(victimInfo.getGame());
				if(game != null) {
					game.getEvents().onPlayerDeath(victim, victimInfo);
				}
				
			}
		}
	}
}
