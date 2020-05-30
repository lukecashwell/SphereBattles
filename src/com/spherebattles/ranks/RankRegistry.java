package com.spherebattles.ranks;

import java.util.HashMap;

public class RankRegistry {
    
	private static HashMap<String, Rank> ranks = new HashMap<String, Rank>();
	
	public void registerRanks() {   
		register(DeveloperRank.class);
		register(IngRank.class);
		register(TechRank.class);
		register(ModRank.class);
		register(AdminRank.class);
		register(TestRank.class);
		register(TesteeRank.class);
	}	
	
	public void register(Class<? extends Rank> rank) {
		try {
			Rank r = rank.newInstance();
			ranks.put(r.getName(), r);
		} catch (Exception o_o) { }
	}
	
	public static Rank getRank(String name) {
		 return ranks.get(name);
	}
	
	public static String[] getRanks() {
		return ranks.keySet().toArray(new String[] {});
	}
	
	public RankRegistry() {
		this.registerRanks();
	}	
}
