package com.ipol.sponticker;

import java.util.ArrayList;

import com.ipol.sponticker.model.EventType;
import com.ipol.sponticker.model.Match;
import com.ipol.sponticker.model.Player;
import com.ipol.sponticker.model.TickerEvent;

public class MockGenerator {

	private static final int NUM_EVENTS = 25;
	private static final double CHANCE_GOAL = 0.1;
	private static final double CHANCE_YELLOW = CHANCE_GOAL + 0.2;
	private static final double CHANCE_RED = CHANCE_YELLOW + 0.05;
	private static final double CHANCE_YELLOW_RED = CHANCE_RED + 0.05;
	private static final double CHANCE_SUBSTITUTE = CHANCE_YELLOW_RED + 0.1;

	public static Match getMatch() {

		Match match = new Match();

		// set the information for the match
		match.setGuestTeam("FC Bayern");
		match.setHomeTeam("VFB Stuttgart");
		match.setStadium("Mercedes-Benz Arena");
		match.setVisitors(15000);
		match.setResult("3:2");

		return match;
	}

	public static ArrayList<TickerEvent> getTicker() {

		ArrayList<TickerEvent> events = new ArrayList<TickerEvent>();

		// fill the list with new events
		for (int eventCount = 0; eventCount < NUM_EVENTS; eventCount++) {
			TickerEvent event = new TickerEvent();

			event.setMinute(randomMinute());

			// randomly generate the event type
			double random = Math.random();
			if (random <= CHANCE_GOAL) {
				event.setType(EventType.GOAL);
				event.setPlayer(new Player("Gomez", "FC Bayern"));
				event.setCommentary("Tor fŸr Bayern!!");
			}
			if (random > CHANCE_GOAL && random <= CHANCE_YELLOW) {
				event.setType(EventType.YELLOW);
				event.setPlayer(new Player("Cacau", "VFB Stuttgart"));
				event.setCommentary("Gelb fŸr Cacau");
			}
			if (random > CHANCE_YELLOW && random <= CHANCE_RED) {
				event.setType(EventType.RED);
				event.setPlayer(new Player("Niedermeier", "VFB Stuttgart"));
				event.setCommentary("Rote Karte, Platzverweis fŸr Niedermeier");
			}
			if (random > CHANCE_RED && random <= CHANCE_YELLOW_RED) {
				event.setType(EventType.YELLOWRED);
				event.setPlayer(new Player("Niedermeier", "VFB Stuttgart"));
				event.setCommentary("Gelb-Rot fŸr Gštze, er fliegt vom Platz");
			}
			if (random > CHANCE_YELLOW_RED && random <= CHANCE_SUBSTITUTE) {
				event.setType(EventType.SUBSTITUTE);
				event.setPlayer(new Player("Robben", "FC Bayern"));
				event.setOtherPlayer("MŸller");
				event.setCommentary("Der Trainer wechselt Robben ein");
			}
			if (random > CHANCE_SUBSTITUTE) {
				event.setType(EventType.NORMAL);
				event.setCommentary("Die Bayern haben Ecke");
			}
			events.add(event);
		}

		return events;
	}

	private static int randomMinute() {
		long minute = 1 + Math.round((90 * Math.random()));
		return (int) minute;
	}
}
