package com.ipol.sponticker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipol.sponticker.model.EventType;
import com.ipol.sponticker.model.Goal;
import com.ipol.sponticker.model.Match;
import com.ipol.sponticker.model.Player;
import com.ipol.sponticker.model.TickerEvent;

public class JSonParser {

	private static final String TAG_PAARUNGEN = "PAARUNGEN";
	private static final String TAG_PAARUNG = "PAARUNG";
	private static final String TAG_MATCH_EVENTS = "SPIELEREIGNISSE";

	// tags for match
	private static final String TAG_STADIUM = "STADION";
	private static final String TAG_HOME_TEAM = "HEIM";
	private static final String TAG_GUEST_TEAM = "GAST";
	private static final String TAG_VISITORS = "ZUSCHAUER";
	private static final String TAG_NAME = "content";

	// tags for events
	private static final String TAG_EVENTS = "EREIGNIS";
	private static final String TAG_COMMENTARY = "KOMMENTAR";
	private static final String TAG_MINUTE = "MINUTE";
	private static final String TAG_TYPE = "TYP";
	private static final String TAG_PLAYER = "SPIELER";
	private static final String TAG_TEAM = "VEREIN";
	private static final String TAG_SUBSTITUTE_PLAYER = "SPEZIFISCH";
	private static final String TAG_SCORE = "SPIELSTAND";
	
	private Match match;
	
	public JSonParser() {
		match = new Match();
	}

	public JSONObject getJSON(InputStream stream) {

		JSONObject jsonObject = null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream, "ISO-8859-1"), 8);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			stream.close();
			jsonObject = new JSONObject(sb.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	public JSONArray getEventArray(JSONObject object) {

		JSONArray array = null;

		try {
			array = object.getJSONObject(TAG_PAARUNGEN)
					.getJSONObject(TAG_PAARUNG).getJSONObject(TAG_MATCH_EVENTS)
					.getJSONArray(TAG_EVENTS);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}

	public ArrayList<TickerEvent> getEvents(JSONArray jsonEvents) {

		ArrayList<TickerEvent> events = new ArrayList<TickerEvent>();
		for (int i = 0; i < jsonEvents.length(); i++) {
			JSONObject jsonEvent = null;
			TickerEvent event = new TickerEvent();
			try {
				jsonEvent = jsonEvents.getJSONObject(i);
				event.setCommentary(jsonEvent.getString(TAG_COMMENTARY));

				String minute = jsonEvent.getString(TAG_MINUTE);

				// check if the time is in the added time
				if (minute.contains("+")) {
					event.setMinute(90);
					event.setAddedTime(Integer.valueOf(minute.substring(minute
							.indexOf("+") + 1)));
				} else {
					event.setMinute(Integer.valueOf(minute));
				}

				if (jsonEvent.has(TAG_TYPE)) {
					// set the player
					String name = jsonEvent.getJSONObject(TAG_PLAYER)
							.getString(TAG_NAME);
					String team = jsonEvent.getString(TAG_TEAM);
					event.setPlayer(new Player(name, team));

					// set the event type
					String type = jsonEvent.getString(TAG_TYPE);

					if (type.equals(TickerEvent.TYPE_GOAL)) {
						event.setType(EventType.GOAL);
						event.setScore(jsonEvent.getString(TAG_SCORE));
						Goal goal = new Goal();
						goal.setMinute(event.getMinute());
						goal.setAddedTime(event.getAddedTime());
						goal.setPlayer(event.getPlayer());
						match.addGoal(goal);
					}
					if (type.equals(TickerEvent.TYPE_RED)) {
						event.setType(EventType.RED);
					}
					if (type.equals(TickerEvent.TYPE_SUBSTITUTE)) {
						event.setType(EventType.SUBSTITUTE);
						event.setOtherPlayer(jsonEvent
								.getString(TAG_SUBSTITUTE_PLAYER));
					}
					if (type.equals(TickerEvent.TYPE_YELLOW)) {
						event.setType(EventType.YELLOW);
					}
					if (type.equals(TickerEvent.TYPE_YELLOW_RED)) {
						event.setType(EventType.YELLOWRED);
					}
				} else {
					event.setType(EventType.NORMAL);
				}
				events.add(event);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return events;
	}

	public Match getMatch(JSONObject object) {
		
		JSONObject matchObject;
		try {
			matchObject = object.getJSONObject(TAG_PAARUNGEN)
					.getJSONObject(TAG_PAARUNG);
			match.setGuestTeam(matchObject.getJSONObject(TAG_GUEST_TEAM).getString(TAG_NAME));
			match.setHomeTeam(matchObject.getJSONObject(TAG_HOME_TEAM).getString(TAG_NAME));
			match.setResult(matchObject.getJSONObject(TAG_SCORE).getString(TAG_NAME));
			match.setStadium(matchObject.getString(TAG_STADIUM));
			match.setVisitors(matchObject.getJSONObject(TAG_VISITORS).getInt(TAG_NAME));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return match;
	}
}
