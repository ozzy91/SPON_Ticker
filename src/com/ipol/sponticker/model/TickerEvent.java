package com.ipol.sponticker.model;

public class TickerEvent implements Comparable<TickerEvent> {
	
	public static final String TYPE_GOAL = "TOR";
	public static final String TYPE_YELLOW = "GELB";
	public static final String TYPE_RED = "ROT";
	public static final String TYPE_YELLOW_RED = "GELBROT";
	public static final String TYPE_SUBSTITUTE = "EINWECHSLUNG";
	
	private int minute;
	private int addedTime;
	private String commentary;
	private String otherPlayer;
	private String score;
	private Player player;
	private EventType type;

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

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public String getOtherPlayer() {
		return otherPlayer;
	}

	public void setOtherPlayer(String otherPlayer) {
		this.otherPlayer = otherPlayer;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	@Override
	public int compareTo(TickerEvent another) {
		if (this.minute < another.minute)
			return 1;
		if (this.minute > another.minute)
			return -1;
		if (this.minute == another.minute && this.addedTime != 0){
			if (this.addedTime < another.addedTime)
				return 1;
			if (this.addedTime > another.addedTime)
				return -1;
		}
		return 0;
	}

}