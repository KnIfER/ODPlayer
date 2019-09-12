package com.knziha.ODPlayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class IndicatorFractional extends View {
    float mvalue = 0;
    Paint mTextPaint;
    String numerator="";
    String denominator;
    float fontHeight;
    Rect txtRect;

    int measuredX;
    int measuredY;

    public IndicatorFractional(Context context) {
        super(context);
        init();
    }

    public IndicatorFractional(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndicatorFractional(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        fontHeight=mTextPaint.getFontMetrics().bottom - mTextPaint.getFontMetrics().top;
        txtRect=new Rect();
        //setFloat(1);
    }


    float text_width1;
    float text_width2;

    public void setFloat(float value){
        mvalue = value;
        numerator = String.format("%.2f",mvalue);
        denominator = null;
        text_width1=mTextPaint.measureText(numerator);
        text_width2=0;
        if(text_width1>measuredX)
            requestLayout();
        else invalidate();
    }


    public void setNumber(int val) {
        numerator = Integer.toString(val);
        text_width1=mTextPaint.measureText(numerator);
        if(text_width1>measuredX)
            requestLayout();
        else invalidate();
    }


    public void setFraction(int _numerator,int _denominator){
        numerator = String.valueOf(_numerator);
        denominator = String.valueOf(_denominator);

        text_width1=mTextPaint.measureText(numerator);
        text_width2=mTextPaint.measureText(denominator);
        if(text_width1>measuredX || fontHeight*2>measuredY)
            requestLayout();
        else invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        int centerX=getWidth()/2;
        int centerY=getHeight()/2;


        if(denominator==null){
            mTextPaint.getTextBounds(numerator,0,numerator.length(),txtRect);
            Paint.FontMetrics fontm = mTextPaint.getFontMetrics();
            canvas.drawText(numerator, centerX, centerY+(txtRect.height())/2, mTextPaint);
        }else {
            canvas.drawText(numerator, centerX, centerY, mTextPaint);
            canvas.drawLine(centerX-text_width2/2,centerY,centerX+text_width2/2,centerY,mTextPaint);
            canvas.drawText(denominator, centerX, centerY + fontHeight, mTextPaint);
        }



    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = (int) Math.max(text_width1,text_width2);
        int h = (int) (fontHeight*(denominator==null?1:2));
        final int pleft = getPaddingLeft();
        final int pright = getPaddingRight();
        final int ptop = getPaddingTop();
        final int pbottom = getPaddingBottom();

        w += pleft + pright;
        h += ptop + pbottom;
        w = Math.max(w, getSuggestedMinimumWidth());
        h = Math.max(h, getSuggestedMinimumHeight());

        int widthSize = resolveSizeAndState(w, widthMeasureSpec, 0);
        int heightSize = resolveSizeAndState(h, heightMeasureSpec, 0);

        measuredX=Math.max(widthSize,measuredX);
        measuredY=Math.max(heightSize,measuredY);

        setMeasuredDimension(measuredX, measuredY);
    }

}
