package com.spherebattles.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.spherebattles.game.Game;
import com.spherebattles.main.Main;
import com.spherebattles.players.PlayerInfo;
import com.spherebattles.utils.MessageUtils;


public class OnPlayerChat implements Listener {
	
	// Swear filter built into this class
	private static String[] badWords = {"fuk","shit"," aβ","axwund"};
	
	@EventHandler
	public void playerChat(AsyncPlayerChatEvent event) {
		event.setCancelled(true);
		String playerMessage = event.getMessage();
		
		// Filter code.
		// Really slooooooooooooooooow..
		boolean hasBadWord = false;
		{
			String filterMessage = playerMessage.toLowerCase();
			filterMessage = filterMessage.replace('v', 'u');
			filterMessage = filterMessage.replace("ou", "u");
			filterMessage = filterMessage.replace("ss", "β");
			filterMessage = filterMessage.replace("ck", "k");
			
			StringBuilder newMessage = new StringBuilder();
			char pastChar = ' ';
			for(char c : filterMessage.toCharArray()) {
				if(pastChar != c) {
					newMessage.append(c);
				}
				pastChar = c;
			}
			
			filterMessage = newMessage.toString();
			for(String bw : badWords) {
				if(filterMessage.contains(bw)) {
					hasBadWord = true;
					break;
				}
			}
		}
		
		Player player = event.getPlayer();
		
		if(hasBadWord) {
			MessageUtils.errorMessage(player, "Please use some better language.");
		} else {
			PlayerInfo playerInfo = PlayerInfo.getInfo(player);
			
			StringBuilder message = new StringBuilder();
			message
			.append(MessageUtils.m("&a ƒ "))
			.append(playerInfo.getChatTag())
			.append(MessageUtils.m("&7 "));
			
			System.out.println(">>>> " + player.getName() + " :: " + event.getMessage());
			
			Game game = Main.getInstance().games.get(playerInfo.getGame());
			StringBuilder newPlayerMessage = new StringBuilder(playerMessage);
			if(game != null) {
				String insert = game.getEvents().onPlayerChat(player, playerInfo, newPlayerMessage);
				if(insert == null) { 
					return;
				}
				message.append(MessageUtils.m(insert));
			}
			
			message
			.append(playerInfo.getDisplayName())
			.append(MessageUtils.m(": &f"))
			.append(newPlayerMessage);
			
			if(newPlayerMessage.length() > 0) {
				for(Player p : Bukkit.getOnlinePlayers()) {
					p.sendMessage(message.toString());
				}
			}
		}		
	}
}
