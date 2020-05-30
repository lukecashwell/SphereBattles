package com.spherebattles.players;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.spherebattles.NBT.NBTTagCompound;
import com.spherebattles.NBT.NBTTagStringArray;
import com.spherebattles.ranks.Rank;
import com.spherebattles.ranks.RankRegistry;
import com.spherebattles.utils.DataTools;
import com.spherebattles.utils.MessageUtils;

import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo.PlayerInfoData;

public class PlayerInfo {
	
	private static final HashMap<Player, PlayerInfo> playerInfos = new HashMap<>();
	
	private Player player;
	
	// Cache for player's display chat tag.
	private String chatTag;
	
	private String tag;
	private List<Rank> ranks;
	

	// Money is the player's balance * 100
	public int money;
	public int kills;
	
	// Non-saved variables
	private String nick;
	private String gameId;
	private Team team;
	private Team pastTeam;
	
	private int pastGameID;
	
	private boolean hasTeamSpawn;
	
	public enum Team {
		BLUE,
		RED,
		NONE
	}
	
	private Team[] teams = { Team.BLUE, Team.RED, Team.NONE };
	
	private PlayerInfo(Player player) {
		this.player = player;
		NBTTagCompound playerInfo = DataTools.readNBTFile(this.getDataFile());
		if(playerInfo != null && playerInfo.getInt("version") != 0) {
			this.kills = playerInfo.getInt("kills");
			this.money = playerInfo.getInt("money");
			this.tag   = playerInfo.getString("tag");
			this.pastGameID = playerInfo.getInt("pastGame");
			
			NBTTagStringArray ranks = (NBTTagStringArray)playerInfo.getTag("ranks");
			this.ranks = new ArrayList<>();
			if(ranks != null)
			for(String rank : ranks.getAsStringArray()) {
				Rank r = RankRegistry.getRank(rank);
				if(r != null) this.ranks.add(r);
			}
			if(playerInfo.hasKey("pastTeam")) {
				this.pastTeam = teams[playerInfo.getInt("pastTeam")];
			}
		} else {
			this.tag = "";
			this.ranks = new ArrayList<>();
			
			this.kills = 0;
			this.money = 0;
			this.pastTeam = Team.NONE;
			this.pastGameID = 0;
		}
		this.nick = null;
		this.hasTeamSpawn = false;
		this.resetTeam();
		this.updateChatTag();
	}
	
	public String getDataFile() {
		return "playerDatas" + File.separatorChar + "data_" + this.player.getUniqueId();
	}
	
	public static PlayerInfo getInfo(Player player) {
		PlayerInfo p = PlayerInfo.playerInfos.get(player);
		if(p == null) {
			p = new PlayerInfo(player);
			PlayerInfo.playerInfos.put(player, p);
		}
		return p;
	}
	
	public static void unloadPlayer(Player player) {
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("blue").removeEntry(player.getName());
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(player.getName());
		PlayerInfo.playerInfos.remove(player);
	}
	
	public void saveData() {
		NBTTagCompound playerInfo = new NBTTagCompound();
		playerInfo.setInt("kills", this.kills);
		playerInfo.setInt("money", this.money);
		playerInfo.setString("tag", this.tag);
		
		ArrayList<String> ranks = new ArrayList<>();
		for(Rank rank : this.ranks) {
			ranks.add(rank.getName());
		}
		
		playerInfo.setTag("ranks", new NBTTagStringArray(ranks));
		playerInfo.setInt("version", 1);
		
		DataTools.writeNBTFile(this.getDataFile(), playerInfo);
	}
	
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.chatTag = tag;
	}

	public List<Rank> getRanks() {
		return ranks;
	}

	public void addRank(Rank rank) {
		this.ranks.add(rank);
		this.saveData();
		this.updateChatTag();
	}
	
	public void clearRanks() {
		this.ranks = new ArrayList<>();
		this.saveData();
		this.updateChatTag();
	}
	
	public String getGame() {
		return this.gameId;
	}
	
	public void setGame(String game) {
		this.gameId = game;
	}
	
	public int getMoney() {
		return money;
	}
	
	public void setMoney(int money) {
		this.money = money;
	}

	public void incMoney(int money) {
		this.money += money;
	}
	
	public void decMoney(int money) {
		this.money -= money;
	}
	
	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}
	
	public String getNick() {
		return this.nick;
	}
	
	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public boolean hasRank(String rank) {
		for(Rank r : this.ranks) {
			if(r.getName() == rank) {
				return true;
			}
		}
		return false;
	}
	
	public void updateChatTag() {
		if(this.tag != null) {
		    StringBuilder tag = new StringBuilder();
			
			List<Rank> ranks = new ArrayList<>();
			for(Rank rank : this.ranks) ranks.add(rank);
			
			boolean isFirst = true;
			while(!ranks.isEmpty()) {
				int greatestPriority = 0;
				int greatestIndex = 0;
				Rank greatestRank = null;
				
				int i = 0;
				for(Rank rank : ranks) {
					int priority = rank.getPriority();
					if(priority > greatestPriority) {
						greatestPriority = priority;
						greatestRank = rank;
						greatestIndex = i;
					}
					i++;
				}
				ranks.remove(greatestIndex);
				if(isFirst) {
					tag.append(MessageUtils.m(greatestRank.getFullForm()));
				} else {
					tag.append(MessageUtils.m(greatestRank.getSubForm()));
				}
				isFirst = false;
			}
			if(tag.length() == 0) { 
				this.chatTag = "";
			} else {
				this.chatTag = tag.toString();
			}
		} else {
			this.chatTag = MessageUtils.m(this.tag);
		}
	}
	
	public String getDisplayName() {
		if(this.nick == null) {
			return this.player.getName();
		}
		return this.nick;
	}
	
	public String getChatTag() {
		return this.chatTag;
	}

	public int getPastGameID() {
		return this.pastGameID;
	}
	
	public void setPastGameID(int id) {
		this.pastGameID = id;
	}
	
	public boolean doesHaveTeamSpawn() {
		return this.hasTeamSpawn;
	}
	
	public void setDoesHaveTeamSpawn(boolean hasSpawn) {
		this.hasTeamSpawn = hasSpawn;
	}
	
	public void setTeam(Team team) {
		if(team == Team.BLUE) {
			Bukkit.getScoreboardManager().getMainScoreboard().getTeam("blue").addEntry(this.player.getName());
		}
		if(team == Team.RED) {
			Bukkit.getScoreboardManager().getMainScoreboard().getTeam("red").addEntry(this.player.getName());	
		}
		this.team = team;
		this.pastTeam = team;
	}
	
	public void resetTeam() {
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("blue").removeEntry(this.player.getName());
		Bukkit.getScoreboardManager().getMainScoreboard().getTeam("red").removeEntry(this.player.getName());
		this.team = Team.NONE;
	}
	
	public Team getTeam() {
		return this.team;
	}	
	
	public Team getPastTeam() {
		return this.pastTeam;
	}
	
}
