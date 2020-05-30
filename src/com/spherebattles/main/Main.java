package com.spherebattles.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.spherebattles.NBT.INBTBase;
import com.spherebattles.NBT.NBTTagCompound;
import com.spherebattles.NBT.NBTTagString;
import com.spherebattles.NBT.NBTTagStringArray;
import com.spherebattles.commands.CommandRegistry;
import com.spherebattles.events.EventRegistry;
import com.spherebattles.game.Game;
import com.spherebattles.game.VoidGenerator;
import com.spherebattles.ranks.RankRegistry;
import com.spherebattles.utils.DataTools;
import com.spherebattles.utils.MessageUtils;


public class Main extends JavaPlugin {
	
	private static Main plugin;
	
	public File dataFile;
	public HashMap<String, Game> games = new HashMap<>();
	public int gameCount = 0;
	private Random random;
	
	public List<String> completedGamePlayers;
	
	@Override
	public void onEnable() {	
		Main.plugin = this;
		this.random = new Random();
		this.dataFile = this.getDataFolder();
		INBTBase nbt = DataTools.readNBTFile("players.nbt").getTag("players");
		if(nbt != null) {
			this.completedGamePlayers = ((NBTTagStringArray)nbt).getAsStringList();
		}
		if(this.completedGamePlayers == null) {
			this.completedGamePlayers = new ArrayList<String>();
		}
		
	
		
		new EventRegistry(this);
		new CommandRegistry(this);
		new RankRegistry();
		
		WorldCreator creator = new WorldCreator("void");
		creator.generator(new VoidGenerator());
		World world = creator.createWorld();
		world.setDifficulty(Difficulty.PEACEFUL);
	
		new Game("main");	
	
		Scoreboard mainScore = Bukkit.getScoreboardManager().getMainScoreboard();
		
		if(mainScore.getTeam("red") == null) {
			mainScore.registerNewTeam("red");
		}
		Team redTeam = mainScore.getTeam("red");
		redTeam.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
		redTeam.setPrefix(MessageUtils.m("&c[RED] "));
		redTeam.setColor(ChatColor.RED);
		
		if(mainScore.getTeam("blue") == null) {
			mainScore.registerNewTeam("blue");
		}
		Team blueTeam = mainScore.getTeam("blue");
		blueTeam.setPrefix(MessageUtils.m("&9[BLUE] "));
		blueTeam.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
		blueTeam.setColor(ChatColor.BLUE);
	
	}
	
	@Override
	public void onDisable() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("players", new NBTTagStringArray(this.completedGamePlayers));
		DataTools.writeNBTFile("players.nbt", tag);
		for(Game game : games.values()) {
			game.delete();
		}
	}
	
	public static Main getInstance() {
		return Main.plugin;
	}
	
	public Random getRandom() {
		return this.random;
	}

	public void addPlayedPlayer(String name) {
		this.completedGamePlayers.add(name);		
	}
	
}
