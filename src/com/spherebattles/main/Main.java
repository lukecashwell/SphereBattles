package com.spherebattles.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import com.spherebattles.commands.CommandRegestry;
import com.spherebattles.events.EventRegestry;


public class Main extends JavaPlugin {
	
	public static JavaPlugin plugin;
	public static File dataFile;
	
	
	@Override
	public void onEnable() {	
		new EventRegestry(this);
		new CommandRegestry(this);
		Main.plugin = this;	
		Main.dataFile = this.getDataFolder();
	}
	
	@Override
	public void onDisable() { 
		
	}
	
}
