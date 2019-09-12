package com.knziha.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.knziha.ODPlayer.VICMainActivity;

import java.io.File;
import java.util.Locale;

public class MonitorTextView extends TextView {
    public VICMainActivity a;
    public MonitorTextView(Context context) {
        super(context);
    }

    public MonitorTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MonitorTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * For debug overlays. Scale pixel value according to screen density.
     */
    private int px(int px) {
        return (int)(a.dm.density * px);
    }


    private Paint debugTextPaint;
    private Paint debugLinePaint;
    boolean debug=true;
    private void createPaints() {
        if ((debugTextPaint == null || debugLinePaint == null) && debug) {
            debugTextPaint = new Paint();
            debugTextPaint.setTextSize(px(12));
            debugTextPaint.setColor(Color.MAGENTA);
            debugTextPaint.setStyle(Paint.Style.FILL);
            debugLinePaint = new Paint();
            debugLinePaint.setColor(Color.MAGENTA);
            debugLinePaint.setStyle(Paint.Style.STROKE);
            debugLinePaint.setStrokeWidth(px(1));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        createPaints();
        if(a!=null && debug) {
            //debugTextPaint.setColor(0xff6666ff);
            canvas.drawText("Scale: " + String.format(Locale.ENGLISH, "%.2f", a.scale) + " (" + a.minScale() + " - " + a.maxScale + ")", px(5), px(15), debugTextPaint);
            if(a.vTranslate!=null)canvas.drawText("Translate: " + String.format(Locale.ENGLISH, "%.2f", a.vTranslate.x) + ":" + String.format(Locale.ENGLISH, "%.2f", a.vTranslate.y), px(5), px(30), debugTextPaint);
            PointF center = a.getCenter();

            if(center!=null)canvas.drawText("Source center: " + String.format(Locale.ENGLISH, "%.2f", center.x) + ":" + String.format(Locale.ENGLISH, "%.2f", center.y), px(5), px(45), debugTextPaint);
            if(center!=null)canvas.drawText("Source center2: " + String.format(Locale.ENGLISH, "%.2f",
            a.sin   ) + ":" + String.format(Locale.ENGLISH, "%.2f",
            a.cos   ), px(5), px(60), debugTextPaint);

            canvas.drawText("size = " + a.getScreenWidth()+"x"+a.getScreenHeight()+" -- "+a.sWidth+"x"+a.sHeight, px(5), px(90), debugTextPaint);

            debugTextPaint.setColor(Color.MAGENTA);


        }
    }
}
