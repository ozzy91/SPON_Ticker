package com.ipol.sponticker.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class GoalTextView extends TextView {

	public GoalTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setScrollPosition(int scrollTo){
		scrollTo(0, scrollTo);
	}
	
	public int getScrollPosition(){
		return 0;
	}

}
