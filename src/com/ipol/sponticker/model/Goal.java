package com.ipol.sponticker.model;

public class Goal {
	
	private Player player;
	private int minute;
	private int addedTime;
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public int getAddedTime() {
		return addedTime;
	}
	public void setAddedTime(int addedTime) {
		this.addedTime = addedTime;
	}

}
