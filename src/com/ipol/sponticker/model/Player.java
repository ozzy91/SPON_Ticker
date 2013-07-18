package com.ipol.sponticker.model;

public class Player {
	
	String name;
	String team;
	
	public Player(String name, String team){
		this.name = name;
		this.team = team;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}
}
