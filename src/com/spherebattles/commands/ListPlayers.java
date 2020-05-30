package com.spherebattles.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.spherebattles.NBT.NBTTagCompound;
import com.spherebattles.NBT.NBTTagString;
import com.spherebattles.main.Main;
import com.spherebattles.utils.DataTools;
import com.spherebattles.utils.MessageUtils;

public class ListPlayers implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	for(String string : Main.getInstance().completedGamePlayers) {
    		System.out.println(string);
    	}
    	return true;
    }
}
