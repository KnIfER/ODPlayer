package com.knziha.ODPlayer;

import android.view.View;
import android.widget.CheckedTextView;

/** 一旦 string 值改变或有增减，这个类可能需要重新编译。*/
class VoutClicker implements View.OnClickListener {
    final VICMainAppOptions opt;
    public VoutClicker(VICMainAppOptions _opt) {
        opt=_opt;
    }
    @Override
    public void onClick(View v) {
        if(v instanceof CheckedTextView){
            CheckedTextView cb = (CheckedTextView) v;
            cb.toggle();
            boolean val = cb.isChecked();
            switch (cb.getId()){
                case R.string.use_surfaceview:
                    opt.set_USE_SURFACE_VIEW(val);
                break;
                case R.string.voice_only:
                    opt.setVoiceOnly(val);
                break;
                case R.string.accurate_seek:
                    opt.setAccurateSeek(val);
                break;
                case R.string.enabled:
                    opt.setPanoramaMode(val);
                break;
            }
        }
    }
}
