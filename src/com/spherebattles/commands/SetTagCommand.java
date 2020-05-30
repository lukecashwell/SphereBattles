package com.spherebattles.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.spherebattles.NBT.NBTTagCompound;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.ranks.Rank;
import com.spherebattles.ranks.RankRegistry;
import com.spherebattles.utils.DataTools;
import com.spherebattles.utils.MessageUtils;

public class SetTagCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Player player = (Player)sender;
    	if(player.isOp()) {
    		if(args.length > 1) {
    			Player targetPlayer = Bukkit.getPlayer(args[0]);
    			if(targetPlayer != null) {
    				PlayerInfo targetInfo = PlayerInfo.getInfo(targetPlayer);
    				if(!args[1].equals("null")) {
    					StringBuilder tag = new StringBuilder();
    					for(int i = 1; i < args.length;i++) {
    						tag.append(args[i]).append(' ');
    					}
    					tag.setLength(tag.length() - 1);
    					targetInfo.setTag(MessageUtils.m(tag.toString()));

    					MessageUtils.successMessage(player, "You have successfully set their rank!");
    				} else {
    					targetInfo.setTag(null);
    					MessageUtils.successMessage(player, "You have successfully reset their rank!");
    				}
    			} else {
    				MessageUtils.errorMessage(player, "The player: '" + args[0] + "' is not on the server.");
    			}    			
    		}  else {
        		MessageUtils.errorMessage(player, "/settag <name> <tag>");
        	}
    	} else {
    		MessageUtils.errorMessage(player, "No permisions.");
    	}
    	return true;
    }
}
