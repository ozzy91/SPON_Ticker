package com.ipol.sponticker.model;

import java.util.ArrayList;

public class Match {

	private String homeTeam;
	private String guestTeam;
	private String stadium;
	private String result;

	private int visitors;
	private ArrayList<Goal> goals = new ArrayList<Goal>();
	private ArrayList<Card> cards = new ArrayList<Card>();
	private ArrayList<Substitution> substitutions = new ArrayList<Substitution>();

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

	public void addGoal(Goal goal) {
		if (goal != null)
			goals.add(goal);
	}

	public ArrayList<Goal> getGoals() {
		return goals;
	}
	
	public void addCard(Card card) {
		if (card != null)
			cards.add(card);
	}

	public ArrayList<Card> getCards() {
		return cards;
	}
	
	public void addSubstitution(Substitution substitution) {
		if (substitution != null)
			substitutions.add(substitution);
	}

	public ArrayList<Substitution> getSubstitution() {
		return substitutions;
	}

}
