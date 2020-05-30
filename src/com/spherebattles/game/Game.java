package com.spherebattles.game;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Bed.Part;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.spherebattles.main.Main;
import com.spherebattles.utils.Lambda;
import com.spherebattles.utils.MessageUtils;

import sun.util.logging.resources.logging_ko;

public class Game {

	private String name;
	private World world;
	
	private GameEvents events;
	
	private Location redTeamSpawn;
	private Location blueTeamSpawn;
	
	private ItemStack redTeamSpawnItem;
	private ItemStack blueTeamSpawnItem;
	
	public static final int DISTANCE_BETWEEN_TEAMS = 300;
	
	public Game(String name) {
		Main.getInstance().games.put(name, this);
		this.name = name;	
		this.delete();
		this.events = new GameEvents(this);		
		
		this.redTeamSpawnItem = createRedSpawnItem();
		this.blueTeamSpawnItem = createBlueSpawnItem();
	}	
	
	public void copyWorld(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                target.mkdirs();
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyWorld(srcFile, destFile);
	                }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }	        
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	public void generateWorld(Lambda lambda) {
		if(this.world != null) {
			lambda.f();
			return;
		}
		
		try {
			File worldFile = new File(Bukkit.getWorldContainer(), "sb_" + name);
			this.copyWorld(new File(Bukkit.getWorldContainer(), "void"), worldFile);
			File session = new File(worldFile, "session.lock");
	        session.delete();
	        session.createNewFile();
	        DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(session));
	 
	        try {
	           dataoutputstream.writeLong(System.currentTimeMillis());
	        } finally {
	           dataoutputstream.close();
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		WorldCreator creator = new WorldCreator("sb_" + name);
		creator.generator(new VoidGenerator());
		World world = creator.createWorld();
		world.setDifficulty(Difficulty.NORMAL);
		world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
		world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
		world.setGameRule(GameRule.KEEP_INVENTORY, true);
		world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
		world.setTime(1000);
		this.world = world;
		
		System.out.println("[ Sphere Battles ] -> Starting world generation.");
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
			this.redTeamSpawn = new Location(world, DISTANCE_BETWEEN_TEAMS / 2, 70, 0, 90, 0);		
			List<Sphere> spheres = new ArrayList<>();  
			spheres.add(this.generateTeamArea(this.redTeamSpawn, 10, Material.RED_WOOL));
		
			// Aligned with the X axis
			// Generate team pads.
		
			Object[][] lowTierResourcesRed = { 
					{ false, Material.OAK_LOG, Material.DARK_OAK_LOG, Material.BIRCH_LOG }, 
					{ false, Material.OAK_LOG, Material.DARK_OAK_LOG, Material.BIRCH_LOG }, 
					{ false, Material.COBBLESTONE, Material.STONE, Material.COAL_BLOCK, Material.IRON_ORE },
					{ false, Material.RED_WOOL },
					{ false, Material.GRAVEL, Material.SANDSTONE },
					{ true, Material.COBWEB }
			};	
			
			Random random = Main.getInstance().getRandom();
			long t = 0;
			for(Object[] objs : lowTierResourcesRed) {
				t++;
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					
				int radius = random.nextInt(10) + 5;
				boolean isNear = true;
				Sphere sphere = null;
				while(isNear) {
					sphere = new Sphere(this.redTeamSpawn.getBlockX() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockZ() + random.nextInt(50) - 25, radius);
					isNear = false;
					for(Sphere s : spheres) {
						if(s.isNear(sphere)) {
							isNear = true;
							break;
						}	
					}	
				}
				spheres.add(sphere);
				Material[] mats = new Material[objs.length - 1];
				for(int i = 1; i < objs.length; i++) {
					mats[i - 1] = (Material)objs[i];
				}
				this.generateSphere(sphere.getLocation(world), radius, (boolean)objs[0], mats);
				}, t);
			}			
			
		
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
				Object[][] midTierResourcesRed = { 
						{ false, Material.IRON_BLOCK }, 
						{ false, Material.IRON_BLOCK },
						{ false, Material.GLOWSTONE },
						{ false, Material.RED_WOOL },
						{ false, Material.REDSTONE_BLOCK, Material.NETHER_QUARTZ_ORE },
						{ false, Material.SLIME_BLOCK  },
						{ false, Material.HONEY_BLOCK },
				};
				long m = 0;
				for(Object[] objs : midTierResourcesRed) {
					m++;
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
						int radius = random.nextInt(7) + 4;
						boolean isNear = true;
						Sphere sphere = null;
						while(isNear) {
							sphere = new Sphere(this.redTeamSpawn.getBlockX() + random.nextInt(50) - 25 - DISTANCE_BETWEEN_TEAMS/4, this.redTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockZ() + random.nextInt(75) - 37, radius);
							isNear = false;
							for(Sphere s : spheres) {
								if(s.isNear(sphere)) {
									isNear = true;
									break;
								}
							}
						}
						spheres.add(sphere);
						Material[] mats = new Material[objs.length - 1];
						for(int i = 1; i < objs.length; i++) {
							mats[i - 1] = (Material)objs[i];
						}
						
						this.generateSphere(sphere.getLocation(world), radius, (boolean)objs[0], mats);
					}, m);
				}
				
				///---------------
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
					this.blueTeamSpawn = new Location(world, -DISTANCE_BETWEEN_TEAMS / 2, 70, 0, -90, 0);  
					spheres.add(this.generateTeamArea(this.blueTeamSpawn, 10, Material.BLUE_WOOL));
				
					// Aligned with the X axis
					// Generate team pads.
				
					Object[][] lowTierResourcesBlue = { 
							{ false,  Material.OAK_LOG, Material.DARK_OAK_LOG, Material.BIRCH_LOG }, 
							{ false, Material.OAK_LOG, Material.DARK_OAK_LOG, Material.BIRCH_LOG }, 
							{ false, Material.COBBLESTONE, Material.STONE, Material.COAL_BLOCK, Material.IRON_ORE },
							{ false, Material.BLUE_WOOL },
							{ false, Material.GRAVEL, Material.SANDSTONE },
							{ true, Material.COBWEB }
					};	
					
					long y = 0;
					for(Object[] objs : lowTierResourcesBlue) {
						y++;
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
							
						int radius = random.nextInt(10) + 5;
						boolean isNear = true;
						Sphere sphere = null;
						while(isNear) {
							sphere = new Sphere(this.blueTeamSpawn.getBlockX() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockZ() + random.nextInt(50) - 25, radius);
							isNear = false;
							for(Sphere s : spheres) {
								if(s.isNear(sphere)) {
									isNear = true;
									break;
								}	
							}	
						}
						spheres.add(sphere);
						Material[] mats = new Material[objs.length - 1];
						for(int i = 1; i < objs.length; i++) {
							mats[i - 1] = (Material)objs[i];
						}
						this.generateSphere(sphere.getLocation(world), radius, (boolean)objs[0], mats);
						}, y);
					}			
				
				
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
						Object[][] midTierResourcesBlue = { 
								{ false, Material.IRON_BLOCK }, 
								{ false, Material.IRON_BLOCK },
								{ false, Material.GLOWSTONE },
								{ false, Material.BLUE_WOOL },
								{ false, Material.REDSTONE_BLOCK, Material.NETHER_QUARTZ_ORE },
								{ false, Material.SLIME_BLOCK },
								{ false, Material.HONEY_BLOCK },
						};
						long u = 0;
						for(Object[] objs : midTierResourcesBlue) {
							u++;
							Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
								int radius = random.nextInt(7) + 4;
								boolean isNear = true;
								Sphere sphere = null;
								while(isNear) {
									sphere = new Sphere(this.blueTeamSpawn.getBlockX() + random.nextInt(50) - 25 + DISTANCE_BETWEEN_TEAMS/4, this.redTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockZ() + random.nextInt(75) - 37, radius);
									isNear = false;
									for(Sphere s : spheres) {
										if(s.isNear(sphere)) {
											isNear = true;
											break;
										}
									}
								}
								spheres.add(sphere);
								Material[] mats = new Material[objs.length - 1];
								for(int i = 1; i < objs.length; i++) {
									mats[i - 1] = (Material)objs[i];
								}
								
								this.generateSphere(sphere.getLocation(world), radius, (boolean)objs[0], mats);
							}, u);
						}
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
							// Generate Mob Sphere Red
							{
								int radius = random.nextInt(5) + 3;
								boolean isNear = true;
								Sphere sphere = null;
								while(isNear) {
									sphere = new Sphere(this.redTeamSpawn.getBlockX() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockZ() + random.nextInt(50) - 25, radius);
									isNear = false;
									for(Sphere s : spheres) {
										if(s.isNear(sphere)) {
											isNear = true;
											break;
										}	
									}	
								}
								spheres.add(sphere);
								Location sphereLoc = sphere.getLocation(world);
								this.generateSphere(sphereLoc, radius, true, Material.AIR, Material.GRASS_BLOCK, Material.GRASS_BLOCK);
								Block center = sphereLoc.getBlock();
								center.setType(Material.SPAWNER);
								BlockState blockState = center.getState();
								CreatureSpawner spawner = ((CreatureSpawner)blockState);
								spawner.setSpawnedType(EntityType.PIG);
								blockState.update();
									
								sphereLoc.subtract(0, 1, 0);
								center = sphereLoc.getBlock();
								center.setType(Material.SPAWNER);
								blockState = center.getState();
								spawner = ((CreatureSpawner)blockState);
								spawner.setSpawnedType(EntityType.CHICKEN);
								blockState.update();
								
								sphereLoc.add(0, 2, 0);
								center = sphereLoc.getBlock();
								center.setType(Material.SPAWNER);
								blockState = center.getState();
								spawner = ((CreatureSpawner)blockState);
								spawner.setSpawnedType(EntityType.COW);
								blockState.update();
							}
							
							// Generate Enchant Sphere Red
							{
								int radius = random.nextInt(5) + 6;
								boolean isNear = true;
								Sphere sphere = null;
								while(isNear) {
									sphere = new Sphere(this.redTeamSpawn.getBlockX() + random.nextInt(50) - 25 - DISTANCE_BETWEEN_TEAMS/4, this.redTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.redTeamSpawn.getBlockZ() + random.nextInt(50) - 25, radius);
									isNear = false;
									for(Sphere s : spheres) {
										if(s.isNear(sphere)) {
											isNear = true;
											break;
										}	
									}	
								}
								spheres.add(sphere);
								Location sphereLoc = sphere.getLocation(world);
								this.generateSphere(sphereLoc, radius, true, Material.AIR, Material.BOOKSHELF, Material.BOOKSHELF, Material.OBSIDIAN);
								Block center = sphereLoc.getBlock();
								center.setType(Material.CHEST);
								BlockState blockState = center.getState();
								Chest chest = ((Chest)blockState);
								Inventory inventory = chest.getBlockInventory();
								inventory.setItem(0, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(1, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(2, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(3, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(4, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(5, new ItemStack(Material.LAPIS_LAZULI, 64));
								inventory.setItem(6, new ItemStack(Material.LAPIS_LAZULI, 64));
								inventory.setItem(7, new ItemStack(Material.DIAMOND, 5));
							}
							
							// Generate Mob Sphere Blue
							
							{
								int radius = random.nextInt(5) + 3;
								boolean isNear = true;
								Sphere sphere = null;
								while(isNear) {
									sphere = new Sphere(this.blueTeamSpawn.getBlockX() + random.nextInt(50) - 25, this.blueTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.blueTeamSpawn.getBlockZ() + random.nextInt(50) - 25, radius);
									isNear = false;
									for(Sphere s : spheres) {
										if(s.isNear(sphere)) {
											isNear = true;
											break;
										}	
									}	
								}
								spheres.add(sphere);
								Location sphereLoc = sphere.getLocation(world);
								this.generateSphere(sphereLoc, radius, true, Material.AIR, Material.GRASS_BLOCK, Material.GRASS_BLOCK);
								Block center = sphereLoc.getBlock();
								center.setType(Material.SPAWNER);
								BlockState blockState = center.getState();
								CreatureSpawner spawner = ((CreatureSpawner)blockState);
								spawner.setSpawnedType(EntityType.PIG);
								blockState.update();
									
								sphereLoc.subtract(0, 1, 0);
								center = sphereLoc.getBlock();
								center.setType(Material.SPAWNER);
								blockState = center.getState();
								spawner = ((CreatureSpawner)blockState);
								spawner.setSpawnedType(EntityType.CHICKEN);
								blockState.update();
						
								sphereLoc.add(0, 2, 0);
								center = sphereLoc.getBlock();
								center.setType(Material.SPAWNER);
								blockState = center.getState();
								spawner = ((CreatureSpawner)blockState);
								spawner.setSpawnedType(EntityType.COW);
								blockState.update();
						
							}
							
							// Generate Enchant Sphere Blue
							{
								int radius = random.nextInt(5) + 6;
								boolean isNear = true;
								Sphere sphere = null;
								while(isNear) {
									sphere = new Sphere(this.blueTeamSpawn.getBlockX() + random.nextInt(50) - 25 + DISTANCE_BETWEEN_TEAMS/4, this.blueTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.blueTeamSpawn.getBlockZ() + random.nextInt(50) - 25, radius);
									isNear = false;
									for(Sphere s : spheres) {
										if(s.isNear(sphere)) {
											isNear = true;
											break;
										}	
									}	
								}
								spheres.add(sphere);
								Location sphereLoc = sphere.getLocation(world);
								this.generateSphere(sphereLoc, radius, true, Material.AIR, Material.BOOKSHELF, Material.BOOKSHELF, Material.OBSIDIAN);
								Block center = sphereLoc.getBlock();
								center.setType(Material.CHEST);
								BlockState blockState = center.getState();
								Chest chest = ((Chest)blockState);
								Inventory inventory = chest.getBlockInventory();
								inventory.setItem(0, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(1, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(2, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(3, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(4, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));
								inventory.setItem(5, new ItemStack(Material.LAPIS_LAZULI, 64));
								inventory.setItem(6, new ItemStack(Material.LAPIS_LAZULI, 64));
								inventory.setItem(7, new ItemStack(Material.DIAMOND, 5));
							}
							
							// Generate Diamond Spheres
							for(int i = 0; i < 3; i++) {
								int radius = random.nextInt(6) + 5;
								boolean isNear = true;
								Sphere sphere = null;
								while(isNear) {
									sphere = new Sphere(random.nextInt(50) - 25, this.blueTeamSpawn.getBlockY() + random.nextInt(50) - 25, this.blueTeamSpawn.getBlockZ() + random.nextInt(120) - 60, radius);
									isNear = false;
									for(Sphere s : spheres) {
										if(s.isNear(sphere)) {
											isNear = true;
											break;
										}	
									}	
								}
								spheres.add(sphere);
								Location sphereLoc = sphere.getLocation(world);
								this.generateSphere(sphereLoc, radius, false, Material.DIAMOND_BLOCK, Material.OAK_LEAVES, Material.GOLD_BLOCK, Material.OAK_LOG);
							}
							
							lambda.f();
						}, 2*u);	
			
						
					}, 2*y);				
				}, 20);			
			}, t*2);
		}, 100);
		
	
	
	}
	
	public Sphere generateTeamArea(Location loc, int radius, Material block) {
		Location newLoc = loc.clone().add(-3, -1, -3);
		this.generateSphere(loc, radius, true, block, block, Material.AIR);
		for(int i = 0; i < 7; i++) {
			for(int j = 0; j < 7; j++) {
				Location blockLoc = newLoc.clone().add(i, 0, j);
				blockLoc.getBlock().setType(Material.BEDROCK);
			}
		}
		
		loc.getBlock().setType(block);
		
		return new Sphere(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), radius);
	}
	
	public void generateSphere(Location location, int radius, boolean hollow, Material... blocks) {
		Random random = Main.getInstance().getRandom();
		for(int i = -radius; i < radius; i++) {
			for(int j = -radius; j < radius; j++) {
				for(int k = -radius; k < radius; k++) {
					int r = i*i + j*j + k*k;
					if(r <= radius*radius && (!hollow || r >= (radius-1)*(radius-1))) {
						Location loc = location.clone().add(i, j, k);
						Block b = loc.getBlock();
						Material block = blocks[random.nextInt(blocks.length)];
						b.setType(block);
					}
				}
			}
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	public GameEvents getEvents() {
		return this.events;
	}
	
	public Location getRedTeamPlayerSpawn() {
		Location loc = this.redTeamSpawn.clone().add(0, 1, 0);
		while(!loc.getBlock().isEmpty()) loc.add(0,1,0);
		return loc;
	}
	
	public Location getBlueTeamPlayerSpawn() {
		Location loc = this.blueTeamSpawn.clone().add(0, 1, 0);
		while(!loc.getBlock().isEmpty()) loc.add(0,1,0);
		return loc;
	}
	
	public ItemStack createRedSpawnItem() {
		ItemStack itemStack = new ItemStack(Material.ORANGE_GLAZED_TERRACOTTA, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(MessageUtils.m("&cRed Team Spawn"));
		List<String> lore = new ArrayList<>();
		lore.add(MessageUtils.m("&7Wherever this block is blaced becomes the"));
		lore.add(MessageUtils.m("&7new spawn of &cRed Team&7!"));
		meta.setLore(lore);
		meta.setCustomModelData(1234);
		itemStack.setItemMeta(meta);
		return itemStack;
	}
	
	public boolean isRedSpawnItem(ItemStack item) {
		if(item == null || item.getItemMeta() == null) return false;
		return item.getItemMeta().equals(this.redTeamSpawnItem.getItemMeta());
	}
	
	public boolean isBlueSpawnItem(ItemStack item) {
		if(item == null || item.getItemMeta() == null) return false;
		return item.getItemMeta().equals(this.blueTeamSpawnItem.getItemMeta());
	}
	
	public ItemStack createBlueSpawnItem() {
		ItemStack itemStack = new ItemStack(Material.LIGHT_BLUE_GLAZED_TERRACOTTA, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.setDisplayName(MessageUtils.m("&9Blue Team Spawn"));
		List<String> lore = new ArrayList<>();
		lore.add(MessageUtils.m("&7Wherever this block is blaced becomes the"));
		lore.add(MessageUtils.m("&7new spawn of &9Blue Team&7!"));
		meta.setLore(lore);
		meta.setCustomModelData(1234);
		itemStack.setItemMeta(meta);
		return itemStack;
	}
	
	public Location getRedTeamSpawn() {
		return this.redTeamSpawn;
	}
	
	public Location getBlueTeamSpawn() {
		return this.blueTeamSpawn;
	}
	
	public void setBlueSpawn(Location location) {
		this.blueTeamSpawn = location;
	}
	
	public void setRedSpawn(Location location) {
		this.redTeamSpawn = location;
	}
	
	public void delete() {
		if(this.world != null) {
			this.unloadWorld(this.world);
			Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
				this.deleteWorld(this.world.getWorldFolder());
				this.world = null;
			}, 5);
		}
	}
	
	private void unloadWorld(World world) {
	    if(world != null) {
	        Bukkit.getServer().unloadWorld(world, false);
	    }	    
	}
	
	private boolean deleteWorld(File path) {	
		if(path.exists()) {
	        File files[] = path.listFiles();
	        for(int i = 0; i < files.length; i++) {
	            if(files[i].isDirectory()) {
	                deleteWorld(files[i]);
	            } else {
	                files[i].delete();
	            }
	        }
	    }
		return path.delete();
	}
	
}
