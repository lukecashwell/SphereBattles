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

public class RankCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Player player = (Player)sender;
    	if(player.isOp()) {
    		if(args.length >= 2) {
    			Player targetPlayer = Bukkit.getPlayer(args[0]);
    			if(targetPlayer != null) {
    				PlayerInfo targetInfo = PlayerInfo.getInfo(targetPlayer);
    				if(args[1].equals("add")) {
    					if(args.length == 3) {
    						Rank rank = RankRegistry.getRank(args[2].toLowerCase());
    						if(rank == null) {
    							MessageUtils.infoMessage(player, "/rank <player> add <rank>");
        						MessageUtils.infoMessage_soundless(player, "Ranks ----");
        						String[] ranks = RankRegistry.getRanks();
        						for(String r : ranks) {
        							MessageUtils.infoMessage_soundless(player, r);
        						}
        						MessageUtils.infoMessage_soundless(player, "----------");
    						} else {
    							targetInfo.addRank(rank);
    							MessageUtils.successMessage(player, "Successfully added rank to " + args[0]);
    						} 
    					} else {
    						MessageUtils.infoMessage(player, "/rank <player> add <rank>");
    						MessageUtils.infoMessage_soundless(player, "Ranks ----");
    						String[] ranks = RankRegistry.getRanks();
    						for(String r : ranks) {
    							MessageUtils.infoMessage_soundless(player, r);
    						}
    						MessageUtils.infoMessage_soundless(player, "----------");
    					}
    				} else if(args[1].equals("clear")) {
    					MessageUtils.successMessage(player, "Successfully cleared ranks of " + args[0]);
    					targetInfo.clearRanks();
    				} else {
    					MessageUtils.infoMessage(player, "/rank <player> <add|clear>");
    				}
    			} else {
    				MessageUtils.errorMessage(player, "The player: '" + args[0] + "' is not on the server.");
    			}
    		} else {
    			MessageUtils.infoMessage(player, "/rank <player> <add|clear>");
    		}
    	} else {
    		MessageUtils.errorMessage(player, "No permisions.");
    	}
    	return true;
    }
}
