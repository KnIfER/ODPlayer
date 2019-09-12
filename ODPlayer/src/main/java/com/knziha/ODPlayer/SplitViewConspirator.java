package com.knziha.ODPlayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class SplitViewConspirator extends LinearLayout{

    public SplitViewConspirator(Context context) {
        super(context);
        init();
    }

    public SplitViewConspirator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SplitViewConspirator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

	
	private void init() {
	}


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}