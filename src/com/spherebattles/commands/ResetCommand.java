package com.spherebattles.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.spherebattles.NBT.NBTTagCompound;
import com.spherebattles.utils.DataTools;
import com.spherebattles.utils.MessageUtils;

public class ResetCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	Player player = (Player)sender;
    	MessageUtils.infoMessage(player, "Some message... wrting file");
    	NBTTagCompound thing = new NBTTagCompound();
    	thing.setTag("spawn", DataTools.locationToNbt(player.getLocation()));
    	DataTools.writeNBTFile("savedState.nbt", thing);
        return true;
    }
}
