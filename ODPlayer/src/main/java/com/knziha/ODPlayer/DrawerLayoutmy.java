package com.knziha.ODPlayer;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;

import com.knziha.text.SelectableTextView;

import java.util.ArrayList;

public class DrawerLayoutmy extends androidx.drawerlayout.widget.DrawerLayout {
    public SelectableTextView tv2guard;
    public PopupWindow popupToGuard;

	public DrawerLayoutmy(@NonNull Context context) {
        super(context);
    }

    public DrawerLayoutmy(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerLayoutmy(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
		if(popupToGuard!=null){
			popupToGuard.dismiss();
			return true;
		}
        int action = ev.getAction();
        if(action==MotionEvent.ACTION_UP || action==MotionEvent.ACTION_DOWN){
            headerView.dragingBar=null;
        }
        return super.onTouchEvent(ev);
    }

    ProtectiveListView headerView;
    final ArrayList<Rect> exempters = new ArrayList<>();
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev){
    	if(popupToGuard!=null)
    		return true;
        //if(true) return false;
        boolean ret=false;
        if(isDrawerOpen(GravityCompat.START))
            ret = headerView.judgeInRect(ev,true);
        if(ret) return false;

        if(tv2guard!=null)
            if(tv2guard.startInDrag)
                return false;
        //CMN.Log("exempters: "+exempters.size());
        if(isDrawerOpen(GravityCompat.START))
        for(Rect eI:exempters){
            //CMN.Log(eI);
            //CMN.Log((int)ev.getX(),(int)ev.getY());
			boolean isRTL=getResources().getConfiguration().getLayoutDirection()== View.LAYOUT_DIRECTION_RTL;
			if(eI.contains((int)(ev.getX()-(isRTL?((View)headerView.getParent()).getLeft():0)),(int)ev.getY()))
                return false;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
