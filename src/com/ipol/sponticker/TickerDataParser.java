package com.ipol.sponticker;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ipol.sponticker.model.EventType;
import com.ipol.sponticker.model.TickerEvent;
import com.ipol.sponticker.model.TickerMatch;

public class TickerDataParser {

	private static final String TAG_PAARUNGEN = "PAARUNGEN";
	private static final String TAG_PAARUNG = "PAARUNG";
	private static final String TAG_MATCH_EVENTS = "SPIELEREIGNISSE";

	// tags for match
	private static final String TAG_STADIUM = "STADION";
	private static final String TAG_HOME_TEAM = "HEIM";
	private static final String TAG_GUEST_TEAM = "GAST";
	private static final String TAG_TEAM_ID = "VEREINID";
	private static final String TAG_VISITORS = "ZUSCHAUER";
	private static final String TAG_STANDING = "SPIELSTAND";
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
	private static final String TAG_TICKER_TIME = "TICKERZEIT";

	private TickerMatch match;
	
	private SimpleDateFormat dateFormat;

	public TickerDataParser() {
		match = new TickerMatch();
		dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);
	}

	/**
	 * Create a JSONObject for a JSON file represented by an InputStream.
	 * 
	 * @param stream
	 *            The InputStream of the JSON file.
	 * @return The JSONObect that represents the JSON file.
	 */
	public JSONObject getJSON(InputStream stream) {

		JSONObject jsonObject = null;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "ISO-8859-1"), 8);
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

	/**
	 * Creates a JSONArray for all ticker events in the JSONObject parameter.
	 * 
	 * @param object
	 *            The JSONObject holding the array of ticker events.
	 * @return A JSONArray with all the ticker events.
	 */
	public JSONArray getEventArray(JSONObject object) {

		JSONArray array = null;

		try {
			array = object.getJSONObject(TAG_PAARUNGEN).getJSONObject(TAG_PAARUNG).getJSONObject(TAG_MATCH_EVENTS).getJSONArray(TAG_EVENTS);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return array;
	}

	/**
	 * Parses all Events from the JSON file and creates a list of them.
	 * 
	 * @param jsonEvents
	 *            The JSONArray of the ticker events
	 * @return An ArrayList with all the created TickerEvents
	 */
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
					event.setMinute(Integer.valueOf(minute.substring(0, minute.indexOf("+"))));
					event.setAddedTime(Integer.valueOf(minute.substring(minute.indexOf("+") + 1)));
				} else {
					event.setMinute(Integer.valueOf(minute));
				}
				
				if(jsonEvent.has(TAG_TICKER_TIME)){
					try {
						event.setTickerTime(dateFormat.parse(jsonEvent.getString(TAG_TICKER_TIME)));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}

				if (jsonEvent.has(TAG_TYPE)) {
					// set the player
					try {
						event.setPlayer(jsonEvent.getJSONObject(TAG_PLAYER).getString(TAG_NAME));
					} catch (JSONException e) {
						event.setPlayer(jsonEvent.getString(TAG_PLAYER));
					} finally {
						event.setTeam(jsonEvent.getString(TAG_TEAM));

						// set the event type
						String type = jsonEvent.getString(TAG_TYPE);

						if (type.equals(TickerEvent.TYPE_GOAL)) {
							event.setType(EventType.GOAL);
							event.setScore(jsonEvent.getString(TAG_SCORE));
						}
						if(type.equals(TickerEvent.TYPE_OWNGOAL)){
							event.setType(EventType.OWNGOAL);
							event.setScore(jsonEvent.getString(TAG_SCORE));
						}
						if (type.equals(TickerEvent.TYPE_RED)) {
							event.setType(EventType.RED);
						}
						if (type.equals(TickerEvent.TYPE_SUBSTITUTE)) {
							event.setType(EventType.SUBSTITUTE);
							event.setOtherPlayer(jsonEvent.getString(TAG_SUBSTITUTE_PLAYER));
						}
						if (type.equals(TickerEvent.TYPE_YELLOW)) {
							event.setType(EventType.YELLOW);
						}
						if (type.equals(TickerEvent.TYPE_YELLOW_RED)) {
							event.setType(EventType.YELLOWRED);
						}
						if(type.equals(TickerEvent.TYPE_PENALTY)){
							event.setType(EventType.PENALTY);
						}
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

	/**
	 * Loads all relevant information about the match and creates a new
	 * TickerMatch Object.
	 * 
	 * @param object
	 *            The JSONObject that holds the information of the match.
	 * @return A TickerMatch with the data from the JSON file.
	 */
	public TickerMatch getMatch(JSONObject object) {

		JSONObject matchObject;
		try {
			matchObject = object.getJSONObject(TAG_PAARUNGEN).getJSONObject(TAG_PAARUNG);
			match.setGuestTeam(matchObject.getJSONObject(TAG_GUEST_TEAM).getString(TAG_NAME));
			match.setGuestId(matchObject.getJSONObject(TAG_GUEST_TEAM).getString(TAG_TEAM_ID));
			match.setHomeTeam(matchObject.getJSONObject(TAG_HOME_TEAM).getString(TAG_NAME));
			match.setHomeId(matchObject.getJSONObject(TAG_HOME_TEAM).getString(TAG_TEAM_ID));
			match.setResult(matchObject.getJSONObject(TAG_SCORE).getString(TAG_NAME));
			match.setStadium(matchObject.getString(TAG_STADIUM));
			if (matchObject.getJSONObject(TAG_VISITORS).has(TAG_NAME))
				match.setVisitors(matchObject.getJSONObject(TAG_VISITORS).getInt(TAG_NAME));
			matchObject.getJSONObject(TAG_STANDING).getString(TAG_MINUTE);

			String minute = matchObject.getJSONObject(TAG_STANDING).getString(TAG_MINUTE);

			// check if the time is in the added time
			if (minute.contains("+")) {
				match.setMinute(Integer.valueOf(minute.substring(0, minute.indexOf("+"))));
				match.setAddedTime(Integer.valueOf(minute.substring(minute.indexOf("+") + 1)));
			} else {
				match.setMinute(Integer.valueOf(minute));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return match;
	}
}
