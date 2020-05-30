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

public class NickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Player player = (Player)sender;
    	if(player.isOp()) {
    	
    	}
    	return true;
    }
}
