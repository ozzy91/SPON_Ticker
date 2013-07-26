package com.ipol.sponticker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ipol.sponticker.model.EventType;
import com.ipol.sponticker.model.Match;
import com.ipol.sponticker.model.TickerEvent;

public class TickerFragment extends Fragment implements OnGlobalLayoutListener {

	private static final int SCROLL_OFFSET_TOP = 100;
	private static final long HEADER_SCROLL_DELAY = 2000;

	private Activity activity;

	// Views
	private View fragmentView;
	private ListView tickerList;
	private TextView txtHomeTeam;
	private TextView txtGuestTeam;
	private TextView txtResult;
	private GoalText txtGoalsHome;
	private GoalText txtGoalsGuest;

	private Timeline timeline;

	// Data
	private Match match;
	private ArrayList<TickerEvent> events;
	private ArrayList<TickerEvent> goalsHome;
	private ArrayList<TickerEvent> goalsGuest;

	private Timer scrollTimer;
	private int scrollPositionHome = 0;
	private int scrollPositionGuest = 0;

	InputStream is = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		activity = getActivity();
		fragmentView = inflater.inflate(R.layout.ticker_fragment, container, false);

		init();

		if (is == null) {
			try {
				is = activity.getAssets().open("matches/1874414.json");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		JSonParser parser = new JSonParser();
		JSONObject obj = parser.getJSON(is);
		JSONArray eventArray = parser.getEventArray(obj);
		events = parser.getEvents(eventArray);
		match = parser.getMatch(obj);

		Collections.sort(events);
		TickerAdapter adapter = new TickerAdapter(activity, R.layout.list_event, events);
		tickerList.setAdapter(adapter);

		txtHomeTeam.setText(match.getHomeTeam());
		txtGuestTeam.setText(match.getGuestTeam());
		txtResult.setText(match.getResult());

		timeline.createLayout(match);

		createHeader();

		return fragmentView;
	}

	/**
	 * Initializes variables for views. Gets all views from the activity
	 * specified by a variable.
	 */
	public void init() {

		tickerList = (ListView) fragmentView.findViewById(R.id.ticker_list);
		txtHomeTeam = (TextView) fragmentView.findViewById(R.id.txt_home_team);
		txtGuestTeam = (TextView) fragmentView.findViewById(R.id.txt_guest_team);
		txtResult = (TextView) fragmentView.findViewById(R.id.txt_result);
		txtGoalsHome = (GoalText) fragmentView.findViewById(R.id.txt_goals_home);
		txtGoalsGuest = (GoalText) fragmentView.findViewById(R.id.txt_goals_guest);

		timeline = (Timeline) fragmentView.findViewById(R.id.ticker_timeline);
		timeline.getViewTreeObserver().addOnGlobalLayoutListener(this);

	}

	public void createHeader() {

		String homeText = "";
		String guestText = "";

		goalsHome = new ArrayList<TickerEvent>();
		goalsGuest = new ArrayList<TickerEvent>();
		// get the goals
		for (TickerEvent event : events) {
			if (event.getType() == EventType.GOAL)
				if (event.getTeam().equals(match.getHomeTeam())) {
					goalsHome.add(event);
				} else {
					goalsGuest.add(event);
				}
		}
		Collections.reverse(goalsHome);
		Collections.reverse(goalsGuest);

		for (TickerEvent goal : goalsHome) {
			String tmp = goal.getPlayer() + " (" + goal.getMinute() + ".)\n";
			homeText += tmp;
		}
		for (TickerEvent goal : goalsGuest) {
			String tmp = goal.getPlayer() + " (" + goal.getMinute() + ".)\n";
			guestText += tmp;
		}

		txtGoalsHome.setText(homeText);
		txtGoalsGuest.setText(guestText);
	}

	/**
	 * Scrolls to the item closest to the given minute. If there is no item at
	 * the given minute, it scrolls to the closest one before or after
	 * 
	 * @param minute
	 *            The minute to scroll to.
	 */
	public void scrollToMinute(int minute) {

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

			tickerList.setSelectionFromTop(index, SCROLL_OFFSET_TOP);

		} else if (index != 0) {

			int earlier = events.get(index).getMinute();
			int later = events.get(index - 1).getMinute();

			int deltaEarlier = minute - earlier;
			int deltaLater = later - minute;

			if (deltaEarlier < deltaLater) {
				// tickerList.smoothScrollToPositionFromTop(index,
				// SCROLL_OFFSET_TOP);
				tickerList.setSelectionFromTop(index, SCROLL_OFFSET_TOP);
			} else {
				// tickerList.smoothScrollToPositionFromTop(index - 1,
				// SCROLL_OFFSET_TOP);
				tickerList.setSelectionFromTop(index - 1, SCROLL_OFFSET_TOP);
			}
		} else {
			tickerList.smoothScrollToPosition(0);
		}

	}

	public void scrollToEvent(TickerEvent event) {

		tickerList.setSelectionFromTop(events.indexOf(event), SCROLL_OFFSET_TOP);
	}

	@Override
	public void onGlobalLayout() {

		TickerScrollListener myListener = new TickerScrollListener(timeline);
		tickerList.setOnScrollListener(myListener);
		timeline.setIconInfo(events, this);

		timeline.setOnTouchListener(new TimelineTouchListener(tickerList,
				TickerFragment.this, timeline));

		timeline.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();

		// if (txtGoalsHome.getLayout().getLineCount() > 2) {
		scrollTimer = new Timer();
		scrollTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {

						ObjectAnimator animator;

						if (goalsHome.size() > scrollPositionHome + 2
								&& goalsGuest.size() > scrollPositionGuest + 2) {
							// both teams have more than two goals

							TickerEvent nextGoalHome = goalsHome
									.get(scrollPositionHome + 2);
							TickerEvent nextGoalGuest = goalsGuest
									.get(scrollPositionGuest + 2);
							if (nextGoalHome.getMinute() < nextGoalGuest.getMinute()) {
								scrollPositionHome++;

								animator = ObjectAnimator.ofInt(txtGoalsHome,
										"scrollPosition", txtGoalsHome.getLayout()
												.getLineTop(scrollPositionHome));
								animator.start();

							} else {
								scrollPositionGuest++;
								animator = ObjectAnimator.ofInt(txtGoalsGuest,
										"scrollPosition", txtGoalsGuest.getLayout()
												.getLineTop(scrollPositionGuest));
								animator.start();
							}

						} else if (goalsHome.size() > scrollPositionHome + 2) {
							// only home team has more than two goals

							scrollPositionHome++;
							animator = ObjectAnimator.ofInt(txtGoalsHome,
									"scrollPosition", txtGoalsHome.getLayout()
											.getLineTop(scrollPositionHome));
							animator.start();

						} else if (goalsGuest.size() > scrollPositionGuest + 2) {
							// only guest team has more than two goals

							scrollPositionGuest++;
							animator = ObjectAnimator.ofInt(txtGoalsGuest,
									"scrollPosition", txtGoalsGuest.getLayout()
											.getLineTop(scrollPositionGuest));
							animator.start();

						} else {
							// both teams don't have more than two goals left

							int currentHomePosition = txtGoalsHome.getLayout()
									.getLineTop(scrollPositionHome);
							int currentGuestPosition = txtGoalsGuest.getLayout()
									.getLineTop(scrollPositionHome);

							// animations to scroll the text out of sight

							ObjectAnimator scrollHomeOut = ObjectAnimator.ofInt(
									txtGoalsHome, "scrollPosition", currentHomePosition,
									currentHomePosition + 100);

							ObjectAnimator scrollGuestOut = ObjectAnimator.ofInt(
									txtGoalsGuest, "scrollPosition",
									currentGuestPosition, currentGuestPosition + 100);

							scrollPositionHome = 0;
							scrollPositionGuest = 0;

							ObjectAnimator scrollHomeIn = ObjectAnimator.ofInt(
									txtGoalsHome, "scrollPosition", -100, txtGoalsHome
											.getLayout().getLineTop(scrollPositionHome));

							ObjectAnimator scrollGuestIn = ObjectAnimator.ofInt(
									txtGoalsGuest, "scrollPosition", -100, txtGoalsGuest
											.getLayout().getLineTop(scrollPositionHome));

							AnimatorSet set = new AnimatorSet();
							set.play(scrollHomeOut).with(scrollGuestOut);
							set.play(scrollHomeIn).with(scrollGuestIn);
							set.play(scrollHomeIn).after(scrollHomeOut);

							set.start();
						}
					}
				});
			}
		}, HEADER_SCROLL_DELAY, HEADER_SCROLL_DELAY);
		// }
	}

	@Override
	public void onPause() {
		super.onPause();
		scrollTimer.cancel();
		scrollTimer.purge();
		System.out.println("Task cancelled");
	}

	public void parseMatch(InputStream is) {
		this.is = is;
	}
}
