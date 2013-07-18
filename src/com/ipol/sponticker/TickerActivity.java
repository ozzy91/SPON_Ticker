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

	// Views
	private ListView tickerList;
	private TextView txtHomeTeam;
	private TextView txtGuestTeam;
	private TextView txtResult;
	private TextView txtScorers;
	private View pointer;
	private View firstHalfTimeline;
	private View secondHalfTimeline;

	// Data
	private Match match;
	private ArrayList<TickerEvent> events;

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

		addIcons(match);

		txtHomeTeam.setText(match.getHomeTeam());
		txtGuestTeam.setText(match.getGuestTeam());
		txtResult.setText(match.getResult());
		txtScorers.setText(match.getGoals().size() + "");
	}

	public void init() {
		tickerList = (ListView) findViewById(R.id.ticker_list);
		txtHomeTeam = (TextView) findViewById(R.id.txt_home_team);
		txtGuestTeam = (TextView) findViewById(R.id.txt_guest_team);
		txtResult = (TextView) findViewById(R.id.txt_result);
		txtScorers = (TextView) findViewById(R.id.txt_scorers);
		pointer = findViewById(R.id.timeline_pointer);
		firstHalfTimeline = findViewById(R.id.timeline_first_half);
		secondHalfTimeline = findViewById(R.id.timeline_second_half);

		firstHalfTimeline.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						TickerScrollListener myListener = new TickerScrollListener(
								pointer, firstHalfTimeline, secondHalfTimeline);
						tickerList.setOnScrollListener(myListener);

						firstHalfTimeline.getViewTreeObserver()
								.removeOnGlobalLayoutListener(this);
					}
				});
	}

	public void addIcons(Match match) {
		ArrayList<Goal> goals = match.getGoals();
		ArrayList<Card> cards = match.getCards();
		ArrayList<Substitution> substitutions = match.getSubstitution();

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.timeline_layout);

		for (Goal goal : goals) {
			ImageView icon = new ImageView(this);
			icon.setImageResource(R.drawable.timeline_ball_2x);
			LayoutParams params = new LayoutParams(30, 30);
			params.addRule(RelativeLayout.ALIGN_BOTTOM,
					R.id.timeline_first_half);
			icon.setLayoutParams(params);

			float sizeOfMinute = 320 / 45;
			int minute = goal.getMinute();
			if (minute < 45) {
				icon.setTranslationY(-goal.getMinute() * sizeOfMinute);
			} else {
				icon.setTranslationY(-goal.getMinute() * sizeOfMinute -60);
			}
			layout.addView(icon, params);
		}
		
		for (Card card : cards) {
			ImageView icon = new ImageView(this);
			
			switch(card.getColor()){
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
			params.addRule(RelativeLayout.ALIGN_BOTTOM,
					R.id.timeline_first_half);
			params.setMargins(40, 0, 0, 0);
			icon.setLayoutParams(params);

			float sizeOfMinute = 320 / 45;
			int minute = card.getMinute();
			if (minute < 45) {
				icon.setTranslationY(-card.getMinute() * sizeOfMinute);
			} else {
				icon.setTranslationY(-card.getMinute() * sizeOfMinute -60);
			}
			layout.addView(icon, params);
		}
		
		for(Substitution substitution : substitutions){
			ImageView icon = new ImageView(this);
			icon.setImageResource(R.drawable.timeline_substitution_2x);
			LayoutParams params = new LayoutParams(30, 30);
			params.addRule(RelativeLayout.ALIGN_BOTTOM,
					R.id.timeline_first_half);
			params.setMargins(80, 0, 0, 0);
			icon.setLayoutParams(params);

			float sizeOfMinute = 320 / 45;
			int minute = substitution.getMinute();
			if (minute < 45) {
				icon.setTranslationY(-substitution.getMinute() * sizeOfMinute);
			} else {
				icon.setTranslationY(-substitution.getMinute() * sizeOfMinute -60);
			}
			layout.addView(icon, params);
		}
	}
}
