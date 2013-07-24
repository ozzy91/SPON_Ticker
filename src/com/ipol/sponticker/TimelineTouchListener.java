package com.ipol.sponticker;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ListView;

public class TimelineTouchListener implements OnTouchListener {

	private ListView list;
	private TickerFragment fragment;
	private View firstHalfTimeline;
	private View secondHalfTimeline;

	public TimelineTouchListener(ListView list, TickerFragment tickerFragment,
			View firstHalfTimeline, View secondHalfTimeline) {

		this.list = list;
		this.fragment = tickerFragment;
		this.firstHalfTimeline = firstHalfTimeline;
		this.secondHalfTimeline = secondHalfTimeline;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		System.out.println("onTouch");

		if (event.getActionMasked() == MotionEvent.ACTION_DOWN
				|| event.getActionMasked() == MotionEvent.ACTION_MOVE) {
			float touchedY = event.getY();

			// if user clicks on the 0 minutes label
			if (touchedY > firstHalfTimeline.getBottom()) {
				list.smoothScrollToPosition((list.getAdapter().getCount() - 1));
			}

			// if user clicks on the max minutes label
			else if (touchedY < secondHalfTimeline.getTop()) {
				list.smoothScrollToPosition(0);
			}
			// if user clicks on the space between halftimes
			else if (touchedY > secondHalfTimeline.getBottom()
					&& touchedY < firstHalfTimeline.getTop()) {
				fragment.scrollToMinute(45);
			}

			// if user clicks on the timeline
			else {

				
				// clicked on the firstHalfTimeline
				if (touchedY > firstHalfTimeline.getTop()) {
					float minute = (touchedY - firstHalfTimeline.getTop())
							/ fragment.sizeOfMinute;

					fragment.scrollToMinute(45 - (int) minute);
				} else {
					// clicked on the secondHalfTime
					float minute = (touchedY - secondHalfTimeline.getTop())
							/ fragment.sizeOfMinute;

					fragment.scrollToMinute(90 - (int) minute);
				}

			}

		}

		return true;
	}
}
