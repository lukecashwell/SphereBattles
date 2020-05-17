package com.spherebattles.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemUtils {

	public static int IsSword(Material item) {
		int strength = -1;
		switch(item) {
		case DIAMOND_SWORD: { strength = 3; } break;
		case LEGACY_DIAMOND_SWORD: { strength = 3; } break;
		case GOLDEN_SWORD: { strength = 1; } break;
		case LEGACY_GOLD_SWORD: { strength = 1; } break;
		case IRON_SWORD: { strength = 2; } break;
		case LEGACY_IRON_SWORD: { strength = 2; } break;
		case STONE_SWORD: { strength = 1; } break;
		case LEGACY_STONE_SWORD: { strength = 1; } break;
		case WOODEN_SWORD: { strength = 0; } break;
		case LEGACY_WOOD_SWORD: { strength = 0; } break;
		}
		return strength;
	}
	
	public static int IsPickaxe(Material item) {
		int strength = -1;
		switch(item) {
		case DIAMOND_PICKAXE: { strength = 3; } break;
		case LEGACY_DIAMOND_PICKAXE: { strength = 3; } break;
		case GOLDEN_PICKAXE: { strength = 1; } break;
		case LEGACY_GOLD_PICKAXE: { strength = 1; } break;
		case IRON_PICKAXE: { strength = 2; } break;
		case LEGACY_IRON_PICKAXE: { strength = 2; } break;
		case STONE_PICKAXE: { strength = 1; } break;
		case LEGACY_STONE_PICKAXE: { strength = 1; } break;
		case WOODEN_PICKAXE: { strength = 0; } break;
		case LEGACY_WOOD_PICKAXE: { strength = 0; } break;
		}
		return strength;
	}
	
	public static int IsSpade(Material item) {
		int strength = -1;
		switch(item) {
		case DIAMOND_SHOVEL: { strength = 3; } break;
		case LEGACY_DIAMOND_SPADE: { strength = 3; } break;
		case GOLDEN_SHOVEL: { strength = 1; } break;
		case LEGACY_GOLD_SPADE: { strength = 1; } break;
		case IRON_SHOVEL: { strength = 2; } break;
		case LEGACY_IRON_SPADE: { strength = 2; } break;
		case STONE_SHOVEL: { strength = 1; } break;
		case LEGACY_STONE_SPADE: { strength = 1; } break;
		case WOODEN_SHOVEL: { strength = 0; } break;
		case LEGACY_WOOD_SPADE: { strength = 0; } break;
		}
		return strength;
	}	
	
	public static int IsAxe(Material item) {
		int strength = -1;
		switch(item) {
		case DIAMOND_AXE: { strength = 3; } break;
		case LEGACY_DIAMOND_AXE: { strength = 3; } break;
		case GOLDEN_AXE: { strength = 1; } break;
		case LEGACY_GOLD_AXE: { strength = 1; } break;
		case IRON_AXE: { strength = 2; } break;
		case LEGACY_IRON_AXE: { strength = 2; } break;
		case STONE_AXE: { strength = 1; } break;
		case LEGACY_STONE_AXE: { strength = 1; } break;
		case WOODEN_AXE: { strength = 0; } break;
		case LEGACY_WOOD_AXE: { strength = 0; } break;
		}
		return strength;
	}
	
	public static int getFortune(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		return meta.hasEnchant(Enchantment.LUCK) ? meta.getEnchantLevel(Enchantment.LUCK) : 0;
	}
}
