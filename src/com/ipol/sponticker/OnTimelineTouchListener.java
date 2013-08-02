package com.ipol.sponticker;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ipol.sponticker.gui.TickerFragment;
import com.ipol.sponticker.gui.TimelineView;

public class OnTimelineTouchListener implements OnTouchListener {

	private PullToRefreshListView list;
	private TickerFragment fragment;
	private TimelineView timeline;
	private float lastX;
	private float lastY;
	private float currX;
	private float currY;
	private float diffX;
	private float diffY;

	private boolean scrollVertical;

	public OnTimelineTouchListener(PullToRefreshListView list, TickerFragment tickerFragment,
			TimelineView timeline) {

		this.list = list;
		this.fragment = tickerFragment;
		this.timeline = timeline;
	}

	public boolean onTouch(View v, MotionEvent event) {

		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			lastX = event.getRawX();
			lastY = event.getRawY();
		}
		if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
			currX = event.getRawX();
			currY = event.getRawY();
			diffX = currX - lastX;
			diffY = currY - lastY;

			if (Math.abs(diffY) > Math.abs(diffX))
				scrollVertical = true;
			else
				scrollVertical = false;
		}

		if (scrollVertical
				&& (event.getActionMasked() == MotionEvent.ACTION_DOWN || event
						.getActionMasked() == MotionEvent.ACTION_MOVE)) {

			v.getParent().requestDisallowInterceptTouchEvent(true);

			float touchedY = event.getY();

			// move the pointer, but only if it's within the timeline bounds
			if (touchedY < timeline.txtMinuteZero.getTop()
					&& touchedY > timeline.txtMinuteEnd.getBottom())
				timeline.pointer.setTranslationY(touchedY - timeline.getHeight()
						+ timeline.txtMinuteZero.getHeight());

			// if user clicks on the 0 minutes label
			if (touchedY < timeline.txtMinuteZero.getBottom()
					&& touchedY > timeline.txtMinuteZero.getTop()) {
				list.getRefreshableView().smoothScrollToPosition((list.getRefreshableView().getAdapter().getCount() - 1));
			}

			// if user clicks on the max minutes label
			else if (touchedY < timeline.txtMinuteEnd.getBottom()
					&& touchedY > timeline.txtMinuteEnd.getTop()) {
				list.getRefreshableView().smoothScrollToPosition(0);
			}
			// if user clicks on the halftime break label, only if minute is
			// after 45
			else if (timeline.minute > 45
					&& touchedY < timeline.txtMinuteBreak.getBottom()
					&& touchedY > timeline.txtMinuteBreak.getTop()) {
				fragment.scrollToMinute(45);
			}

			// if user clicks on the 90minute break label, only if minute is
			// after 90
			else if (timeline.minute > 90
					&& touchedY < timeline.txtMinuteNinety.getBottom()
					&& touchedY > timeline.txtMinuteNinety.getTop()) {
				fragment.scrollToMinute(90);
			}

			// if user clicks on the timeline
			else {

				// clicked on the firstHalfTimeline
				if (touchedY > timeline.firstHalfTimeline.getTop()) {

					double minute = (touchedY - timeline.firstHalfTimeline.getTop())
							/ timeline.minuteHeight;
					fragment.scrollToMinute(45 - (int) minute);

				} else if (touchedY > timeline.secondHalfTimeline.getTop()) {
					// clicked on the secondHalfTime
					double minute = (touchedY - timeline.secondHalfTimeline.getTop())
							/ timeline.minuteHeight;
					fragment.scrollToMinute(90 - (int) minute);

				} else if (timeline.thirdTimeline != null
						&& touchedY > timeline.thirdTimeline.getTop()) {
					// clicked on the third timeline
					double minute = (touchedY - timeline.thirdTimeline.getTop())
							/ timeline.minuteHeight;
					fragment.scrollToMinute(120 - (int) minute);
				}

			}
		}

		return true;
	}
}
