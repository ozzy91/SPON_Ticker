package com.ipol.sponticker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ipol.sponticker.model.Card;
import com.ipol.sponticker.model.Goal;
import com.ipol.sponticker.model.Match;
import com.ipol.sponticker.model.Substitution;
import com.ipol.sponticker.model.TickerEvent;

public class TickerActivity extends Activity {
	
	private static final int SCROLL_OFFSET_TOP = 100;

	// Views
	private ListView tickerList;
	private TextView txtHomeTeam;
	private TextView txtGuestTeam;
	private TextView txtResult;
	private TextView txtScorers;
	private View pointer;
	private View firstHalfTimeline;
	private View secondHalfTimeline;

	private RelativeLayout timelineLayout;

	// Data
	private Match match;
	private ArrayList<TickerEvent> events;

	public float sizeOfMinute;
	public float timelineGap;

	TickerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ticker);
		init();

		InputStream is = null;
		try {
			is = getAssets().open("1874414.json");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JSonParser parser = new JSonParser();
		JSONObject obj = parser.getJSON(is);
		JSONArray eventArray = parser.getEventArray(obj);
		events = parser.getEvents(eventArray);
		match = parser.getMatch(obj);

		Collections.sort(events);
		adapter = new TickerAdapter(this, R.layout.list_event, events);
		tickerList.setAdapter(adapter);

		txtHomeTeam.setText(match.getHomeTeam());
		txtGuestTeam.setText(match.getGuestTeam());
		txtResult.setText(match.getResult());
		txtScorers.setText(match.getGoals().size() + "");

	}

	/**
	 * Initializes variables for views.
	 * Gets all views from the activity specified by a variable.
	 */
	public void init() {
		tickerList = (ListView) findViewById(R.id.ticker_list);
		txtHomeTeam = (TextView) findViewById(R.id.txt_home_team);
		txtGuestTeam = (TextView) findViewById(R.id.txt_guest_team);
		txtResult = (TextView) findViewById(R.id.txt_result);
		txtScorers = (TextView) findViewById(R.id.txt_scorers);
		pointer = findViewById(R.id.timeline_pointer);
		firstHalfTimeline = findViewById(R.id.timeline_first_half);
		secondHalfTimeline = findViewById(R.id.timeline_second_half);
		timelineLayout = (RelativeLayout) findViewById(R.id.timeline_layout);

		firstHalfTimeline.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						sizeOfMinute = firstHalfTimeline.getHeight() / 44;
						timelineGap = secondHalfTimeline.getBottom()
								- firstHalfTimeline.getTop();

						TickerScrollListener myListener = new TickerScrollListener(
								pointer, sizeOfMinute, timelineGap);
						tickerList.setOnScrollListener(myListener);

						timelineLayout.setOnTouchListener(new TimelineTouchListener(
								tickerList, TickerActivity.this, firstHalfTimeline,
								secondHalfTimeline));

						addIcons(match);

						firstHalfTimeline.getViewTreeObserver()
								.removeOnGlobalLayoutListener(this);
					}
				});
	}

	
	
	/**
	 * Adds the icons on the timeline for special events.
	 * @param match The match with the information about events.
	 */
	public void addIcons(Match match) {
		ArrayList<Goal> goals = match.getGoals();
		ArrayList<Card> cards = match.getCards();
		ArrayList<Substitution> substitutions = match.getSubstitution();

		// add all goal icons
		for (Goal goal : goals) {
			ImageView icon = new ImageView(this);
			icon.setImageResource(R.drawable.timeline_ball_2x);
			LayoutParams params = new LayoutParams(30, 30);
			params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.timeline_first_half);
			params.setMargins(0, 0, 0, -15);
			icon.setLayoutParams(params);

			int minute = goal.getMinute();
			if (minute < 45) {
				icon.setTranslationY(-goal.getMinute() * sizeOfMinute - icon.getHeight()
						/ 2);
			} else {
				icon.setTranslationY(-goal.getMinute() * sizeOfMinute + timelineGap
						- icon.getHeight() / 2);
			}
			timelineLayout.addView(icon, params);
		}

		// add all card icons
		for (Card card : cards) {
			ImageView icon = new ImageView(this);

			switch (card.getColor()) {
			case Card.YELLOW:
				icon.setImageResource(R.drawable.timeline_card_yellow_2x);
				break;
			case Card.YELLOW_RED:
				icon.setImageResource(R.drawable.timeline_card_yellowred_2x);
				break;
			case Card.RED:
				icon.setImageResource(R.drawable.timeline_card_red_2x);
				break;
			}
			LayoutParams params = new LayoutParams(30, 30);
			params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.timeline_first_half);
			params.setMargins(40, 0, 0, -15);
			icon.setLayoutParams(params);

			int minute = card.getMinute();
			if (minute < 45) {
				icon.setTranslationY(-card.getMinute() * sizeOfMinute);
			} else {
				icon.setTranslationY(-card.getMinute() * sizeOfMinute + timelineGap);
			}
			timelineLayout.addView(icon, params);
		}

		// add all substitution icons
		for (Substitution substitution : substitutions) {
			ImageView icon = new ImageView(this);
			icon.setImageResource(R.drawable.timeline_substitution_2x);
			LayoutParams params = new LayoutParams(30, 30);
			params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.timeline_first_half);
			params.setMargins(80, 0, 0, -15);
			icon.setLayoutParams(params);

			int minute = substitution.getMinute();
			if (minute < 45) {
				icon.setTranslationY(-substitution.getMinute() * sizeOfMinute);
			} else {
				icon.setTranslationY(-substitution.getMinute() * sizeOfMinute
						+ timelineGap);
			}
			timelineLayout.addView(icon, params);
		}
	}
	
	

	/**
	 * Scrolls to the item closest to the given minute.
	 * If there is no item at the given minute,
	 * it scrolls to the closest one before or after
	 * 
	 * @param minute The minute to scroll to.
	 */
	public void scrollToMinute(int minute) {

		System.out.println("Scroll to " + minute);
		int index = 0;
		boolean exact = false;

		for (int loop = 0; loop < events.size(); loop++) {
			TickerEvent event = events.get(loop);
			if (event.getMinute() == minute) {
				index = loop;
				exact = true;
				break;
			} else if (event.getMinute() < minute) {
				index = loop;
				break;
			}
		}
		if (exact) {
			tickerList.smoothScrollToPositionFromTop(index, SCROLL_OFFSET_TOP);
		} else if(index != 0){
			
			int earlier = events.get(index).getMinute();
			int later = events.get(index - 1).getMinute();

			int deltaEarlier = minute - earlier;
			int deltaLater = later - minute;

			if (deltaEarlier < deltaLater) {
				tickerList.smoothScrollToPositionFromTop(index, SCROLL_OFFSET_TOP);
			} else {
				tickerList.smoothScrollToPositionFromTop(index - 1, SCROLL_OFFSET_TOP);
			}
		} else {
			tickerList.smoothScrollToPosition(0);
		}

	}

}
