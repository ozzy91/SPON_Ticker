package com.ipol.sponticker.gui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipol.sponticker.R;
import com.ipol.sponticker.model.TickerEvent;
import com.ipol.sponticker.model.TickerMatch;

public class TimelineView extends RelativeLayout {

	private static int ICON_GOAL_TOUCH_RADIUS;
	private static int ICON_OTHER_TOUCH_RADIUS;
	private static int TIMELINE_ICON_SIZE;
	private static int TIMELINE_THICKNESS;

	private Context context;
	public int minute;
	public int timelineGap;
	public double minuteHeight;

	public View firstHalfTimeline;
	public View secondHalfTimeline;
	public View thirdTimeline;
	public View pointer;

	public TextView txtMinuteZero;
	public TextView txtMinuteBreak;
	public TextView txtMinuteNinety;
	public TextView txtMinuteEnd;

	private ArrayList<LinearLayout> goalIcons;
	private List<TickerEvent> events;
	private TickerFragment fragment;

	public TimelineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

	}

	public void createLayout(TickerMatch match) {

		TIMELINE_THICKNESS = (int) context.getResources().getDimension(R.dimen.timeline_thickness);
		TIMELINE_ICON_SIZE = (int) context.getResources().getDimension(R.dimen.timeline_icon_size);
		ICON_GOAL_TOUCH_RADIUS = (int) context.getResources().getDimension(R.dimen.timeline_icon_goal_touch_radius);
		ICON_OTHER_TOUCH_RADIUS = (int) context.getResources().getDimension(R.dimen.timeline_icon_other_touch_radius);
		final int timelineMarginLeft = TIMELINE_ICON_SIZE / 2 - TIMELINE_THICKNESS / 2;
		System.out.println("marginLeft " + timelineMarginLeft);

		minute = match.getMinute();
		LayoutParams params;

		txtMinuteZero = (TextView) findViewById(R.id.timeline_txt_minute_zero);
		txtMinuteEnd = (TextView) findViewById(R.id.timeline_txt_minute_current);
		txtMinuteEnd.setText(minute + ".");
		txtMinuteBreak = (TextView) findViewById(R.id.timeline_txt_minute_break);
		txtMinuteNinety = (TextView) findViewById(R.id.timeline_txt_minute_ninety);
		firstHalfTimeline = findViewById(R.id.timeline_first_half);
		pointer = findViewById(R.id.timeline_pointer);

		if (minute > 45) {

			txtMinuteBreak.setVisibility(View.VISIBLE);

			params = new LayoutParams(TIMELINE_THICKNESS, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ABOVE, R.id.timeline_txt_minute_zero);
			firstHalfTimeline.setLayoutParams(params);

			secondHalfTimeline = findViewById(R.id.timeline_second_half);
			secondHalfTimeline.setVisibility(View.VISIBLE);

			if (minute <= 90) {
				// after all Views are in layout, calculate the right sizes
				txtMinuteBreak.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					public void onGlobalLayout() {
						doOnGlobalLayout();

						LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						params.addRule(RelativeLayout.ABOVE, R.id.timeline_first_half);
						txtMinuteBreak.setLayoutParams(params);

						LayoutParams par = new LayoutParams(TIMELINE_THICKNESS, (int) (minuteHeight * 45));
						par.addRule(RelativeLayout.ABOVE, R.id.timeline_txt_minute_zero);
						firstHalfTimeline.setLayoutParams(par);

						// remove listener, not needed after first call
						txtMinuteBreak.getViewTreeObserver().removeGlobalOnLayoutListener(this);

					}
				});
			}
		}

		if (minute > 90) {
			txtMinuteNinety.setVisibility(View.VISIBLE);

			thirdTimeline = findViewById(R.id.timeline_third_half);
			thirdTimeline.setVisibility(View.VISIBLE);

			// after all Views are in layout, calculate the right sizes
			txtMinuteBreak.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@SuppressWarnings("deprecation")
				public void onGlobalLayout() {
					doOnGlobalLayout();

					LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.ABOVE, R.id.timeline_first_half);
					txtMinuteBreak.setLayoutParams(params);

					params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.ABOVE, R.id.timeline_second_half);
					txtMinuteNinety.setLayoutParams(params);

					LayoutParams par = new LayoutParams(TIMELINE_THICKNESS, (int) (minuteHeight * 45));
					par.addRule(RelativeLayout.ABOVE, R.id.timeline_txt_minute_zero);
					firstHalfTimeline.setLayoutParams(par);

					par = new LayoutParams(TIMELINE_THICKNESS, (int) (minuteHeight * 45));
					par.addRule(RelativeLayout.ABOVE, R.id.timeline_txt_minute_break);
					secondHalfTimeline.setLayoutParams(par);

					// remove listener, not needed after first call
					txtMinuteBreak.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});

		}

		if (minute <= 45) {
			txtMinuteEnd.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@SuppressWarnings("deprecation")
				public void onGlobalLayout() {
					doOnGlobalLayout();
					txtMinuteEnd.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			});
		}

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
			return false;
		} else if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
			
			return true;
		}
		return false;
	}

	public void setIconInfo(List<TickerEvent> events, TickerFragment tickerFragment) {

		this.fragment = tickerFragment;
		this.events = events;
	}

	/**
	 * Adds the icons on the timeline for special events.
	 */
	public void addIcons() {

		goalIcons = new ArrayList<LinearLayout>();

		for (TickerEvent event : events) {

			LinearLayout icon = createTimelineIcon(event);

			if (icon != null) {

				int minute = event.getMinute();
				if (minute <= 45) {
					icon.setY((int) (-event.getMinute() * minuteHeight - icon.getHeight() / 2));
				} else if (minute <= 90) {
					icon.setY((int) (-event.getMinute() * minuteHeight - txtMinuteBreak.getHeight() - icon.getHeight() / 2));
				} else if (minute > 90) {
					icon.setY((int) (-event.getMinute() * minuteHeight - txtMinuteBreak.getHeight() - txtMinuteNinety.getHeight() - icon.getHeight() / 2));
				}
				addView(icon);
			}

		}

		for (LinearLayout goalIcon : goalIcons) {
			goalIcon.bringToFront();
		}
	}

	private LinearLayout createTimelineIcon(final TickerEvent tickerEvent) {

		ImageView icon = new ImageView(context);
		final LinearLayout linear = new LinearLayout(context);
		LayoutParams params = null;
		android.widget.LinearLayout.LayoutParams iconParams = new android.widget.LinearLayout.LayoutParams(TIMELINE_ICON_SIZE, TIMELINE_ICON_SIZE);
		iconParams.gravity = Gravity.CENTER_VERTICAL;

		switch (tickerEvent.getType()) {
		case GOAL:
			icon.setImageResource(R.drawable.timeline_ball_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2 * ICON_GOAL_TOUCH_RADIUS);
			iconParams.setMargins(0, 0, 0, 0);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_GOAL_TOUCH_RADIUS);
			goalIcons.add(linear);
			break;
		case OWNGOAL:
			icon.setImageResource(R.drawable.timeline_ball_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2 * ICON_GOAL_TOUCH_RADIUS);
			iconParams.setMargins(0, 0, 0, 0);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_GOAL_TOUCH_RADIUS);
			goalIcons.add(linear);
			break;
		case SUBSTITUTE:
			icon.setImageResource(R.drawable.timeline_substitution_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2 * ICON_OTHER_TOUCH_RADIUS);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_OTHER_TOUCH_RADIUS);
			iconParams.setMargins(80, 0, 0, 0);
			break;
		case YELLOW:
			icon.setImageResource(R.drawable.timeline_card_yellow_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2 * ICON_OTHER_TOUCH_RADIUS);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_OTHER_TOUCH_RADIUS);
			iconParams.setMargins(40, 0, 0, 0);
			break;
		case YELLOWRED:
			icon.setImageResource(R.drawable.timeline_card_yellowred_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2 * ICON_OTHER_TOUCH_RADIUS);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_OTHER_TOUCH_RADIUS);
			iconParams.setMargins(40, 0, 0, 0);
			break;
		case RED:
			icon.setImageResource(R.drawable.timeline_card_red_2x);
			params = new LayoutParams(LayoutParams.MATCH_PARENT, TIMELINE_ICON_SIZE + 2 * ICON_OTHER_TOUCH_RADIUS);
			params.setMargins(0, 0, 0, -TIMELINE_ICON_SIZE / 2 - ICON_OTHER_TOUCH_RADIUS);
			iconParams.setMargins(40, 0, 0, 0);
			break;
		case PENALTY:
			return null;
		case NORMAL:
			return null;
		}

		params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.timeline_first_half);
		params.addRule(RelativeLayout.ALIGN_LEFT, R.id.timeline_first_half);
		linear.setLayoutParams(params);
		linear.setOrientation(LinearLayout.HORIZONTAL);
		linear.setClickable(true);
		linear.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {

					pointer.setTranslationY(linear.getY() - getHeight() + txtMinuteZero.getHeight() + linear.getHeight() / 2);

					fragment.scrollToEvent(tickerEvent);
				}

				return true;
			}
		});

		linear.addView(icon, iconParams);

		return linear;
	}

	public void doOnGlobalLayout() {
		timelineGap = txtMinuteBreak.getHeight() + txtMinuteNinety.getHeight();

		double height = TimelineView.this.getHeight() - txtMinuteNinety.getHeight() - txtMinuteBreak.getHeight() - txtMinuteZero.getHeight()
				- txtMinuteEnd.getHeight();

		minuteHeight = height / minute;

		float marginLeft = TIMELINE_ICON_SIZE / 2 - context.getResources().getDimension(R.dimen.timeline_thickness) / 2;
		firstHalfTimeline.setTranslationX(marginLeft);
		if (secondHalfTimeline != null)
			secondHalfTimeline.setTranslationX(marginLeft);
		if (thirdTimeline != null)
			thirdTimeline.setTranslationX(marginLeft);
		if (events != null)
			addIcons();
		
		System.out.println(pointer.getY());
		System.out.println(txtMinuteEnd.getBottom());
		pointer.setTranslationY(-pointer.getY() + txtMinuteEnd.getBottom());

	}
}
