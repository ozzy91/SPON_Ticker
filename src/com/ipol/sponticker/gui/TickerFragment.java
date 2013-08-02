package com.ipol.sponticker.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ipol.sponticker.OnTickerScrollListener;
import com.ipol.sponticker.OnTimelineTouchListener;
import com.ipol.sponticker.R;
import com.ipol.sponticker.TickerAdapter;
import com.ipol.sponticker.TickerDataParser;
import com.ipol.sponticker.model.EventType;
import com.ipol.sponticker.model.TickerEvent;
import com.ipol.sponticker.model.TickerMatch;

public class TickerFragment extends Fragment implements OnGlobalLayoutListener {

	private static final String MATCH_FILE = "CL_finale.json";

	private static final int SCROLL_OFFSET_TOP = 100;
	private static final long HEADER_SCROLL_DELAY = 3000;

	private Activity activity;

	// Views
	private View fragmentView;
	private PullToRefreshListView tickerList;
	private TextView txtHomeTeam;
	private TextView txtGuestTeam;
	private TextView txtResult;
	private GoalTextView txtGoalsHome;
	private GoalTextView txtGoalsGuest;
	private ImageView imgHomeTeam;
	private ImageView imgGuestTeam;

	private TimelineView timeline;

	// Data
	private TickerMatch match;
	private ArrayList<TickerEvent> events;
	private ArrayList<TickerEvent> goalsHome;
	private ArrayList<TickerEvent> goalsGuest;

	private Timer scrollTimer;
	private OnTickerScrollListener myListener;
	private int scrollPositionHome = 0;
	private int scrollPositionGuest = 0;
	private boolean isInit = false;

	InputStream is = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		fragmentView = inflater.inflate(R.layout.fragment_ticker, container, false);

		activity = getActivity();

		InputStream is = null;
		try {
			is = activity.getAssets().open("matches/" + MATCH_FILE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		parseMatch(is);

		return fragmentView;
	}

	public void init() {

		tickerList = (PullToRefreshListView) fragmentView.findViewById(R.id.ticker_list);
		txtHomeTeam = (TextView) fragmentView.findViewById(R.id.txt_home_team);
		txtGuestTeam = (TextView) fragmentView.findViewById(R.id.txt_guest_team);
		txtResult = (TextView) fragmentView.findViewById(R.id.txt_result);
		txtGoalsHome = (GoalTextView) fragmentView.findViewById(R.id.txt_goals_home);
		txtGoalsGuest = (GoalTextView) fragmentView.findViewById(R.id.txt_goals_guest);
		imgHomeTeam = (ImageView) fragmentView.findViewById(R.id.img_home_team);
		imgGuestTeam = (ImageView) fragmentView.findViewById(R.id.img_guest_team);

		timeline = (TimelineView) fragmentView.findViewById(R.id.ticker_timeline);
		timeline.getViewTreeObserver().addOnGlobalLayoutListener(this);
		
		ILoadingLayout loadingLayout = tickerList.getLoadingLayoutProxy();
		loadingLayout.setRefreshingLabel("Wird geladen...");
		loadingLayout.setReleaseLabel("Zum Aktualisieren loslassen");
		loadingLayout.setPullLabel("Zum Aktualisieren herunter ziehen");
		tickerList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				new RefreshTask().execute();
			}
			
		});

	}

	public void createHeader() {

		txtHomeTeam.setText(match.getHomeShortname());
		txtGuestTeam.setText(match.getGuestShortname());
		txtResult.setText(match.getResult());

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

			tickerList.getRefreshableView().setSelectionFromTop(index, SCROLL_OFFSET_TOP);

		} else if (index != 0) {

			int earlier = events.get(index).getMinute();
			int later = events.get(index - 1).getMinute();

			int deltaEarlier = minute - earlier;
			int deltaLater = later - minute;

			if (deltaEarlier < deltaLater) {
				tickerList.getRefreshableView().setSelectionFromTop(index, SCROLL_OFFSET_TOP);
			} else {
				tickerList.getRefreshableView().setSelectionFromTop(index - 1, SCROLL_OFFSET_TOP);
			}
		} else {
			if (minute > events.get(0).getMinute()) {
				tickerList.getRefreshableView().setSelection(0);
			} else {
				tickerList.getRefreshableView().setSelection(events.size() - 1);
			}
		}

	}

	public void scrollToEvent(TickerEvent event) {

		tickerList.getRefreshableView().setSelectionFromTop(events.indexOf(event), SCROLL_OFFSET_TOP);
	}

	@SuppressWarnings("deprecation")
	public void onGlobalLayout() {

		myListener = new OnTickerScrollListener(timeline);
		tickerList.setOnScrollListener(myListener);
		timeline.setIconInfo(events, this);

		timeline.setOnTouchListener(new OnTimelineTouchListener(tickerList,
				TickerFragment.this, timeline));
		tickerList.getRefreshableView().setSelection(0);
		timeline.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		scrollPositionGuest = 0;
		scrollPositionHome = 0;
		if (isInit && scrollTimer == null) {
			if (goalsHome.size() > 2 || goalsGuest.size() > 2) {
				scrollTimer = new Timer();
				scrollTimer.scheduleAtFixedRate(new ScrollTimerTask(),
						HEADER_SCROLL_DELAY, HEADER_SCROLL_DELAY);
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (scrollTimer != null) {
			scrollTimer.cancel();
			scrollTimer.purge();
			scrollTimer = null;
		}
	}

	@SuppressWarnings("unchecked")
	public void setTickerData(Map<String, Object> tickerData) {

		boolean matchStarted = false;

		this.events = (ArrayList<TickerEvent>) tickerData.get("events");
		this.match = (TickerMatch) tickerData.get("match");

		init();

		Collections.sort(events);

		ArrayList<TickerEvent> adapterEvents = new ArrayList<TickerEvent>();

		TickerEvent stadiumInfo = new TickerEvent();
		stadiumInfo.setCommentary(match.getStadium());
		adapterEvents.add(0, stadiumInfo);

		TickerEvent started = new TickerEvent();
		started.setCommentary("Anpfiff");

		for (TickerEvent ev : events) {
			if (!matchStarted && ev.getMinute() == 0) {
				adapterEvents.add(started);
				matchStarted = true;
			}
			adapterEvents.add(ev);
		}

		if (!matchStarted)
			adapterEvents.add(started);

		TickerAdapter adapter = new TickerAdapter(activity, R.layout.item_ticker_event,
				adapterEvents);
		tickerList.setAdapter(adapter);

		timeline.createLayout(match);
		createHeader();

		if (goalsHome.size() > 2 || goalsGuest.size() > 2) {
			scrollTimer = new Timer();
			scrollTimer.scheduleAtFixedRate(new ScrollTimerTask(), HEADER_SCROLL_DELAY,
					HEADER_SCROLL_DELAY);
		}

		this.isInit = true;
	}

	public void parseMatch(InputStream is) {

		List<TickerEvent> events;
		TickerMatch match;
		HashMap<String, Object> tickerData = new HashMap<String, Object>();

		TickerDataParser parser = new TickerDataParser();
		JSONObject obj = parser.getJSON(is);
		JSONArray eventArray = parser.getEventArray(obj);
		events = parser.getEvents(eventArray);
		match = parser.getMatch(obj);

		match.setHomeShortname(match.getHomeTeam());
		match.setGuestShortname(match.getGuestTeam());

		tickerData.put("events", events);
		tickerData.put("match", match);

		setTickerData(tickerData);
	}

	private class ScrollTimerTask extends TimerTask {

		@Override
		public void run() {
			activity.runOnUiThread(new Runnable() {

				public void run() {

					ObjectAnimator animator;

					if (goalsHome.size() > scrollPositionHome + 2
							&& goalsGuest.size() > scrollPositionGuest + 2) {
						// both teams have more than two goals

						TickerEvent nextGoalHome = goalsHome.get(scrollPositionHome + 2);
						TickerEvent nextGoalGuest = goalsGuest
								.get(scrollPositionGuest + 2);
						if (nextGoalHome.getMinute() < nextGoalGuest.getMinute()) {
							scrollPositionHome++;

							animator = ObjectAnimator.ofInt(
									txtGoalsHome,
									"scrollPosition",
									txtGoalsHome.getLayout().getLineTop(
											scrollPositionHome - 1),
									txtGoalsHome.getLayout().getLineTop(
											scrollPositionHome));
							animator.start();

						} else {
							scrollPositionGuest++;
							animator = ObjectAnimator.ofInt(
									txtGoalsGuest,
									"scrollPosition",
									txtGoalsGuest.getLayout().getLineTop(
											scrollPositionGuest - 1),
									txtGoalsGuest.getLayout().getLineTop(
											scrollPositionGuest));
							animator.start();
						}

					} else if (goalsHome.size() > scrollPositionHome + 2) {
						// only home team has more than two goals

						scrollPositionHome++;
						animator = ObjectAnimator.ofInt(
								txtGoalsHome,
								"scrollPosition",
								txtGoalsHome.getLayout().getLineTop(
										scrollPositionHome - 1), txtGoalsHome.getLayout()
										.getLineTop(scrollPositionHome));
						animator.start();

					} else if (goalsGuest.size() > scrollPositionGuest + 2) {
						// only guest team has more than two goals

						scrollPositionGuest++;
						animator = ObjectAnimator.ofInt(
								txtGoalsGuest,
								"scrollPosition",
								txtGoalsGuest.getLayout().getLineTop(
										scrollPositionGuest - 1), txtGoalsGuest
										.getLayout().getLineTop(scrollPositionGuest));
						animator.start();

					} else {
						// both teams don't have more than two goals left

						int currentHomePosition = txtGoalsHome.getLayout().getLineTop(
								scrollPositionHome);
						int currentGuestPosition = txtGoalsGuest.getLayout().getLineTop(
								scrollPositionHome);

						// animations to scroll the text out of sight

						ObjectAnimator scrollHomeOut = ObjectAnimator.ofInt(txtGoalsHome,
								"scrollPosition", currentHomePosition,
								currentHomePosition + 100);

						ObjectAnimator scrollGuestOut = ObjectAnimator.ofInt(
								txtGoalsGuest, "scrollPosition", currentGuestPosition,
								currentGuestPosition + 100);

						scrollPositionHome = 0;
						scrollPositionGuest = 0;

						ObjectAnimator scrollHomeIn = ObjectAnimator.ofInt(txtGoalsHome,
								"scrollPosition", -100, txtGoalsHome.getLayout()
										.getLineTop(scrollPositionHome));

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
	}
	
	private class RefreshTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			tickerList.onRefreshComplete();
			super.onPostExecute(result);
		}
		
		
	}
}
