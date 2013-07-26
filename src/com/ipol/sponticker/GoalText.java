package com.ipol.sponticker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class GoalText extends TextView {

	public GoalText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setScrollPosition(int scrollTo){
		scrollTo(0, scrollTo);
	}
	
	public int getScrollPosition(){
		return 0;
	}

}
