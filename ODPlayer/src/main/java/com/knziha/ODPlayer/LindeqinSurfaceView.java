package com.knziha.ODPlayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/** Handling translation of a non-HardwareAccelerated SurfaceView*/
@SuppressWarnings("SpellCheckingInspection")
public class LindeqinSurfaceView extends SurfaceView {
    private static final boolean isGoodSurfaceScaleAvailable=Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    public LindeqinSurfaceView(Context context) {
        super(context);
    }

    public LindeqinSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Bitmap dumpView() {
        int width = getWidth();
        int height = getHeight();
        ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glReadPixels(0, 0, getWidth(), getHeight(),
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);
        buf.rewind();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(buf);
        return bmp;
    }

    @Override
    public void setScaleX(float scaleX) {
        if(isGoodSurfaceScaleAvailable && isHardwareAccelerated()){
            super.setScaleX(scaleX);
            return;
        }
        getLayoutParams().width = (int) (sWidth*scaleX);
    }

    @Override
    public void setScaleY(float scaleY) {
        if(isGoodSurfaceScaleAvailable && isHardwareAccelerated()){
            super.setScaleY(scaleY);
            return;
        }
        getLayoutParams().height = (int) (sHeight*scaleY);
        super.setLayoutParams(getLayoutParams());
    }

    int sWidth, sHeight;
    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        if(isGoodSurfaceScaleAvailable && isHardwareAccelerated()){return;}
        sWidth=params.width;
        sHeight=params.height;
    }

    @Override
    public float getScaleX() {
        if(isGoodSurfaceScaleAvailable && isHardwareAccelerated()){return super.getScaleX();}
        return sHeight!=0?getLayoutParams().height*1.f/sHeight:super.getScaleX();
    }

    @Override
    public float getScaleY() {
        if(isGoodSurfaceScaleAvailable && isHardwareAccelerated()){return super.getScaleY();}
        return sWidth!=0?getLayoutParams().width*1.f/sWidth:super.getScaleX();
    }
}
