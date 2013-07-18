package com.ipol.sponticker;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.ipol.sponticker.model.TickerEvent;

public class TickerScrollListener implements OnScrollListener {

	private View pointer;
	private View firstHalfTimeline;
	private View secondHalfTimeline;

	private int barHeight;
	private int timelineGap;
	private float sizeOfMinute;

	public TickerScrollListener(View pointer, View firstHalfTimeline,
			View secondHalfTimeline) {
		this.pointer = pointer;
		this.firstHalfTimeline = firstHalfTimeline;
		this.secondHalfTimeline = secondHalfTimeline;

		barHeight = firstHalfTimeline.getHeight();
		timelineGap = secondHalfTimeline.getBottom()
				- firstHalfTimeline.getTop();
		System.out.println("Gap "+timelineGap);
		sizeOfMinute = barHeight / 45;

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		TickerEvent firstEvent = (TickerEvent) view.getAdapter().getItem(
				firstVisibleItem + 1);

		int minute = firstEvent.getMinute();
		if (minute < 45) {
			pointer.setTranslationY(-minute * sizeOfMinute);
			// movePointer(Math.round(-minute * sizeOfMinute));
		} else {
			pointer.setTranslationY(-minute * sizeOfMinute + timelineGap);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	public void movePointer(final int amount) {

		int fromXDelta = pointer.getScrollX();
		final int fromYDelta = Math.round(pointer.getTranslationY());
		System.out.println("translate= " + pointer.getTranslationY()
				+ " scroll= " + pointer.getScrollY());

		TranslateAnimation animation = new TranslateAnimation(fromXDelta,
				fromXDelta, fromYDelta, amount - fromYDelta);
		animation.setDuration(200);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				pointer.clearAnimation();
				pointer.setTranslationY(amount);
			}
		});

		pointer.startAnimation(animation);
	}

}
