package com.spherebattles.ranks;

public class Rank {

	// Helps to determine when to use the subForm.
	protected int priority = 0;	
	
	// Always converted to lower case.
	protected String identifier = "rank";
	
	// Used when it is the rank of greatest priority.
	protected String fullForm = "&8[&7RANK&8]";
	
	// Used when it is of sub priority to another rank.
	protected String subForm = "&8[&7R&8]";
	
	public Rank() {
	}
	
	public String getFullForm() {
		return this.fullForm;
	}
	
	public String getSubForm() {
		return this.subForm;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public String getName() {
		return identifier.toLowerCase();
	}

}
