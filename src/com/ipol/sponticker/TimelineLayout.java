package com.ipol.sponticker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class TimelineLayout extends RelativeLayout{

	public TimelineLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		System.out.println("onInterceptTouchEvent");
		
		System.out.println(ev.getEventTime());
		if(ev.getActionMasked() == MotionEvent.ACTION_DOWN){
			return false;
		}else if(ev.getActionMasked() == MotionEvent.ACTION_MOVE){
			return true;
		}
		return false;
	}

}
