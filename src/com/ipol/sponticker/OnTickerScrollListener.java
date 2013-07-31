package com.ipol.sponticker;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.ipol.sponticker.gui.TimelineView;
import com.ipol.sponticker.model.TickerEvent;

public class OnTickerScrollListener implements OnScrollListener {

	private TimelineView timeline;
	private boolean isIdle = true;

	public OnTickerScrollListener(TimelineView timeline) {

		this.timeline = timeline;
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		

		if (!isIdle) {

			TickerEvent firstEvent = (TickerEvent) view.getAdapter().getItem(
					firstVisibleItem + 1);

			int minute = firstEvent.getMinute();

			if (minute <= 45) {
				timeline.pointer.setTranslationY((int) (-minute * timeline.minuteHeight));
			} else if (minute <= 90) {
				timeline.pointer
						.setTranslationY((int) (-minute * timeline.minuteHeight - timeline.txtMinuteBreak
								.getHeight()));
			} else if (minute > 90) {
				timeline.pointer.setTranslationY((int) (-minute * timeline.minuteHeight
						- timeline.txtMinuteBreak.getHeight() - timeline.txtMinuteNinety
						.getHeight()));
			}
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_TOUCH_SCROLL:
			isIdle = false;
			break;
		case SCROLL_STATE_FLING:
			isIdle = false;
			break;
		case SCROLL_STATE_IDLE:
			isIdle = true;
			break;
		}
	}
}
