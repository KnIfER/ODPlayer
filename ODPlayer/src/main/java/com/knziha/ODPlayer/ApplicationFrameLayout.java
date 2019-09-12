package com.knziha.ODPlayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ApplicationFrameLayout extends FrameLayout {
    public ApplicationFrameLayout(@NonNull Context context) {
        super(context);
    }

    public ApplicationFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ApplicationFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    OnTouchListener l;

    public void setOnInterceptListener(OnTouchListener _l){
        l=_l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(l!=null && l.onTouch(this, ev)){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
