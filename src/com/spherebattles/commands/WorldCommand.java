package com.spherebattles.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.spherebattles.NBT.NBTTagCompound;
import com.spherebattles.utils.DataTools;
import com.spherebattles.utils.MessageUtils;

public class WorldCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Player player = (Player)sender;
    	if(player.isOp()) {
    		if(args.length > 0) {
    			if(args[0].equals("goto")) {
    				if(args.length == 2) {
    					World world = Bukkit.getWorld(args[1]);
    					if(world != null) {
    						Location location = new Location(world, 0, 255, 0);
    						player.teleport(location);
    					} else {
    						MessageUtils.errorMessage(player, "No world by name: '" + args[1] + "'");
    					}
    				} else {
    					MessageUtils.infoMessage(player, "/world goto <world name>");
    				}
    			} else {
    				MessageUtils.infoMessage(player, "/world <goto>");
    			}
    		} else {
    			MessageUtils.infoMessage(player, "/world <goto>");
    		}
    	} else {
    		MessageUtils.errorMessage(player, "No permisions.");
    	}
        return true;
    }
}
