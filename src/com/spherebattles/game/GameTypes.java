package com.spherebattles.game;

public enum GameTypes {
	
	TEST
	
	;
	
	public final boolean test;
	
	private GameTypes() {
		this.test = false;
	}
	
	public boolean getTest() {
		return test;
	}
}
