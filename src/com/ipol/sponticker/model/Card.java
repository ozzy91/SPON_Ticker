package com.ipol.sponticker.model;

public class Card {

	private int minute;
	private int addedTime;
	private int color;

	public static final int YELLOW = 1;
	public static final int YELLOW_RED = 2;
	public static final int RED = 3;

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

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}