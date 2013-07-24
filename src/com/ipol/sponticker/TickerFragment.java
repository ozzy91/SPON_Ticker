package com.ipol.sponticker;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ipol.sponticker.model.EventType;
import com.ipol.sponticker.model.Match;
import com.ipol.sponticker.model.TickerEvent;

public class TickerFragment extends Fragment implements OnGlobalLayoutListener {

	private static final int SCROLL_OFFSET_TOP = 100;
	private static final int ICON_GOAL_TOUCH_RADIUS = 20;
	private static final int ICON_OTHER_TOUCH_RADIUS = 5;
	private static final long HEADER_SCROLL_DELAY = 5000;
	private static int TIMELINE_ICON_SIZE;

	private Activity activity;

	// Views
	private View fragmentView;
	private ListView tickerList;
	private TextView txtHomeTeam;
	private TextView txtGuestTeam;
	private TextView txtResult;
	private TextView txtGoalsHome;
	private TextView txtGoalsGuest;

	private Timeline timeline;

	// Data
	private Match match;
	private ArrayList<TickerEvent> events;
	private ArrayList<LinearLayout> goalIcons;

	public float sizeOfMinute;
	public float timelineGap;

	private Timer scrollTimer;
	private int scrollCount = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		activity = getActivity();
		fragmentView = inflater.inflate(R.layout.ticker_fragment, container, false);

		init();

		InputStream is = null;
		try {
			is = activity.getAssets().open("1874414.json");
		} catch (IOException e1) {
			e1.printStackTrace();
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

		TIMELINE_ICON_SIZE = (int) activity.getResources().getDimension(
				R.dimen.timeline_icon_size);

		tickerList = (ListView) fragmentView.findViewById(R.id.ticker_list);
		txtHomeTeam = (TextView) fragmentView.findViewById(R.id.txt_home_team);
		txtGuestTeam = (TextView) fragmentView.findViewById(R.id.txt_guest_team);
		txtResult = (TextView) fragmentView.findViewById(R.id.txt_result);
		txtGoalsHome = (TextView) fragmentView.findViewById(R.id.txt_goals_home);
		txtGoalsGuest = (TextView) fragmentView.findViewById(R.id.txt_goals_guest);

		timeline = (Timeline) fragmentView.findViewById(R.id.ticker_timeline);
		timeline.getViewTreeObserver().addOnGlobalLayoutListener(this);

	}

	public void createHeader() {

		String homeText = "";
		String guestText = "";

		// get the goals
		for (TickerEvent event : events) {
			if (event.getType() == EventType.GOAL) {
				TextView goalText = new TextView(activity);
				goalText.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
				String tmp = event.getPlayer() + " (" + event.getMinute() + ".)\n";
				goalText.setText(event.getPlayer() + " (" + event.getMinute() + ".)");
				goalText.setTextColor(Color.WHITE);
				if (event.getTeam().equals(match.getHomeTeam())) {
					homeText += tmp;
					homeText += tmp;
				} else {
					guestText += tmp;
				}
			}
		}

		txtGoalsHome.setText(homeText);
		txtGoalsGuest.setText(guestText);

	}

	/**
	 * Adds the icons on the timeline for special events.
	 * 
	 * @param match
	 *            The match with the information about events.
	 */
	public void addIcons() {

		goalIcons = new ArrayList<LinearLayout>();

		for (TickerEvent event : events) {

			LinearLayout icon = createTimelineIcon(event);

			if (icon != null) {

				int minute = event.getMinute();
				if (minute < 44) {
					icon.setY(-event.getMinute() * sizeOfMinute - icon.getHeight() / 2);
				} else {
					icon.setY(-event.getMinute() * sizeOfMinute + timelineGap
							- icon.getHeight() / 2);
				}
				// timelineLayout.addView(icon);
			}

		}

		for (LinearLayout goalIcon : goalIcons) {
			goalIcon.bringToFront();
		}
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
			// tickerList.smoothScrollToPositionFromTop(index,
			// SCROLL_OFFSET_TOP);
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

	private LinearLayout createTimelineIcon(final TickerEvent tickerEvent) {

		ImageView icon = new ImageView(activity);
		final LinearLayout linear = new LinearLayout(activity);
		LayoutParams params = null;
		android.widget.LinearLayout.LayoutParams iconParams = new android.widget.LinearLayout.LayoutParams(
				TIMELINE_ICON_SIZE, TIMELINE_ICON_SIZE);
		iconParams.gravity = Gravity.CENTER_VERTICAL;

		switch (tickerEvent.getType()) {
		case GOAL:
			icon.setImageResource(R.drawable.timeline_ball_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2
					* ICON_GOAL_TOUCH_RADIUS);
			iconParams.setMargins(0, 0, 0, 0);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_GOAL_TOUCH_RADIUS);
			goalIcons.add(linear);
			break;
		case SUBSTITUTE:
			icon.setImageResource(R.drawable.timeline_substitution_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2
					* ICON_OTHER_TOUCH_RADIUS);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_OTHER_TOUCH_RADIUS);
			iconParams.setMargins(80, 0, 0, 0);
			break;
		case YELLOW:
			icon.setImageResource(R.drawable.timeline_card_yellow_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2
					* ICON_OTHER_TOUCH_RADIUS);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_OTHER_TOUCH_RADIUS);
			iconParams.setMargins(40, 0, 0, 0);
			break;
		case YELLOWRED:
			icon.setImageResource(R.drawable.timeline_card_yellowred_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2
					* ICON_OTHER_TOUCH_RADIUS);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_OTHER_TOUCH_RADIUS);
			iconParams.setMargins(40, 0, 0, 0);
			break;
		case RED:
			icon.setImageResource(R.drawable.timeline_card_red_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2
					* ICON_OTHER_TOUCH_RADIUS);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_OTHER_TOUCH_RADIUS);
			iconParams.setMargins(40, 0, 0, 0);
			break;
		case NORMAL:
			return null;
		}

		linear.setLayoutParams(params);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		linear.setClickable(true);
		linear.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

					float touchedY = event.getRawY();
					System.out.println("touched " + touchedY + " bottom "
							+ linear.getBottom());
					tickerList.setSelectionFromTop(events.indexOf(tickerEvent),
							SCROLL_OFFSET_TOP);
				}

				return false;
			}
		});

		linear.addView(icon, iconParams);

		return linear;
	}

	@Override
	public void onGlobalLayout() {

		TickerScrollListener myListener = new TickerScrollListener(timeline);
		tickerList.setOnScrollListener(myListener);
		timeline.setIconInfo(events, this);
		//
		// timelineLayout.setOnTouchListener(new
		// TimelineTouchListener(tickerList,
		// TickerFragment.this, firstHalfTimeline, secondHalfTimeline));
		
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

						scrollCount++;
						if (scrollCount >= txtGoalsHome.getLayout().getLineCount() - 1) {
							scrollCount = 0;
						}

						txtGoalsHome.scrollTo(0,
								txtGoalsHome.getLayout().getLineTop(scrollCount));

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
}
