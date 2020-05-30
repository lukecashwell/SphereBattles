package com.spherebattles.utils;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageUtils {

	public static void errorMessage(Player player, String message) {
		player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5f, 2);
		player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l>> &f" + message));
	}
	
	public static void successMessage(Player player, String message) {
		player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 2);
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l>> &f" + message));
	}
	
	public static void broadcastMessage(Collection<? extends Player> players, String message) {
		for(Player player : players) player.sendMessage(m(message));
	}
	
	public static void broadcastSound(Collection<? extends Player> players, Sound sound) {
		for(Player player : players) player.playSound(player.getLocation(), sound, 1, 1);
	}
	
	public static void sendActionMessage(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(m(message)));
	} 
	
	public static void infoMessage(Player player, String message) {
		player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 0.5f, 2);
		player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l>> &f" + message));
	}
	
	public static void infoMessage_soundless(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l>> &f" + message));
	}
	
	public static String m(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
}
