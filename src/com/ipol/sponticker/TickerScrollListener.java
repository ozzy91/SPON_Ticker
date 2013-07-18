package com.ipol.sponticker;

import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.ipol.sponticker.model.TickerEvent;

public class TickerScrollListener implements OnScrollListener {

	private View pointer;
	private float sizeOfMinute;
	private float timelineGap;

	public TickerScrollListener(View pointer, float sizeOfMinute, float timelineGap) {

		this.pointer = pointer;
		this.sizeOfMinute = sizeOfMinute;
		this.timelineGap = timelineGap;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
			int totalItemCount) {

		TickerEvent firstEvent = (TickerEvent) view.getAdapter().getItem(
				firstVisibleItem + 1);

		int minute = firstEvent.getMinute();

		if (minute <= 45) {
			pointer.setTranslationY(-minute * sizeOfMinute);
		} else {
			pointer.setTranslationY(-minute * sizeOfMinute + timelineGap);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}
}
