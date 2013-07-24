package com.ipol.sponticker;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.ipol.sponticker.model.TickerEvent;

public class TickerScrollListener implements OnScrollListener {

	private Timeline timeline;

	public TickerScrollListener(Timeline timeline) {

		this.timeline = timeline;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {
		
		System.out.println("scroll");
		System.out.println(timeline.minuteHeight);
		System.out.println(timeline.timelineGap);

		TickerEvent firstEvent = (TickerEvent) view.getAdapter().getItem(
				firstVisibleItem + 1);

		int minute = firstEvent.getMinute();
		

		if (minute <= 45) {
			timeline.pointer.setTranslationY((int) (-minute * timeline.minuteHeight));
		} else {
			timeline.pointer
					.setTranslationY((int) (-minute * timeline.minuteHeight - timeline.timelineGap));
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}
}
