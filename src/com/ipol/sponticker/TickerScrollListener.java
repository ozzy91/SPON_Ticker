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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}
}
