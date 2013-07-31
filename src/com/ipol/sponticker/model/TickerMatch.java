package com.ipol.sponticker.model;


public class TickerMatch {

	private String homeTeam;
	private String guestTeam;
	private String homeId;
	private String guestId;
	private String homeShortname;
	private String guestShortname;
	private String stadium;
	private String result;
	private int minute;
	private int addedTime;

	private int visitors;

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getGuestTeam() {
		return guestTeam;
	}

	public void setGuestTeam(String guestTeam) {
		this.guestTeam = guestTeam;
	}

	public String getHomeId() {
		return homeId;
	}

	public void setHomeId(String homeId) {
		this.homeId = homeId;
	}

	public String getGuestId() {
		return guestId;
	}

	public void setGuestId(String guestId) {
		this.guestId = guestId;
	}

	public String getHomeShortname() {
		return homeShortname;
	}

	public void setHomeShortname(String homeShortname) {
		this.homeShortname = homeShortname;
	}

	public String getGuestShortname() {
		return guestShortname;
	}

	public void setGuestShortname(String guestShortname) {
		this.guestShortname = guestShortname;
	}

	public String getStadium() {
		return stadium;
	}

	public void setStadium(String stadium) {
		this.stadium = stadium;
	}

	public int getVisitors() {
		return visitors;
	}

	public void setVisitors(int visitors) {
		this.visitors = visitors;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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
