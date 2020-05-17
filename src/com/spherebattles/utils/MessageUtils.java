package com.spherebattles.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

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
	
	public static void infoMessage(Player player, String message) {
		player.playSound(player.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 0.5f, 2);
		player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l>> &f" + message));
	}
	
	public static void infoMessage_soundless(Player player, String message) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&l>> &f" + message));
	}
	
}
