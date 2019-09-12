package com.knziha.text;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.knziha.ODPlayer.CMN;

public class ViewPagerHolder extends ViewPager {
    public SelectableTextView tv2guard;
    public ViewPagerHolder(Context context) {
        super(context);
        init();
    }
    public ViewPagerHolder(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        //setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        if(tv2guard!=null)
        switch(event.getAction()){
            case MotionEvent.ACTION_UP:
                tv2guard.judgeClick();
            break;
        }
        return ret;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = super.onInterceptTouchEvent(ev);
        if(tv2guard!=null && tv2guard.draggingHandle!=null){
            return false;
        }
        return ret;
    }
}
