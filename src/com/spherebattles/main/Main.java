package com.spherebattles.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import com.spherebattles.commands.CommandRegistry;
import com.spherebattles.events.EventRegistry;


public class Main extends JavaPlugin {
	
	private static Main plugin;
	
	public File dataFile;
	
	
	@Override
	public void onEnable() {	
		this.dataFile = this.getDataFolder();
		
		new EventRegistry(this);
		new CommandRegistry(this);
		
		Main.plugin = this;	
	}
	
	@Override
	public void onDisable() { 
		
	}
	
	public static Main getInstance() {
		return Main.plugin;
	}
	
}
