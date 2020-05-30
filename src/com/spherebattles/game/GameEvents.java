package com.spherebattles.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.players.PlayerInfo.Team;
import com.spherebattles.utils.MessageUtils;

import net.minecraft.server.v1_15_R1.Vector3f;

public class GameEvents {
	
	private final Game game;
	private List<Player> players = new ArrayList<>();
	
	private Set<Player> redTeamPlayers = null;
	private Set<Player> redTeamPergatory = null;
	private boolean redSpawnPlaced = false;
	private boolean redSpawnExists = false;
	
	private Set<Player> blueTeamPlayers = null;
	private Set<Player> blueTeamPergatory = null; 
	private boolean blueSpawnPlaced = false;
	private boolean blueSpawnExists = false;
	
	private int gameId;
	
	private int interGameTimer;
	
	private int secondTimer;
	private int startTimer;
	
	private int phase1Timer;
	private int phase2Timer;
	
	private GameState state;
	private Phase phase;
	
	private static final int START_PLAYER_COUNT = 4;
	private static final int COUNT_DOWN_SECONDS = 30;
	
	private static final int FIRST_PHASE_TIME = 150;
	
	private static final int WAITING_TIME = 120;
	
	enum Phase {
		RESOURCE,
		PVP,
	}
	
	enum GameState {
		WAITING,
		GENERATING,
		STARTING,
		PLAYING
	}
	
	public GameEvents(final Game game) {
		this.game = game;
		this.state = GameState.WAITING;
		this.secondTimer = 0;
		this.startTimer = 0;
		this.interGameTimer = 0;
		this.gameId = Main.getInstance().getRandom().nextInt(10000);
		this.redTeamPlayers = new HashSet<>();
		this.redTeamPergatory = new HashSet<>();
		this.blueTeamPlayers = new HashSet<>();
		this.blueTeamPergatory = new HashSet<>();
	}
	
	public void onTick() {
		secondTimer++;
		if(secondTimer >= 20) {
			secondTimer = 0;
			
			if(this.interGameTimer > 0) {
				this.interGameTimer--;
			}
			
			if(this.players.size() >= START_PLAYER_COUNT) {
				if(this.state == GameState.STARTING) {
					if(this.startTimer == COUNT_DOWN_SECONDS) {
						MessageUtils.broadcastMessage(this.players, "&b&l>> &d" + COUNT_DOWN_SECONDS + "&f seconds till the begining of the game!");
						MessageUtils.broadcastSound(this.players, Sound.BLOCK_NOTE_BLOCK_HAT);
					}
					
					if(this.startTimer == COUNT_DOWN_SECONDS/2) {
						MessageUtils.broadcastMessage(this.players, "&b&l>> &d" + this.startTimer + "&f seconds!");
						MessageUtils.broadcastSound(this.players, Sound.BLOCK_NOTE_BLOCK_HAT);
					}
					
					if(this.startTimer == COUNT_DOWN_SECONDS/3) {
						MessageUtils.broadcastMessage(this.players, "&b&l>> &d" + this.startTimer + "&f seconds!");
						MessageUtils.broadcastSound(this.players, Sound.BLOCK_NOTE_BLOCK_HAT);
					}
					
					if(this.startTimer <= 0) {
						MessageUtils.broadcastMessage(this.players, "&b&l>> &fThe game has begun!");
					
						this.gameId = Main.getInstance().getRandom().nextInt(10000);
						
						Random random = Main.getInstance().getRandom();
						List<Player> redTeamPlayers = new ArrayList<>();
						this.blueTeamPlayers = new HashSet<>();
						for(Player player : players) {
							redTeamPlayers.add(player);
						}
						
						while(redTeamPlayers.size() > players.size()/2) {
							int randomIndex = random.nextInt(redTeamPlayers.size());
							blueTeamPlayers.add(redTeamPlayers.remove(randomIndex));
						}
						
						this.redTeamPlayers = new HashSet<>(redTeamPlayers);	
						
						for(Player red : this.redTeamPlayers) {
							red.setHealth(20);
							red.setFireTicks(0);
							red.setGameMode(GameMode.SURVIVAL);
							red.getInventory().clear();
							red.getActivePotionEffects().clear();
							red.setFallDistance(0);
							red.setLevel(0);
							red.setFoodLevel(20);
							red.setSaturation(10);
							red.setAbsorptionAmount(0);
							red.setExp(0);
							red.setTotalExperience(0);
							
							red.getInventory().addItem(new ItemStack(Material.RED_WOOL, 64));
							red.teleport(game.getRedTeamPlayerSpawn());
							MessageUtils.infoMessage(red, "Kill all those &1dirty blues &fand you shall win!");
							PlayerInfo info = PlayerInfo.getInfo(red);
							info.setTeam(Team.RED);
							info.setPastGameID(this.gameId);
							
							this.giveStartingItems(red);
						
						}
						
						for(Player blue : this.blueTeamPlayers) {
							blue.setHealth(20);
							blue.setFireTicks(0);
							blue.setGameMode(GameMode.SURVIVAL);
							blue.getInventory().clear();
							blue.getActivePotionEffects().clear();
							blue.setFallDistance(0);
							blue.setLevel(0);
							blue.setFoodLevel(20);
							blue.setSaturation(10);
							blue.setAbsorptionAmount(0);
							blue.setExp(0);
							blue.setTotalExperience(0);
							
							blue.getInventory().addItem(new ItemStack(Material.BLUE_WOOL, 64));
							blue.teleport(game.getBlueTeamPlayerSpawn());
							MessageUtils.infoMessage(blue, "Kill all those &4bloody reds &fand you shall win!");
							PlayerInfo info = PlayerInfo.getInfo(blue);
							info.setTeam(Team.BLUE);
							info.setPastGameID(this.gameId);
							
							this.giveStartingItems(blue);
							
						}
						
						this.blueSpawnPlaced = true;
						this.blueSpawnExists = true;
						
						this.redSpawnPlaced = true;
						this.redSpawnExists = true;
						
						this.redTeamPergatory = new HashSet<>();
						this.blueTeamPergatory = new HashSet<>();
						
						this.state = GameState.PLAYING;
						this.phase = Phase.RESOURCE;
						this.phase1Timer = FIRST_PHASE_TIME;
					} else
					if(this.startTimer <= 5) {
						MessageUtils.broadcastMessage(this.players, "&b&l>> &d" + this.startTimer + "&f seconds!");
						MessageUtils.broadcastSound(this.players, Sound.BLOCK_NOTE_BLOCK_HAT);
					}
					
				
					this.startTimer--;
				}
			} else {
				if(this.state == GameState.STARTING) {
					MessageUtils.broadcastMessage(this.players, "&c&l>> &fNot enough player's avaialble.");
					this.state = GameState.WAITING;
				}
			}
			if(this.state == GameState.PLAYING) {
				if(this.phase == Phase.RESOURCE) {
					if(this.phase1Timer == FIRST_PHASE_TIME) {
						MessageUtils.broadcastMessage(this.players, "&b&l>> &d" + convertSecondsToString(this.phase1Timer) + "&f seconds until pvp!");
						MessageUtils.broadcastSound(this.players, Sound.BLOCK_NOTE_BLOCK_HAT);		
					}
					if(this.phase1Timer == 30) {
						MessageUtils.broadcastMessage(this.players, "&b&l>> &d" + convertSecondsToString(this.phase1Timer) + "&f seconds!");
						MessageUtils.broadcastSound(this.players, Sound.BLOCK_NOTE_BLOCK_HAT);		
					}
					if(this.phase1Timer == 0) {
						MessageUtils.broadcastMessage(this.players, "&b&l>> &dPvp &fEnabled!");
						MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);		
						this.phase2Timer = 0;
						this.phase = Phase.PVP;
					}
					
					this.phase1Timer--;
				}
				if(this.phase == Phase.PVP) {
					this.phase2Timer++;
					if(players.size() <= 0) {
						this.state = GameState.WAITING;
						game.delete();
					}
					if(blueTeamPlayers.size() <= 0) {
						MessageUtils.broadcastMessage(this.players, "&c&l>> &cRed Team &fhas won the &7SphereBattle&f in &d " + convertSecondsToString(this.phase2Timer) + "&f!");
						this.reset();
					} else
					if(redTeamPlayers.size() <= 0) {
						MessageUtils.broadcastMessage(this.players, "&c&l>> &9Blue Team &fhas won the &7SphereBattle&f in &d " + convertSecondsToString(this.phase2Timer) + "&f!");
						this.reset();
					}
				}
			}
			
		}
		if(this.players.size() >= START_PLAYER_COUNT && state == GameState.WAITING && this.interGameTimer <= 0) {
			this.state = GameState.GENERATING;
			MessageUtils.broadcastMessage(this.players, "&b&l>> &fGenerating game world (this lags a bit.)");
			game.generateWorld(() -> {
				this.state = GameState.STARTING;
				this.startTimer = COUNT_DOWN_SECONDS;
			});
		}
		
		if(this.blueSpawnPlaced) this.game.getBlueTeamSpawn().getBlock().setType(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
		if(this.redSpawnPlaced) this.game.getRedTeamSpawn().getBlock().setType(Material.ORANGE_GLAZED_TERRACOTTA);
		
		for(Player player : this.players) {
			PlayerInfo playerInfo = PlayerInfo.getInfo(player);
			this.onPlayerTick(player, playerInfo);
		}
		
	}	
	
	public void onPlayerTick(Player player, PlayerInfo playerInfo) {
		if(this.state == GameState.PLAYING) {
			if(this.phase == Phase.RESOURCE) {
				MessageUtils.sendActionMessage(player, "&cReds: " + getBoolString(this.redSpawnPlaced, this.redSpawnExists) + " &7: &d" + this.redTeamPlayers.size() + " &9Blues: " + getBoolString(this.blueSpawnPlaced, this.blueSpawnExists) + " &7: &d" + this.blueTeamPlayers.size() + " &ePhase: &8Gathering &fTime: &7" + convertSecondsToString(this.phase1Timer));
				if(player.getLocation().getY() < 0) {
					player.setFallDistance(0);
					if(playerInfo.getTeam() == Team.RED) {
						player.teleport(this.game.getRedTeamPlayerSpawn());
					}
					if(playerInfo.getTeam() == Team.BLUE) {
						player.teleport(this.game.getBlueTeamPlayerSpawn());
					}
				}
 			} else {
 				MessageUtils.sendActionMessage(player, "&cReds: " + getBoolString(this.redSpawnPlaced, this.redSpawnExists) + " &7: &d" + this.redTeamPlayers.size() + " &9Blues: " + getBoolString(this.blueSpawnPlaced, this.blueSpawnExists) + " &7: &d" + this.blueTeamPlayers.size() + " &ePhase: &aPvp &fTime: &7" + convertSecondsToString(this.phase2Timer));			
 			}
			
			{
				Location loc = player.getLocation();
				if(loc.getX() >= Game.DISTANCE_BETWEEN_TEAMS/2 + 100) {
					loc.setX(Game.DISTANCE_BETWEEN_TEAMS/2 + 100 - 1);
					player.teleport(loc);
				}
				if(loc.getX() <= -(Game.DISTANCE_BETWEEN_TEAMS/2 + 100)) {
					loc.setX(-(Game.DISTANCE_BETWEEN_TEAMS/2 + 100 - 1));
					player.teleport(loc);
				}
				if(loc.getZ() >= Game.DISTANCE_BETWEEN_TEAMS/4) {
					loc.setZ(Game.DISTANCE_BETWEEN_TEAMS/4 - 1);
					player.teleport(loc);
				}
				if(loc.getZ() <= -(Game.DISTANCE_BETWEEN_TEAMS/4)) {
					loc.setZ(-(Game.DISTANCE_BETWEEN_TEAMS/4 - 1));
					player.teleport(loc);
				}
				
			}
			
			Inventory inventory = player.getInventory();
			if(playerInfo.getTeam() == Team.BLUE) {
				boolean hasItem = false;
				ItemStack itemStack = null;
				for(ItemStack item : inventory.getContents()) {
					if(item != null)
						if(this.game.isBlueSpawnItem(item)) {
							itemStack = item;
							hasItem = true;
							
						} else
						if(this.game.isRedSpawnItem(item)) {
							inventory.remove(item);
						}
				}
				
				if(!hasItem || this.blueTeamPergatory.contains(player)) {
					inventory.remove(Material.ORANGE_GLAZED_TERRACOTTA);
					inventory.remove(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
					if(player.getInventory().getItemInOffHand().getType()==Material.ORANGE_GLAZED_TERRACOTTA)player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
					if(player.getInventory().getItemInOffHand().getType()==Material.LIGHT_BLUE_GLAZED_TERRACOTTA)player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
					if(playerInfo.doesHaveTeamSpawn()) {
						this.blueSpawnPlaced = true;
						inventory.remove(itemStack);
						playerInfo.setDoesHaveTeamSpawn(false);
						MessageUtils.broadcastMessage(this.players, "&d>> &fThe &9Blue Team Spawn &fhas been re-placed!");
						MessageUtils.broadcastSound(this.players, Sound.BLOCK_BELL_USE);
						for(Player p : this.blueTeamPergatory) {
							PlayerInfo pi = PlayerInfo.getInfo(p);
							Location loc = this.onPlayerRespawn(p, pi);
							p.teleport(loc);
						}
						this.blueTeamPergatory.clear();
					
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
							this.game.getBlueTeamSpawn().getBlock().setType(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
						}, 1);
					}
				} else {
					if(!playerInfo.doesHaveTeamSpawn()) {
						inventory.remove(itemStack);
					}
				}
			}
			if(playerInfo.getTeam() == Team.RED) {
				boolean hasItem = false;
				ItemStack itemStack = null;
				for(ItemStack item : inventory.getContents()) {
					if(this.game.isRedSpawnItem(item)) {
						itemStack = item;
						hasItem = true;
						break;
					}
					if(this.game.isBlueSpawnItem(item)) {
						inventory.remove(item);
					}
				}

				{
					ItemStack item = player.getInventory().getItemInOffHand();
					if(item != null)
						if(this.game.isRedSpawnItem(item)) {
							itemStack = item;
							hasItem = true;
						
						} else
						if(this.game.isBlueSpawnItem(item)) {
							inventory.remove(item);
						} else {
							if(player.getInventory().getItemInOffHand().getType()==Material.ORANGE_GLAZED_TERRACOTTA)player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
							if(player.getInventory().getItemInOffHand().getType()==Material.LIGHT_BLUE_GLAZED_TERRACOTTA)player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
						}
				}
				
				if(!hasItem || this.redTeamPergatory.contains(player)) {
					inventory.remove(Material.ORANGE_GLAZED_TERRACOTTA);
					inventory.remove(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
					if(player.getInventory().getItemInOffHand().getType()==Material.ORANGE_GLAZED_TERRACOTTA)player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
					if(player.getInventory().getItemInOffHand().getType()==Material.LIGHT_BLUE_GLAZED_TERRACOTTA)player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
					
					if(playerInfo.doesHaveTeamSpawn()) {
						inventory.remove(itemStack);
						this.redSpawnPlaced = true;
						playerInfo.setDoesHaveTeamSpawn(false);
						MessageUtils.broadcastMessage(this.players, "&d>> &fThe &cRed Team Spawn &fhas been re-placed!");
						MessageUtils.broadcastSound(this.players, Sound.BLOCK_BELL_USE);
						for(Player p : this.redTeamPergatory) {
							PlayerInfo pi = PlayerInfo.getInfo(p);
							Location loc = this.onPlayerRespawn(p, pi);
							p.teleport(loc);
						}
						this.redTeamPergatory.clear();
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
							this.game.getRedTeamSpawn().getBlock().setType(Material.ORANGE_GLAZED_TERRACOTTA);
						}, 1);
					}
				} else {
					if(!playerInfo.doesHaveTeamSpawn()) {
						inventory.remove(itemStack);
					}
				}
			}
	
			
		}
	}
	
	public void onPlayerJoin(Player player, PlayerInfo playerInfo) { 
		this.players.add(player);
		playerInfo.setGame(this.game.getName());
		
		if(this.state != GameState.PLAYING) {
			World world = Bukkit.getWorld("world");
			Location loc = new Location(world, 0, 255, 0);
			for(int i = 255; i >= 0; i--) {
				loc.subtract(0, 1, 0);
				if(!loc.getBlock().isEmpty()) {
					loc.add(0, 1, 0);
					break;
				}
			}
			
			player.teleport(loc);
			player.setHealth(20);
			player.setFireTicks(0);
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.getActivePotionEffects().clear();
			player.setFallDistance(0);
			player.setLevel(0);
			player.setFoodLevel(20);
			player.setSaturation(10);
			player.setAbsorptionAmount(0);
			player.setExp(0);
			player.setTotalExperience(0);
			MessageUtils.broadcastMessage(this.players, playerInfo.getChatTag() + " &7" + playerInfo.getDisplayName() + " &e(" + players.size() + "/" + START_PLAYER_COUNT + ")"); 
		} else {
			
			if(playerInfo.getPastGameID() != this.gameId || (playerInfo.getPastGameID() == this.gameId && playerInfo.getPastTeam() != Team.NONE)) {
				Team team = findLoosingTeam();
				if (playerInfo.getPastGameID() == this.gameId && playerInfo.getPastTeam() != Team.NONE) {
					team = playerInfo.getPastTeam();
				}
				String addon = null;
				if(team == Team.BLUE) addon = "&9Blue Team&f.";
				if(team == Team.RED) addon = "&cRed Team&f.";
				
				player.setHealth(20);
				player.setFireTicks(0);
				player.setGameMode(GameMode.SURVIVAL);
				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.setFallDistance(0);
				player.setLevel(0);
				player.setFoodLevel(20);
				player.setSaturation(10);
				player.setAbsorptionAmount(0);
				player.setExp(0);
				player.setTotalExperience(0);
				PlayerInfo.getInfo(player).setTeam(team);
				
				if(team == Team.BLUE) {
					this.blueTeamPlayers.add(player);
					player.getInventory().addItem(new ItemStack(Material.BLUE_WOOL, 64));
					player.teleport(game.getBlueTeamPlayerSpawn());
					MessageUtils.infoMessage(player, "Kill all those &4bloody reds &fand you shall win!");
				}
				
				if(team == Team.RED) {
					this.redTeamPlayers.add(player);
					player.getInventory().addItem(new ItemStack(Material.RED_WOOL, 64));
					player.teleport(game.getRedTeamPlayerSpawn());
					MessageUtils.infoMessage(player, "Kill all those &1dirty blues &fand you shall win!");
				}
				
				giveStartingItems(player);
				
				MessageUtils.broadcastMessage(this.players, playerInfo.getChatTag() + " &7" + playerInfo.getDisplayName() + " &fHas joined " + addon); 	
			} else {
				player.teleport(Main.getInstance().getRandom().nextBoolean() ? this.game.getBlueTeamPlayerSpawn() : this.game.getRedTeamPlayerSpawn());
				player.setHealth(20);
				player.setFireTicks(0);
				player.setGameMode(GameMode.SPECTATOR);
				player.getInventory().clear();
				player.getActivePotionEffects().clear();
				player.setFallDistance(0);
				player.setLevel(0);
				player.setFoodLevel(20);
				player.setSaturation(10);
				player.setAbsorptionAmount(0);
				player.setExp(0);
				player.setTotalExperience(0);
			}
		}
		
		player.getInventory().clear();
	}
	
	public String onPlayerChat(Player player, PlayerInfo playerInfo, final StringBuilder message) {
		if(this.state == GameState.PLAYING) {
			if(playerInfo.getTeam() == Team.RED) {
				if(message.charAt(0) == '!') {
					message.replace(0, 1, "");
					return "&c";
				} else {
					StringBuilder m = new StringBuilder();
					m
					.append(MessageUtils.m("&4 £ &c[&4Team&c]"))
					.append(playerInfo.getChatTag())
					.append(MessageUtils.m("&c "))
					.append(playerInfo.getDisplayName())
					.append(MessageUtils.m("&f: "))
					.append(message);
					String newMessage = m.toString();
					for(Player red : this.redTeamPlayers) {
						red.sendMessage(newMessage);
					}
					return null;
				}
			} else if(playerInfo.getTeam() == Team.BLUE) {
				if(message.charAt(0) == '!') {
					message.replace(0, 1, "");
					return "&9";
				} else {
					StringBuilder m = new StringBuilder();
					m
					.append(MessageUtils.m("&1 £ &9[&1Team&9]"))
					.append(playerInfo.getChatTag())
					.append(MessageUtils.m("&9 "))
					.append(playerInfo.getDisplayName())
					.append(MessageUtils.m("&f: "))
					.append(message);
					String newMessage = m.toString();
					for(Player blue : this.blueTeamPlayers) {
						blue.sendMessage(newMessage);
					}
					return null;
				}
			}
		}
		return "";
	}
	
	public void onPlayerQuit(Player player, PlayerInfo playerInfo) {
		if(playerInfo.doesHaveTeamSpawn()) {
			if(playerInfo.getTeam() == Team.RED) {
				this.redSpawnPlaced = true;
				playerInfo.setDoesHaveTeamSpawn(false);
				MessageUtils.broadcastMessage(this.players, "&d>> &fThe &cRed Team Spawn &fhas been re-placed!");
				MessageUtils.broadcastSound(this.players, Sound.BLOCK_BELL_USE);
				for(Player p : this.redTeamPergatory) {
					PlayerInfo pi = PlayerInfo.getInfo(p);
					Location loc = this.onPlayerRespawn(p, pi);
					player.teleport(loc);
				}
				this.redTeamPergatory.clear();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					this.game.getBlueTeamSpawn().getBlock().setType(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
				}, 1);

			}
			if(playerInfo.getTeam() == Team.BLUE) {
				this.blueSpawnPlaced = true;
				playerInfo.setDoesHaveTeamSpawn(false);
				MessageUtils.broadcastMessage(this.players, "&d>> &fThe &9Blue Team Spawn &fhas been re-placed!");
				MessageUtils.broadcastSound(this.players, Sound.BLOCK_BELL_USE);
				for(Player p : this.blueTeamPergatory) {
					PlayerInfo pi = PlayerInfo.getInfo(p);
					Location loc = this.onPlayerRespawn(p, pi);
					p.teleport(loc);
				}
				this.blueTeamPergatory.clear();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					this.game.getBlueTeamSpawn().getBlock().setType(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
				}, 1);

			}
		}
		this.players.remove(player);
		this.blueTeamPlayers.remove(player);
		this.redTeamPlayers.remove(player);
		this.blueTeamPergatory.remove(player);
		this.redTeamPergatory.remove(player);
	}
	
	public boolean onPlayerTakeDamage(Player player, PlayerInfo playerInfo) {
		boolean isCancled = false;
		if(this.state != GameState.PLAYING || this.phase == Phase.RESOURCE) isCancled = true;		
		return isCancled;
	}
	
	public boolean onPlayerDamagePlayer(Player damager, PlayerInfo damagerInfo, Player victim, PlayerInfo victimInfo) {
		boolean isCancled = false;
		if(damagerInfo.getTeam() == victimInfo.getTeam()) {
			MessageUtils.errorMessage(damager, "Stop trying to kill your fellow teammate!");	
			isCancled = true;
		}		
		return isCancled;
	}
	
	public void onPlayerKillPlayer(final Player damager, final PlayerInfo damagerInfo, final Player victim, final PlayerInfo victimInfo) {
		String message = "";
		
		String damagerColor = damagerInfo.getTeam() == Team.BLUE ? " &9" : " &7";
		damagerColor = damagerInfo.getTeam() == Team.RED ? " &c" : damagerColor;
		
		String victimColor = victimInfo.getTeam() == Team.BLUE ? " &9" : " &7";
		victimColor = victimInfo.getTeam() == Team.RED ? " &c" : victimColor;
		
		String damagerName = damagerInfo.getChatTag() + damagerColor + damagerInfo.getDisplayName();
		String victimName = victimInfo.getChatTag() + victimColor + victimInfo.getDisplayName();
		
		Random random = Main.getInstance().getRandom();
		int option = random.nextInt(5);
		switch(option) {
			case 0:{
				message = damagerName + " &fhas killed " + victimName + "&f.";
			}break;
			case 1:{	
				message = victimName + " &fhas failed their team due to the hands of " + damagerName + "&f.";
			}break;
			case 2:{
				message = damagerName + " &fapplied death to " + victimName + "&f.";
			}break;
			case 3:{
				message = damagerName + " &fcaused " + victimName + " &fto die.";
			}break;
			case 4:{
				message = damagerName + " &fdestroyed the name of " + victimName + "&f.";
			}break;
		}
		
		if(victimInfo.getTeam() == Team.BLUE) {
			if(!this.blueSpawnPlaced) {
				if(this.blueSpawnExists) {
					this.blueTeamPergatory.add(victim);
					if(this.blueTeamPergatory.size() - this.blueTeamPlayers.size() == 0) {
						for(Player player : this.blueTeamPlayers) {
							PlayerInfo playerInfo = PlayerInfo.getInfo(player);
					
							String playerColor = playerInfo.getTeam() == Team.BLUE ? " &9" : " &7";
							playerColor = playerInfo.getTeam() == Team.RED ? " &c" : playerColor;
							
							String playerName = playerInfo.getChatTag() + playerColor + playerInfo.getDisplayName();
						
							
							playerInfo.resetTeam();
						
							MessageUtils.broadcastMessage(this.players, "&4&l>> " + playerName + " &fhas been eliminated.");
							MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
						}
						this.blueTeamPlayers.clear();
					}
				} else {
					this.blueTeamPlayers.remove(victim);
					
					victimInfo.resetTeam();
						
					MessageUtils.broadcastMessage(this.players, "&4&l>> " + victimName + " &fhas been eliminated.");
					MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);				
				}
			}
		}
		if(victimInfo.getTeam() == Team.RED) {
			if(!this.redSpawnPlaced) {
				if(this.redSpawnExists) {
					this.redTeamPergatory.add(victim);	
					if(this.redTeamPergatory.size() - this.redTeamPlayers.size() == 0) {
						for(Player player : this.redTeamPlayers) {
							PlayerInfo playerInfo = PlayerInfo.getInfo(player);
							
							String playerColor = playerInfo.getTeam() == Team.BLUE ? " &9" : " &7";
							playerColor = playerInfo.getTeam() == Team.RED ? " &c" : playerColor;
							
							String playerName = playerInfo.getChatTag() + playerColor + playerInfo.getDisplayName();
						
							
							playerInfo.resetTeam();
						
							MessageUtils.broadcastMessage(this.players, "&4&l>> " + playerName + " &fhas been eliminated.");
							MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
						}
						this.redTeamPlayers.clear();
					}
				} else {
					this.redTeamPlayers.remove(victim);
		
					victimInfo.resetTeam();
				
					MessageUtils.broadcastMessage(this.players, "&4&l>> " + victimName + " &fhas been eliminated.");
					MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);	
				}
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
			victim.spigot().respawn();	
		}, 1);
		MessageUtils.broadcastMessage(this.players, "&4&l>> " + message);
	}
	
	public Location onPlayerRespawn(Player player, PlayerInfo playerInfo) {
		Location respawnLoc = new Location(Bukkit.getWorld("world"), 0, 255, 0);
		if(state == GameState.PLAYING) {
			if(playerInfo.getTeam() == Team.NONE || this.redTeamPergatory.contains(player) || this.blueTeamPergatory.contains(player)) {
				if(playerInfo.getPastTeam() == Team.RED) {
					respawnLoc = this.game.getRedTeamPlayerSpawn();
				}
				if(playerInfo.getPastTeam() == Team.BLUE) {
					respawnLoc = this.game.getBlueTeamPlayerSpawn();
				}
				
				player.setFireTicks(0);
				player.setGameMode(GameMode.SPECTATOR);
				player.getActivePotionEffects().clear();
				player.setFallDistance(0);
				player.setLevel(0);
				player.setFoodLevel(20);
				player.setSaturation(10);
				player.setAbsorptionAmount(0);
				player.setExp(0);
				player.setTotalExperience(0);
			} else {
				if(playerInfo.getTeam() == Team.RED) {
					respawnLoc = this.game.getRedTeamPlayerSpawn();
				}
				if(playerInfo.getTeam() == Team.BLUE) {
					respawnLoc = this.game.getBlueTeamPlayerSpawn();
				}
				
				player.setFireTicks(0);
				player.setGameMode(GameMode.SURVIVAL);
				player.getActivePotionEffects().clear();
				player.setFallDistance(0);
				player.setLevel(0);
				player.setFoodLevel(20);
				player.setSaturation(10);
				player.setAbsorptionAmount(0);
				player.setExp(0);
				player.setTotalExperience(0);
				
				if(!game.getBlueTeamSpawn().getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)) {
					if(playerInfo.getTeam() == Team.RED) {
						player.getInventory().addItem(new ItemStack(Material.RED_WOOL, 64));	
					}
					if(playerInfo.getTeam() == Team.BLUE) {
						player.getInventory().addItem(new ItemStack(Material.BLUE_WOOL, 64));
					}
					giveStartingItems(player);
				}
			}
		}
		return respawnLoc;
	}
	
	public void onPlayerDeath(Player victim, PlayerInfo victimInfo) {
		String victimColor = victimInfo.getTeam() == Team.BLUE ? " &9" : " &7";
		victimColor = victimInfo.getTeam() == Team.RED ? " &c" : victimColor;
		
		String victimName = victimInfo.getChatTag() + victimColor + victimInfo.getDisplayName();
		
		if(victimInfo.getTeam() == Team.BLUE) {
			if(!this.blueSpawnPlaced) {
				if(this.redSpawnExists) {
					this.blueTeamPergatory.add(victim);	
					if(this.blueTeamPergatory.size() - this.blueTeamPlayers.size() == 0) {
						for(Player player : this.blueTeamPlayers) {
							PlayerInfo playerInfo = PlayerInfo.getInfo(player);

							String playerColor = playerInfo.getTeam() == Team.BLUE ? " &9" : " &7";
							playerColor = playerInfo.getTeam() == Team.RED ? " &c" : playerColor;
							
							String playerName = playerInfo.getChatTag() + playerColor + playerInfo.getDisplayName();
						
							
							playerInfo.resetTeam();
						
							MessageUtils.broadcastMessage(this.players, "&4&l>> " + playerName + " &fhas been eliminated.");
							MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
						}
						this.blueTeamPlayers.clear();
					}
				} else {
					this.blueTeamPlayers.remove(victim);

					victimInfo.resetTeam();
				
					MessageUtils.broadcastMessage(this.players, "&4&l>> " + victimName + " &fhas been eliminated.");
					MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);	
				}
			} else {
				MessageUtils.broadcastMessage(this.players, "&d&l>> " + victimName + " &ffell off the edge.");
			}
		}
		if(victimInfo.getTeam() == Team.RED) {
			if(!this.redSpawnPlaced) {
				if(this.redSpawnExists) {
					this.redTeamPergatory.add(victim);	
					if(this.redTeamPergatory.size() - this.redTeamPlayers.size() == 0) {
						for(Player player : this.redTeamPlayers) {
							PlayerInfo playerInfo = PlayerInfo.getInfo(player);
							
							String playerColor = playerInfo.getTeam() == Team.BLUE ? " &9" : " &7";
							playerColor = playerInfo.getTeam() == Team.RED ? " &c" : playerColor;
							
							String playerName = playerInfo.getChatTag() + playerColor + playerInfo.getDisplayName();
						
							
							playerInfo.resetTeam();
						
							MessageUtils.broadcastMessage(this.players, "&4&l>> " + playerName + " &fhas been eliminated.");
							MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
						}
						this.redTeamPlayers.clear();
					}
				} else {
					this.redTeamPlayers.remove(victim);
		
					victimInfo.resetTeam();
				
					MessageUtils.broadcastMessage(this.players, "&4&l>> " + victimName + " &fhas been eliminated.");
					MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);	
				}
			} else {
				MessageUtils.broadcastMessage(this.players, "&d&l>> " + victimName + " &ffell off the edge.");
			}
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
			victim.spigot().respawn();	
		}, 1);
	}	
	
	public boolean onPlayerPlace(Player player, PlayerInfo playerInfo, Location location, ItemStack placed) {
		boolean isCanceled = false;
		if(location.getX() >= Game.DISTANCE_BETWEEN_TEAMS/2 + 100) {
			MessageUtils.errorMessage(player, "You can't place blocks out here!");
			return true;
		}
		if(location.getX() <= -(Game.DISTANCE_BETWEEN_TEAMS/2 + 100)) {
			MessageUtils.errorMessage(player, "You can't place blocks out here!");
			return true;
		}
		if(location.getZ() >= Game.DISTANCE_BETWEEN_TEAMS/4) {
			MessageUtils.errorMessage(player, "You can't place blocks out here!");		
			return true;
		}
		if(location.getZ() <= -(Game.DISTANCE_BETWEEN_TEAMS/4)) {
			MessageUtils.errorMessage(player, "You can't place blocks out here!");		
			return true;
		}
		if(!this.blueSpawnPlaced && playerInfo.doesHaveTeamSpawn()) {
			if(this.game.isBlueSpawnItem(placed)) {
				isCanceled = true;
				player.getInventory().remove(placed);
				this.game.setBlueSpawn(location);
				this.blueSpawnPlaced = true;
				playerInfo.setDoesHaveTeamSpawn(false);
				MessageUtils.broadcastMessage(this.players, "&d>> &fThe &9Blue Team Spawn &fhas been re-placed!");
				MessageUtils.broadcastSound(this.players, Sound.BLOCK_BELL_USE);
				for(Player p : this.blueTeamPergatory) {
					PlayerInfo pi = PlayerInfo.getInfo(p);
					Location loc = this.onPlayerRespawn(p, pi);
					p.teleport(loc);
				}
				this.blueTeamPergatory.clear();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					location.getBlock().setType(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
				}, 1);
			}	
		}
		if(!this.redSpawnPlaced && playerInfo.doesHaveTeamSpawn()) {
			if(this.game.isRedSpawnItem(placed)) {
				isCanceled = true;
				player.getInventory().remove(placed);
				this.redSpawnPlaced = true;
				playerInfo.setDoesHaveTeamSpawn(false);
				this.game.setRedSpawn(location);
				MessageUtils.broadcastMessage(this.players, "&d>> &fThe &cRed Team Spawn &fhas been re-placed!");
				MessageUtils.broadcastSound(this.players, Sound.BLOCK_BELL_USE);
				for(Player p : this.redTeamPergatory) {
					PlayerInfo pi = PlayerInfo.getInfo(player);
					Location loc = this.onPlayerRespawn(p, pi);
					p.teleport(loc);
				}
				this.redTeamPergatory.clear();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					location.getBlock().setType(Material.ORANGE_GLAZED_TERRACOTTA);
				}, 1);
			}	
		}
		return isCanceled;
	}
	
	public boolean onPlayerBreak(Player player, PlayerInfo playerInfo, Block block) {
		if(this.blueSpawnPlaced) {
			Location blueSpawn = this.game.getBlueTeamSpawn();
			Vector3f vecBlueSpawn = new Vector3f(blueSpawn.getBlockX(), blueSpawn.getBlockY(), blueSpawn.getBlockZ());
			if(vecBlueSpawn.getX() == block.getX() && vecBlueSpawn.getY() == block.getY() && vecBlueSpawn.getZ() == block.getZ()) {
				String playerColor = playerInfo.getTeam() == Team.BLUE ? " &9" : " &7";
				playerColor = playerInfo.getTeam() == Team.RED ? " &c" : playerColor;
				
				String playerName = playerInfo.getChatTag() + playerColor + playerInfo.getDisplayName();
				
				if(playerInfo.getTeam() == Team.BLUE) {
					if(player.getInventory().firstEmpty() != -1) {
						player.getInventory().addItem(this.game.createBlueSpawnItem());
					} else {
						return false;
					}
					for(Player p : this.blueTeamPlayers) MessageUtils.infoMessage(p, playerName + " &fis moving the team spawn!");
					for(Player p : this.redTeamPlayers) MessageUtils.infoMessage(p, "&9Blue Team Spawn &fis being moved!");
					this.blueSpawnPlaced = false;
					playerInfo.setDoesHaveTeamSpawn(true);
					
					return false;
				}
				if(playerInfo.getTeam() == Team.RED) {
					this.blueSpawnExists = false;
					this.blueSpawnPlaced = false;
					
					for(Player p : this.blueTeamPergatory) {
						PlayerInfo pi = PlayerInfo.getInfo(p);
						this.blueTeamPlayers.remove(p);
						
						pi.resetTeam();
						
						String pColor = pi.getTeam() == Team.BLUE ? " &9" : " &7";
						pColor = pi.getTeam() == Team.RED ? " &c" : pColor;
						
						String pName = pi.getChatTag() + pColor + pi.getDisplayName();
						
						
						MessageUtils.broadcastMessage(this.players, "&4&l>> " + pName + " &fhas been eliminated.");
						MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);				
					}
					
					this.blueTeamPergatory.clear();

					MessageUtils.broadcastMessage(this.players, "&4&l>> " + playerName + " &fhas destroyed &9Blue Team Spawn&f!");
					MessageUtils.broadcastSound(this.players, Sound.ENTITY_ZOMBIE_VILLAGER_CURE);
					return false;
				}
			}
		}
		if(this.redSpawnPlaced) {
			Location redSpawn = this.game.getRedTeamSpawn();
			Vector3f vecRedSpawn = new Vector3f(redSpawn.getBlockX(), redSpawn.getBlockY(), redSpawn.getBlockZ());
			if(vecRedSpawn.getX() == block.getX() && vecRedSpawn.getY() == block.getY() && vecRedSpawn.getZ() == block.getZ()) {
				String playerColor = playerInfo.getTeam() == Team.BLUE ? " &9" : " &7";
				playerColor = playerInfo.getTeam() == Team.RED ? " &c" : playerColor;
				
				String playerName = playerInfo.getChatTag() + playerColor + playerInfo.getDisplayName();
				
				if(playerInfo.getTeam() == Team.RED) {
					if(player.getInventory().firstEmpty() != -1) {
						player.getInventory().addItem(this.game.createRedSpawnItem());
					} else {
						return false;
					}
					for(Player p : this.redTeamPlayers) MessageUtils.infoMessage(p, playerName + " &fis moving the team spawn!");
					for(Player p : this.blueTeamPlayers) MessageUtils.infoMessage(p, "&cRed Team Spawn &fis being moved!");
				
					playerInfo.setDoesHaveTeamSpawn(true);
					this.redSpawnPlaced = false;
					return false;
				}
				if(playerInfo.getTeam() == Team.BLUE) {
					this.redSpawnExists = false;
					this.redSpawnPlaced = false;
					
					for(Player p : this.redTeamPergatory) {
						PlayerInfo pi = PlayerInfo.getInfo(p);
						this.redTeamPlayers.remove(p);
						
						pi.resetTeam();
						
						String pColor = pi.getTeam() == Team.BLUE ? " &9" : " &7";
						pColor = pi.getTeam() == Team.RED ? " &c" : pColor;
						
						String pName = pi.getChatTag() + pColor + pi.getDisplayName();
						
						
						MessageUtils.broadcastMessage(this.players, "&4&l>> " + pName + " &fhas been eliminated.");
						MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);				
					}
					
					this.redTeamPergatory.clear();
					
					MessageUtils.broadcastMessage(this.players, "&4&l>> " + playerName + " &fhas destroyed &cRed Team Spawn&f!");
					MessageUtils.broadcastSound(this.players, Sound.ENTITY_ZOMBIE_VILLAGER_CURE);
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean onPlayerDropItem(Player player, PlayerInfo playerInfo, ItemStack itemDrop) {
		boolean isCanceled = false;
		if(playerInfo.doesHaveTeamSpawn()) {
			if(this.game.isBlueSpawnItem(itemDrop)) {
				isCanceled = true;
			}
			if(this.game.isRedSpawnItem(itemDrop)) {
				isCanceled = true;
			}
		}
		return isCanceled;
	}
	
	public Team findLoosingTeam() {
		if(this.blueTeamPlayers.size() > this.redTeamPlayers.size()) return Team.RED;
		else return Team.BLUE;
	}
	
	public String convertSecondsToString(int seconds) {
		int shownSeconds = seconds % 60;
		int shownMinutes = seconds / 60;
		if(shownSeconds <= 9) {
			return shownMinutes + ":0" + shownSeconds;
		} else {
			return shownMinutes + ":" + shownSeconds;
		}
	}
	
	public String getBoolString(boolean bool1, boolean bool2) {
		if(bool1 && bool2) {
			return MessageUtils.m("&a✔");
		} 
		if(!bool2) {
			return MessageUtils.m("&c✘");
		}
		return MessageUtils.m("&7&l―");
	}
	
	public void reset() {
		World world = Bukkit.getWorld("world");
		Location loc = new Location(world, 0, 255, 0);
		for(int i = 255; i >= 0; i--) {
			loc.subtract(0, 1, 0);
			if(!loc.getBlock().isEmpty()) {
				loc.add(0, 1, 0);
				break;
			}
		}
		for(Player player : this.players) {
			Main.getInstance().addPlayedPlayer(player.getName());
			PlayerInfo.getInfo(player).resetTeam();
			player.getInventory().clear();
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
				player.spigot().respawn();
			},1);
			player.teleport(loc);
			player.setHealth(20);
			player.setFireTicks(0);
			player.setGameMode(GameMode.SURVIVAL);
			player.getInventory().clear();
			player.getActivePotionEffects().clear();
			player.setFallDistance(0);
			player.setLevel(0);
			player.setFoodLevel(20);
			player.setSaturation(20);
			player.setAbsorptionAmount(0);
			player.setExp(0);
			player.setTotalExperience(0);
		
		}
		this.blueTeamPlayers.clear();
		this.redTeamPlayers.clear();
		this.blueTeamPergatory.clear();
		this.redTeamPergatory.clear();
		this.state = GameState.WAITING;
		this.interGameTimer = WAITING_TIME;
		MessageUtils.broadcastMessage(this.players, "&b&l>> &fThe next game will start in &d" + convertSecondsToString(this.interGameTimer) + "&f seconds!");
		MessageUtils.broadcastSound(this.players, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST);		
		MessageUtils.broadcastSound(this.players, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE);
		MessageUtils.broadcastSound(this.players, Sound.ENTITY_LIGHTNING_BOLT_THUNDER);		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
			try {
				this.game.delete();
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}, 20);
	}
	
	public void giveStartingItems(Player player) {
		final Inventory inv = player.getInventory();
		inv.addItem(new ItemStack(Material.STONE_AXE, 1));
		inv.addItem(new ItemStack(Material.GOLDEN_PICKAXE, 1));
	}

}
