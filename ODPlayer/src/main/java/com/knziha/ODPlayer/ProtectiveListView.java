package com.knziha.ODPlayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsSeekBar;
import android.widget.ListView;
import android.widget.SeekBar;

import java.lang.reflect.Method;

public class ProtectiveListView extends ListView {
    public ProtectiveListView(Context context) {
        super(context);
    }

    public ProtectiveListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProtectiveListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(dragingBar!=null) {
            ev.setLocation(ev.getX()-dragingBar.getLeft(),ev.getY());
            dragingBar.onTouchEvent(ev);
            if (ev.getAction() == MotionEvent.ACTION_UP) {
                dragingBar = null;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    ViewGroup headerView;
    View dragingBar;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean ret = judgeInRect(ev,false);
        if(ret) return true;


        ret = super.onInterceptTouchEvent(ev);

        return ret;
    }

    public boolean judgeInRect(MotionEvent ev, boolean fromDrawer) {
        if(dragingBar!=null)
            return true;
        if(ev.getAction()== MotionEvent.ACTION_DOWN){
            dragingBar=null;
            int topM = ((MarginLayoutParams) getLayoutParams()).topMargin;
            float ey = ev.getY() - topM;
            if(ey<headerView.getBottom()-topM && ey> headerView.getChildAt(1).getBottom()+headerView.getTop()) {
				boolean isRTL= fromDrawer && headerView.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
            	float judgeLine = headerView.getChildAt(2).getBottom() - 4 * getResources().getDisplayMetrics().density + headerView.getTop();
                if(ey < judgeLine) {//check seekbar 1
                    SeekBar seekbar = (SeekBar) ((ViewGroup)headerView.getChildAt(2)).getChildAt(2);
					float ex = ev.getX()-(isRTL?((View)getParent()).getLeft():0);
                    if(ex>seekbar.getLeft() && ex<seekbar.getRight()){
                        dragingBar = seekbar;
                        ev.setLocation(ev.getX()-seekbar.getLeft(),ey);
                        dragingBar.onTouchEvent(ev);
                        try {
                            Method mm = AbsSeekBar.class.getDeclaredMethod("trackTouchEvent", MotionEvent.class);
                            mm.setAccessible(true);
                            mm.invoke(dragingBar, ev);
                            return true;
                            //((SeekBar)headerView.findViewById(R.id.sk1)).dispatchDragEvent(ev);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(ey > judgeLine+4 * getResources().getDisplayMetrics().density){//check seekbar 2
                    SeekBar seekbar = (SeekBar) ((ViewGroup)headerView.getChildAt(3)).getChildAt(2);
					float ex = ev.getX()-(isRTL?((View)getParent()).getLeft():0);
					if(ex>seekbar.getLeft() && ex<seekbar.getRight()){
                        dragingBar = seekbar;
                        ev.setLocation(ev.getX()-seekbar.getLeft(),ey);
                        dragingBar.onTouchEvent(ev);
                        try {
                            Method mm = AbsSeekBar.class.getDeclaredMethod("trackTouchEvent", MotionEvent.class);
                            mm.setAccessible(true);
                            mm.invoke(dragingBar, ev);
                            return true;
                            //((SeekBar)headerView.findViewById(R.id.sk1)).dispatchDragEvent(ev);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //CMN.Log("onLayout");
        //try {
            super.onLayout(changed, l, t, r, b);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
    }
}
