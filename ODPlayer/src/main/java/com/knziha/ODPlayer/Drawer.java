package com.knziha.ODPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jaygoo.widget.RangeSeekBar;
import com.knziha.equalizer.EqualizerGroup;
import com.knziha.equalizer.PresetsCustomAdapter;
import com.knziha.equalizer.VerticalSeekBar;
import com.knziha.filepicker.controller.DialogSelectionListener;
import com.knziha.filepicker.model.DialogConfigs;
import com.knziha.filepicker.model.DialogProperties;
import com.knziha.filepicker.utils.ExtensionHelper;
import com.knziha.filepicker.view.CMNF;
import com.knziha.filepicker.view.FilePickerDialog;
import com.knziha.filepicker.view.GoodKeyboardDialog;
import com.knziha.filepicker.widget.CircleCheckBox;
import com.knziha.settings.SettingsActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


/**
 * @author KnIfER
 *
 */
@SuppressLint({"InflateParams"})
public class Drawer extends Fragment implements
		OnClickListener, OnDismissListener, OnCheckedChangeListener, OnLongClickListener, SeekBar.OnSeekBarChangeListener {
	Dialog d;
    Dialog dd;
    private boolean bIsFirstLayout=true;
	private ProtectiveListView mDrawerList;
	ViewGroup mDrawerRootFrame;
	MyAdapter myAdapter;

    CompoundButton sw1,sw2,sw3,sw4,sw5;
    CompoundButton sw6,sw7,sw8,sw9,sw10;

    ViewGroup HeaderView;

	ViewGroup FooterView;
    double mRate = 1.0;
    double SpeedMin=1.f/8;
    double SpeedMax=2.f;
    SeekBar SpeedSeekBar, VolumeSeekBar;
    IndicatorFractional manitv1;
    IndicatorFractional manitv2;
    private CircleCheckBox ckSpeed;
    private CircleCheckBox ckVolume;

    private View equaltweaker;
    private ViewGroup mEqualizerLayer;
    EqualizerGroup eqlv1;
    private boolean isFirstTurnOn=true;
    private short checked_slots;

    private String CurrentPresetName;
    int preset_api_idx=0;
    int preset_vlc_idx=0;
    final int preset_usage_mask=3;
    boolean eq_presets_dirty;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	
	public Drawer() {
	}
    ListView TweakerList;
    View TweakerListFooter;
    PresetsCustomAdapter PresetAdapter;
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		mDrawerRootFrame = (ViewGroup) inflater.inflate(R.layout.activity_main_navi_drawer, container,false);
        mDrawerRootFrame.setClickable(true);
		//mDrawerRootFrame.setOnClickListener(this);
		FooterView = mDrawerRootFrame.findViewById(R.id.footer);
		FooterView.findViewById(R.id.menu_item_setting).setOnClickListener(this);
        FooterView.findViewById(R.id.menu_item_exit).setOnClickListener(this);
        FooterView.findViewById(R.id.menu_item_exit).setOnLongClickListener(this);
        equaltweaker = mDrawerRootFrame.findViewById(R.id.equaltweaker);
        equaltweaker.setOnClickListener(this);
		equaltweaker.setOnLongClickListener(this);

		mDrawerList = mDrawerRootFrame.findViewById(R.id.left_drawer);

        HeaderView = mDrawerRootFrame.findViewById(R.id.header);//(ViewGroup) inflater.inflate(R.layout.drawer_fc_header, null);
        SpeedSeekBar = HeaderView.findViewById(R.id.sk1);
        VolumeSeekBar = HeaderView.findViewById(R.id.sk2);
        mDrawerRootFrame.removeView(HeaderView);
        //CMN.Log("before onCreate2 drawer oncreate2",System.currentTimeMillis()-stst);
        HeaderView.setLayoutParams(new ListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mDrawerList.addHeaderView(HeaderView);
        mDrawerList.headerView = HeaderView;

        ViewGroup linear1 = (ViewGroup) HeaderView.getChildAt(0);
        ViewGroup linear2 = (ViewGroup) HeaderView.getChildAt(1);
        sw1 =  (CompoundButton)  linear1.getChildAt(0);//HeaderView.findViewById(R.id.sw1);
        sw2 =  (CompoundButton) linear1.getChildAt(1);//HeaderView.findViewById(R.id.sw2);
        sw4 =  (CompoundButton) linear1.getChildAt(2);//HeaderView.findViewById(R.id.sw4);
        sw3 =  (CompoundButton) linear1.getChildAt(3);//HeaderView.findViewById(R.id.sw3);
        sw5 =  (CompoundButton) linear1.getChildAt(4);//HeaderView.findViewById(R.id.sw5);
        sw6 =  (CompoundButton) linear2.getChildAt(0);//HeaderView.findViewById(R.id.sw6);
        sw7 =  (CompoundButton) linear2.getChildAt(1);//HeaderView.findViewById(R.id.sw7);
        sw8 =  (CompoundButton) linear2.getChildAt(2);//HeaderView.findViewById(R.id.sw8);
        sw9 =  (CompoundButton) linear2.getChildAt(3);//HeaderView.findViewById(R.id.sw9);
        sw10 = (CompoundButton) linear2.getChildAt(4);//HeaderView.findViewById(R.id.sw10);

        mDrawerRootFrame.addOnLayoutChangeListener(mLayoutChangeCalibrator);
		return mDrawerRootFrame;
	}

    private final OnLayoutChangeListener mLayoutChangeCalibrator = new OnLayoutChangeListener() {
        int oldWidth;
        @Override public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
                                   int oldRight, int oldBottom) {
            int drawerWidth=right-left;
            if(views!=null && (drawerWidth!=oldWidth || bIsFirstLayout)) {
                CMN.Log("drawer", "onLayoutChange");
                if(bIsFirstLayout) SwitchCompatBeautiful.bForbidRquestLayout = true;
                int width = (drawerWidth - sw1.getWidth()*5)/6;
                MarginLayoutParams lp;
                for(View vI:views){
                    lp = (MarginLayoutParams) vI.getLayoutParams();
                    lp.leftMargin = width;
                    vI.setLayoutParams(vI.getLayoutParams());
                }
                oldWidth = drawerWidth;
                AdjustEquLayer(drawerWidth, bottom - top);
            }
            if(bIsFirstLayout) {
                bIsFirstLayout = false;
                SwitchCompatBeautiful.bForbidRquestLayout = false;
            }
        }
    };

    private void AdjustEquLayer(int drawerWidth, int drawerHeight) {
        VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
        if (mEqualizerLayer != null) {
            boolean visible = mEqualizerLayer.getVisibility()==View.VISIBLE;
            mDrawerList.getLayoutParams().height = visible?HeaderView.getHeight(): ViewGroup.LayoutParams.MATCH_PARENT;

            if(visible) {
                MarginLayoutParams target = (MarginLayoutParams) mEqualizerLayer.getLayoutParams();
                if (a.opt.keepBottomBar())
                    target.bottomMargin = a.bottombar.getHeight();
                else
                    target.bottomMargin = FooterView.getHeight();
                int status_height = (a.opt.isFullScreen() ? 0 : CMN.getStatusBarHeight(a));
                target.topMargin = HeaderView.getHeight()+status_height;
                target.width = drawerWidth;
                if (a.mDrawerLayout.exempters.size() == 0)
                    a.mDrawerLayout.exempters.add(new Rect());
                a.mDrawerLayout.exempters.get(0).set(0, HeaderView.getHeight()+status_height, drawerWidth, drawerHeight - target.bottomMargin+status_height);
            }else
                a.mDrawerLayout.exempters.clear();
            //if (drawerWidth != oldWidth) mEqualizerLayer.setLayoutParams(mEqualizerLayer.getLayoutParams());
        }
    }

    class MyAdapter extends ArrayAdapter<String> {
		public MyAdapter(List<String> mdicts, Context context) {
			super(context,R.layout.listview_item0, R.id.text, mdicts);
        }
        @Override
        public boolean areAllItemsEnabled() {
          return false;
        }
        @Override
        public int getCount() {
          return super.getCount()-1;
        }
        @Override
        public boolean isEnabled(int position) {
    		return !"d".equals(getItem(position)); // 如果-开头，则该项不可选
        }
        @NonNull @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
    	  position+=1;
          if("d".equals(getItem(position))){//是标签项
        	  return (convertView!=null && convertView.getTag()==null)?convertView:LayoutInflater.from(getContext()).inflate(R.layout.listview_sep, parent, false);
          }
    	  viewHolder vh = null;
    	  if(convertView!=null)
    		  vh=(viewHolder)convertView.getTag();
          if(vh==null) {
        	  	vh=new viewHolder();
        	  	convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_item0, parent, false);
        	  	convertView.setTag(vh);
        	  	vh.title = convertView.findViewById(R.id.text);
        	  	vh.subtitle = convertView.findViewById(R.id.subtext);
        	  	vh.subtitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorHeaderBlue));
          }
          
  		  vh.title.setText(getItem(position));
          vh.subtitle.setText(null);

  		  convertView.setTag(R.id.position,position);
	  	  convertView.setOnClickListener(Drawer.this);

          return convertView;
        }
      }

    private static class viewHolder{
    	private TextView title;
    	private TextView subtitle;
    }

	@Override
	public View getView() {
		return super.getView();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        VICMainActivity a = ((VICMainActivity) getActivity());
        String[] items = getResources().getStringArray(R.array.drawer_items);
        myAdapter = new MyAdapter(Arrays.asList(items), a.getApplicationContext());
        mDrawerList.setAdapter(myAdapter);
		a.drawerFragment = this;
		a.mDrawerLayout.headerView=mDrawerList;
        mRate = a.opt.getRate();
        a.mVolume = a.opt.getVolume();
        boolean val;

        preset_api_idx=a.opt.get_preset_api_idx();
        preset_vlc_idx=a.opt.get_preset_vlc_idx();
        val=a.opt.isEqualizerEnabled();
        if(val) sw1.setTag(false);
        sw1.setOnCheckedChangeListener(this);
        //sw1.setOnLongClickListener(v -> true);
        sw1.setChecked(val);

        sw2.setOnCheckedChangeListener(this);
        sw2.setChecked(a.opt.getRespectAudioManager());

        val = a.opt.isMute();
        sw3.setChecked(!val);
        sw3.setOnCheckedChangeListener(this);

        sw4.setChecked(a.opt.isBackgroundPlayEnabled());
        sw4.setOnCheckedChangeListener(this);

        val=a.opt.isFullScreen();
        sw5.setTag(false);
        sw5.setChecked(!val);
        sw5.setOnCheckedChangeListener(this);
        sw5.setChecked(val);

        sw6.setChecked(a.opt.getVibrationEnabled());
        sw6.setOnCheckedChangeListener(this);

        val=a.opt.keepBottomBar();
        sw7.setChecked(val);
        sw7.setOnCheckedChangeListener(this);

        val=a.opt.getShaffle();
        sw8.setChecked(val);
        sw8.setOnCheckedChangeListener(this);

        val=a.opt.getLoopMusic();
        sw9.setChecked(val);
        sw9.setOnCheckedChangeListener(this);

        val=a.opt.getSingleMusic();
        sw10.setChecked(val);
        sw10.setOnCheckedChangeListener(this);


        manitv1 = HeaderView.getChildAt(2).findViewById(R.id.manitv1);
        manitv2 = HeaderView.getChildAt(3).findViewById(R.id.manitv2);

		ckSpeed = HeaderView.getChildAt(2).findViewById(R.id.check1);
		ckVolume = HeaderView.getChildAt(3).findViewById(R.id.check2);


		TextView view = HeaderView.findViewById(R.id.spdup);
		boolean isLTR = ((View) ckSpeed.getParent()).getContext().getResources().getConfiguration().getLayoutDirection()==
				View.LAYOUT_DIRECTION_LTR;
        ShapeDrawable d = new ShapeDrawable(new SpeedTagShape(isLTR?0:1));
        d.getPaint().setColor(0xff6699bb);
        view.setBackground(d);
        view.setOnClickListener(this);

        view = HeaderView.findViewById(R.id.spddn);
        d = new ShapeDrawable(new SpeedTagShape(isLTR?1:0));
        d.getPaint().setColor(0xff6699bb);
        view.setBackground(d);
        view.setOnClickListener(this);

        view = HeaderView.findViewById(R.id.voldn);
        d = new ShapeDrawable(new SpeedTagShape(isLTR?1:0));
        d.getPaint().setColor(0xff6699bb);
        view.setBackground(d);
        view.setOnClickListener(this);

        view = HeaderView.findViewById(R.id.volup);
        d = new ShapeDrawable(new SpeedTagShape(isLTR?0:1));
        d.getPaint().setColor(0xff6699bb);
        view.setBackground(d);
        view.setOnClickListener(this);

        int pad = (int) (getResources().getDimension(R.dimen.seek_bar_thumb_size)/2);
        SpeedSeekBar.setPadding(pad, 0, pad, 0);
        SpeedSeekBar.setMax(1000);
        SpeedSeekBar.setProgress((int) ((mRate-SpeedMin)/(SpeedMax-SpeedMin)*SpeedSeekBar.getMax()));
        setRate(mRate,false);

        VolumeSeekBar.setMax(1000);
        VolumeSeekBar.setProgress((int) (a.mVolume* VolumeSeekBar.getMax()));
        manitv2.setNumber((int) (a.mVolume * 100));
        VolumeSeekBar.setPadding(pad, 0, pad, 0);

        val = a.opt.getRateEnabled();
        ckSpeed.setChecked(val);
        if(val) a.mMediaPlayerCompat.setRate((float) mRate);
        ckSpeed.setOnClickListener(this);
        ckSpeed.setOnLongClickListener(this);

        val = a.opt.getVolumeEnabled();
        ckVolume.setChecked(val);
        if(val && a.mVolume!=1 || a.opt.isMute())
            a.mMediaPlayerCompat.setVolume((float) a.mVolume);
        ckVolume.setOnClickListener(this);
        ckVolume.setOnLongClickListener(this);

		//test groups
        //View v = new View(a);v.setTag(R.id.position,1);onClick(v);
		//View v = new View(a);v.setTag(R.id.position,7);onClick(v);
		//View v = new View(a);v.setTag(R.id.position,11);onClick(v);
	}

    CompoundButton[] views;
    @Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
        CMN.Log("drawer", "onViewCreated");
        SpeedSeekBar.setOnSeekBarChangeListener(this);
        VolumeSeekBar.setOnSeekBarChangeListener(this);
        views=new CompoundButton[]{sw1,sw2,sw3,sw4,sw5,sw6,sw7,sw8,sw9,sw10};
	}

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CMN.Log("drawer", "onDestroyView");
        SpeedSeekBar.setOnSeekBarChangeListener(null);
        VolumeSeekBar.setOnSeekBarChangeListener(null);
        for(CompoundButton vI:views)
            vI.setOnCheckedChangeListener(null);
        views=null;
    }

    @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
            switch (seekBar.getId()){
                case R.id.sk1:
                    if(!a.opt.getRateEnabled())ckSpeed.performClick();
                    if(a.mMediaPlayerCompat!=null){
                        double rate = (SpeedMin + (SpeedMax - SpeedMin) * 1.0 * progress / seekBar.getMax());
                        setRate(rate,true);
                    }
                break;
                case R.id.sk2:
                    if(!a.opt.getVolumeEnabled())ckVolume.performClick();
                    if(a.mMediaPlayerCompat!=null) {//设置内置音量
                        a.mVolume=progress * 1.0 / seekBar.getMax();
                        a.mMediaPlayerCompat.setVolume((float) a.mVolume);
                        manitv2.setNumber((int) (progress * 1.0 / seekBar.getMax() * 100));
                    }
                break;
            }
        }
    }
    @Override public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override public void onStopTrackingTouch(SeekBar seekBar) { }

    @Override
	public void onClick(View v) {
        VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
		if(!a.systemIntialized) return;
		int id = v.getId();
		switch(id) {
            case R.id.check1:
                if(!v.isEnabled()) break;
                ckSpeed.toggle();
                boolean val = a.opt.setRateEnabled(ckSpeed.isChecked());
                if(val) a.mMediaPlayerCompat.setRate((float) mRate);
                else a.mMediaPlayerCompat.setRate(1);
            break;
            case R.id.check2:
                if(!v.isEnabled()) break;
                ckVolume.toggle();
                val = a.opt.setVolumeEnabled(ckVolume.isChecked());
                if(val)  a.mMediaPlayerCompat.setVolume((float) a.mVolume);
                else a.mMediaPlayerCompat.setVolume(1);
            break;
            case R.id.menu_item_setting:
            return;
            case R.id.menu_item_exit://退出
            return;
            case R.id.spdup:
                if(!a.opt.getRateEnabled())ckSpeed.performClick();
                if(!v.isEnabled())return;
                int taregtProgress;
                double speed = mRate;
                if(speed<=1.0/8){
                    int base= (int) (1/SpeedMin);
                    while(speed>=1.0/base){
                        base/=2;
                    }
                    speed = 1.0/base;
                }else{
                    double base = SpeedMin;
                    while(speed>=base){
                        if(base<0.5)
                            base*=2;
                        else
                            base+=0.25;
                    }
                    speed = base;
                }
                try {
                    setRate(speed,true);
                    if(speed>SpeedMax)
                        SpeedMax=speed;
                    taregtProgress = (int) ((speed-SpeedMin)/(SpeedMax-SpeedMin)*SpeedSeekBar.getMax());
                    SpeedSeekBar.setProgress(taregtProgress);
                } catch (Exception e) {
                    a.showT("不支持的速度："+speed);
                }
            return;
            case R.id.spddn:
                if(!a.opt.getRateEnabled())ckSpeed.performClick();
                if(!v.isEnabled())return;
                speed = mRate;
                if(speed<=1.0/8){
                    int base= (int) (1/SpeedMin);
                    while(speed<=1.0/base){
                        base*=2;
                    }
                    speed = 1.0/base;
                }else{
                    double base = SpeedMax;
                    while(speed<=base){
                        if(base<=0.5)
                            base/=2;
                        else
                            base-=0.25;
                    }
                    speed = base;
                }
                try {
                    setRate(speed,true);
                    if(speed<SpeedMin)
                        SpeedMin=speed;
                    taregtProgress = (int) ((speed-SpeedMin)/(SpeedMax-SpeedMin)*SpeedSeekBar.getMax());
                    SpeedSeekBar.setProgress(taregtProgress);
                } catch (Exception e) {
                    a.showT("不支持的速度："+speed);
                }
                return;
            case R.id.volup:
                if(!a.opt.getVolumeEnabled())ckVolume.performClick();
                if(!v.isEnabled())return;
                a.mVolume += 0.2;
                a.mMediaPlayerCompat.setVolume((float) a.mVolume);
                taregtProgress = (int) (0.5 + a.mVolume* VolumeSeekBar.getMax());
                manitv2.setNumber((int) (0.5 + a.mVolume*100));
                VolumeSeekBar.setProgress(taregtProgress);
            return;
            case R.id.voldn:
                if(!a.opt.getVolumeEnabled())ckVolume.performClick();
                if(!v.isEnabled())return;
                a.mVolume -= 0.2;
                if(a.mVolume<0)
                    a.mVolume=0;
                a.mMediaPlayerCompat.setVolume((float) a.mVolume);
                taregtProgress = (int) (0.5 + a.mVolume* VolumeSeekBar.getMax());
                manitv2.setNumber((int) (0.5 + a.mVolume*100));
                VolumeSeekBar.setProgress(taregtProgress);
            return;
            case R.id.equaltweaker:
                //showEqTweakerDialog();
                if(TweakerList==null){
                    TweakerList = new ListView(a);
                    PresetAdapter = new PresetsCustomAdapter(a, new ArrayList<>());
                    TweakerList.setAdapter(PresetAdapter);
                    View tweaker_header = getLayoutInflater().inflate(R.layout.equaltweaker_header,null);
                    TweakerListFooter = getLayoutInflater().inflate(R.layout.checker3,null);
                    decorateHeader(tweaker_header);
                    decorateFooter(TweakerListFooter);
                    TweakerList.addHeaderView(tweaker_header);
                    TweakerList.addFooterView(TweakerListFooter);
                    ((RangeSeekBar)tweaker_header.findViewById(R.id.slide_tweaker)).setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener(){
                        ArrayList<Integer> BandLevelsCache = new ArrayList<>(5);
                        float oldPos=0f;
                        float overShootThresholdDelta=0.0f;
                        @Override public void onRangeChanged(RangeSeekBar view, final float pos, boolean isFromUser) {
                            if(isFromUser){
                                a.mMediaPlayerCompat.BatchAjustAmp((int) (pos*a.mMediaPlayerCompat.getBandRange()[1]*0.618), BandLevelsCache);
                                int bandCount=a.mMediaPlayerCompat.getBandCount();
                                if(eqlv1!=null) {
                                    eqlv1.setCapacity(bandCount, LayoutInflater.from(a.getApplicationContext()), true, false);
                                    for (int i = 0; i < eqlv1.getChildCount(); i++) {
                                        VerticalSeekBar skI = ((VerticalSeekBar) eqlv1.getChildAt(i));
                                        skI.UpdateMyThumb();
                                    }
                                }
                            }
                        }
                        @Override public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                            int bandCount=a.mMediaPlayerCompat.getBandCount();
                            BandLevelsCache.clear();
                            for (int i = 0; i < bandCount; i++)
                                BandLevelsCache.add(a.mMediaPlayerCompat.getAmp(i));
                            if(!VICMainAppOptions.isTransparentDialog)
                                if(d!=null && d.getWindow()!=null)
                                    d.getWindow().getDecorView().setAlpha(0.2f);
                            oldPos=overShootThresholdDelta=0.0f;
                        }
                        @Override public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                            if(!VICMainAppOptions.isTransparentDialog)
                                if(d!=null && d.getWindow()!=null)
                                    d.getWindow().getDecorView().setAlpha(1f);
                            view.reSetPosition();
                            oldPos=overShootThresholdDelta=0.0f;
                        }
                    });
                    SeekBar sk1 = tweaker_header.findViewById(R.id.gap_tweaker);
                    SeekBar sk2 = tweaker_header.findViewById(R.id.size_tweaker);
                    sk1.setMax((int) (60*a.opt.dm.density));
                    sk1.setProgress(a.opt.EqbarSpacing);
                    sk2.setMax((int) (55*a.opt.dm.density));
                    sk2.setProgress(a.opt.EqbarSize-(int) (5*a.opt.dm.density));
                    SeekBar.OnSeekBarChangeListener barAdjusterListener = new SeekBar.OnSeekBarChangeListener() {
                        @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if (fromUser) {
                                switch (seekBar.getId()){
                                    case R.id.gap_tweaker:
                                        a.opt.EqbarSpacing = progress;
                                    break;
                                    case R.id.size_tweaker:
                                        a.opt.EqbarSize = progress+(int) (5*a.opt.dm.density);
                                    break;
                                }
                                if(eqlv1!=null) {
                                    eqlv1.setCapacity(eqlv1.getChildCount(), null, false, true);
                                }
                            }
                        }
                        @Override public void onStartTrackingTouch(SeekBar seekBar) {
                            if(!VICMainAppOptions.isTransparentDialog)
                                if (d != null && d.getWindow() != null) {
                                    d.getWindow().getDecorView().setAlpha(0.2f);
                                }
                        }
                        @Override public void onStopTrackingTouch(SeekBar seekBar) {
                            if(!VICMainAppOptions.isTransparentDialog)
                                if (d != null && d.getWindow() != null) {
                                    d.getWindow().getDecorView().setAlpha(1f);
                                }
                        }
                    };
                    sk1.setOnSeekBarChangeListener(barAdjusterListener);
                    sk2.setOnSeekBarChangeListener(barAdjusterListener);
                    //TweakerList.setTag(getLayoutInflater().inflate(R.layout.circle_checker_item_menu_titilebar,null));
                    PresetAdapter.setPopulaterFutherListener(new PresetsCustomAdapter.OnPopulaterFuther() {
                        boolean bFurtherPopulated=false;
                        @Override
                        public void populateFurther() {
                            if(getContext()==null) return;
                            PresetAdapter.setListMargin(3);
                            PresetAdapter._list.clear();
                            if(bFurtherPopulated=!bFurtherPopulated){
                                PresetAdapter._list.addAll(a.mMediaPlayerCompat.getPresetNames());
                                PresetAdapter._list_pre_empt[2]=getContext().getResources().getStringArray(R.array.share_pre_empt)[3];
                            }else
                                PresetAdapter._list_pre_empt[2]=getContext().getResources().getStringArray(R.array.share_pre_empt)[2];
                            TweakerList.setAdapter(PresetAdapter);
                            PresetAdapter.notifyDataSetChanged();
                        }


                        @Override
                        public void saveCurrentAs() {
                            final ViewGroup dv = (ViewGroup) getLayoutInflater().inflate(com.knziha.filepicker.R.layout.fp_edittext, null);
                            final EditText etNew = dv.findViewById(com.knziha.filepicker.R.id.edt_input);
                            final View btn_Done = dv.findViewById(com.knziha.filepicker.R.id.done);
                            final ImageView btn_SwicthFolderCreation = dv.findViewById(com.knziha.filepicker.R.id.toolbar_action1);
                            btn_SwicthFolderCreation.setVisibility(View.INVISIBLE);
                            btn_Done.setOnClickListener(v -> {
                                String name = etNew.getText().toString();
                                JSONObject new_preset = new JSONObject(3);
                                new_preset.put("pname", name);
                                for (int i = 0; i < a.disk_presets.size(); i++) {
                                    if(name.equals(((JSONObject) a.disk_presets.get(i)).get("pname"))){
                                        a.show(R.string.invalid_duplicate_name);
                                        return;
                                    }
                                }
                                new_preset.put("type", a.mMediaPlayerCompat.isApiPlayer()?0:1);
                                JSONArray data = new JSONArray(5);
                                a.mMediaPlayerCompat.FillEqualizerValuesToJSON(data);
                                if(data.size()==0){
                                    a.show(R.string.save_failed);
                                    return;
                                }
                                new_preset.put("data", data);
                                a.disk_presets.add(new_preset);
                                if(dd!=null){
                                    dd.dismiss();
                                    dd=null;
                                }
                                eq_presets_dirty =true;
                                a.show(R.string.saved);
                            });
                            if(true){
                                GoodKeyboardDialog dTmp = new GoodKeyboardDialog(a);
                                dTmp.setContentView(dv);
                                Window win = dTmp.getWindow();
                                win.setGravity(Gravity.TOP);
                                win.getAttributes().verticalMargin=0.01f;
                                win.setAttributes(win.getAttributes());
                                win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dv.getLayoutParams().width=-1;
                                if(dv.getLayoutParams() instanceof MarginLayoutParams) {
                                    ((MarginLayoutParams) dv.getLayoutParams()).leftMargin = (int) (15 * a.opt.dm.density);
                                    ((MarginLayoutParams) dv.getLayoutParams()).rightMargin = (int) (15 * a.opt.dm.density);
                                }
                                dv.setLayoutParams(dv.getLayoutParams());
                                win.setLayout(-1,-2);
                                dTmp.show();
                                dd = dTmp;
                                win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                etNew.requestFocus();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                final AlertDialog dd = builder.create();
                                dd.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dd.setView(dv, 0, 0, 0, 0);
                                dd.getWindow().setGravity(Gravity.TOP);
                                dd.getWindow().getAttributes().verticalMargin=0.01f;
                                dd.getWindow().setAttributes(dd.getWindow().getAttributes());
                                dd.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                                //dd.getWindow().setBackgroundDrawableResource(R.drawable.popup_shadow_s);
                                dd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dd.show();
                                dd.setOnDismissListener(dia ->{
                                    etNew.setEnabled(false);
                                    //etNew.clearFocus();
                                    //InputMethodManager inputMethodManager = (InputMethodManager) a.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    //inputMethodManager.hideSoftInputFromWindow(etNew.getWindowToken(),0);
                                    //((AlertDialog)dia).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                });
                            }

                        }

                        @Override
                        public void saveCurrent() {
                            int type = a.mMediaPlayerCompat.isApiPlayer()?0:1;
                            int usage = getPresetUsage(type);
                            if(usage!=0){
                                int index = getPresetIndex(type);
                                if (usage == 1) {
                                    String name = a.mMediaPlayerCompat.getPresetNames().get(index);
                                    for (int i = 0; i < a.disk_presets.size(); i++) {
                                        if (name.equals(a.disk_presets.getJSONObject(i).getString("pname"))){
                                            a.mMediaPlayerCompat.FillEqualizerValuesToJSON(a.disk_presets.getJSONObject(i).getJSONArray("data"));
                                            a.showT(R.string.saved);
                                            eq_presets_dirty =true;
                                            return;
                                        }
                                    }
                                    a.showT("nothing to save");
                                } else if (usage == 2) {
                                    a.mMediaPlayerCompat.FillEqualizerValuesToJSON(a.disk_presets.getJSONObject(index).getJSONArray("data"));
                                    eq_presets_dirty =true;
                                    a.showT(R.string.saved);
                                }
                            }
                        }
                    });
                    TweakerList.setAdapter(PresetAdapter);
                    TweakerList.setOnItemClickListener((p, view, position, id1) -> {//使用代码定义的预设
                        if(view!=TweakerListFooter) {
                            String name;
                            FilmCapsule mComp = a.mMediaPlayerCompat;
                            int bandCount = mComp.getBandCount();
                            int type = mComp.isApiPlayer() ? 0 : 1;
                            int index = position - PresetAdapter.number_margin - TweakerList.getHeaderViewsCount();
                            if(index<PresetAdapter.filtered_disk_presets.size()){
                                JSONArray data = PresetAdapter.filtered_disk_presets.get(index).getJSONArray("data");
                                name = PresetAdapter.filtered_disk_presets.get(index).getString("pname");
                                for (int i = 0; i < Math.min(bandCount, data.size()); i++) {
                                    mComp.setAmp(i, data.getInteger(i));
                                }
                                setPresetUsage_Disk(type, PresetAdapter.filtered_disk_presets_tracer.get(index));
                                ((TextView)TweakerListFooter.findViewById(R.id.text1)).setText((CurrentPresetName=name)==null?CurrentPresetName:CurrentPresetName.toUpperCase());
                            }else {
                                index -= PresetAdapter.filtered_disk_presets.size();
                                if(mComp.usePreset(index)==0) {
                                    setPresetUsage_Coded(type, index);
                                    ((TextView) TweakerListFooter.findViewById(R.id.text1)).setText((CurrentPresetName = mComp.getPresetNames().get(index)) == null ? CurrentPresetName : CurrentPresetName.toUpperCase());
                                }
                            }
                            eqlv1.setCapacity(bandCount, LayoutInflater.from(a.getApplicationContext()), true, false);
                            for (int i = 0; i < eqlv1.getChildCount(); i++) {
                                VerticalSeekBar skI = ((VerticalSeekBar) eqlv1.getChildAt(i));
                                skI.UpdateMyThumb();
                            }
                            if(!a.opt.getPinTweakerDialog()){
                                if(d!=null)
                                    d.dismiss();
                            }
                        }else{
                            if(VICMainAppOptions.isTransparentDialog =!VICMainAppOptions.isTransparentDialog){
                                d.getWindow().getDecorView().setAlpha(0.6f);
                            }else
                                d.getWindow().getDecorView().setAlpha(1f);
                        }
                    });
                    TweakerList.setOnItemLongClickListener((p, view, position, id12) -> {
                        //长按预设
                        CMN.Log("longClick!");

                        return true;
                    });

                }

                ((TextView)TweakerListFooter.findViewById(R.id.text1)).setText(CurrentPresetName==null?CurrentPresetName:CurrentPresetName.toUpperCase());

                //TODO more efficient filtering method.
                PresetAdapter.filtered_disk_presets.clear();
                PresetAdapter.filtered_disk_presets_tracer.clear();
                int type = a.mMediaPlayerCompat.isApiPlayer()?0:1;
                if(a.disk_presets!=null)
                    for (int i = 0; i < a.disk_presets.size(); i++) {
                        try {
                            JSONObject pI = (JSONObject) a.disk_presets.get(i);
                            if(Integer.valueOf(""+pI.get("type")).equals(type)){
                                PresetAdapter.filtered_disk_presets.add(pI);
                                PresetAdapter.filtered_disk_presets_tracer.add(i);
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                TweakerList.setAdapter(PresetAdapter);

                if(TweakerList.getParent()!=null)
                    ((ViewGroup)TweakerList.getParent()).removeView(TweakerList);

                if(false){
                    d=new Dialog(a);
                    d.setContentView(TweakerList);
                    d.getWindow().setBackgroundDrawableResource(true?R.drawable.popup_shadow_d:R.drawable.popup_shadow_l);
                    d.getWindow().setDimAmount(0);
                    d.show();
                    VICMainAppOptions.isTransparentDialog =false;
                }else{
                    AlertDialog.Builder dialog_builder = new  AlertDialog.Builder(a);
                    dialog_builder.setView(TweakerList);
                    AlertDialog dTmp = dialog_builder.create();
                    dTmp.getWindow().setBackgroundDrawableResource(true?R.drawable.popup_shadow_d:R.drawable.popup_shadow_l);
                    a.fix_full_screen(dTmp.getWindow().getDecorView());
                    dTmp.getWindow().setDimAmount(0);
                    dTmp.show();
                    d=dTmp;
                    VICMainAppOptions.isTransparentDialog =false;
                }
            return;
		}
        try {
            int position = (int) v.getTag(R.id.position);
            onListItemClick(position);
        } catch (Exception ignored) {}
    }

    @Override
    public boolean onLongClick(View v) {
		VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return false;
        switch (v.getId()){
            case R.id.check1:
                ViewGroup toolbar_action1 = HeaderView.findViewById(R.id.toolbar_action1);
                for (int i = 0; i < HeaderView.getChildCount(); i++) {
                    HeaderView.getChildAt(i).setEnabled(false);
                }
                toolbar_action1.setAlpha(0.5f);
            return true;
            case R.id.check2:
                HeaderView.findViewById(R.id.toolbar_action2).setOnClickListener(vv -> {});
            return true;
			case R.id.equaltweaker:
				if(a.opt.setEqualizerShown(!a.opt.isEqualizerShown())){
					Turn_On_Equalizer(true);
				}else if(mEqualizerLayer!=null){
					mEqualizerLayer.setVisibility(View.GONE);
				}
				AdjustEquLayer(mDrawerRootFrame.getWidth(), mDrawerRootFrame.getHeight());
			return true;
        }
        return false;
    }

    private void setRate(double speed,boolean update) {
        VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
        mRate=speed;
        if(update){
            try{
                a.mMediaPlayerCompat.setRate((float) mRate);
            } catch (Exception e) {
                a.showT("不支持的速度："+speed);
                return;
            }
        }
        if(speed<=1.0/8){
            int base = 8;
            while(speed<1.0/base){
                base*=2;
            }
            manitv1.setFraction((int) (speed*base), base);
        }else{
            manitv1.setFloat((float) speed);
        }
        //CMN.Log("raterate",speed);
    }
    FilePickerDialog fpickerd;
    @SuppressLint("ResourceType")
    public void onListItemClick(int position) {
        VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
        switch(position) {
            case 1://打开
                a.onPause();
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MULTI_MODE;
                properties.selection_type = DialogConfigs.FILE_SELECT;
                properties.root = new File("/");
                properties.error_dir = new File(Environment.getExternalStorageDirectory().getPath());
                File dwlFn;
                if(a.mMediaPlayerCompat.currentFile!=null){
                    dwlFn = a.mMediaPlayerCompat.currentFile.getParentFile();
                }else
                    dwlFn = new File(Environment.getExternalStorageDirectory(), "Download");
                properties.offset = dwlFn;
                properties.opt_dir=new File(a.opt.pathTo(a.getApplicationContext())+"favorite_dirs/");
                if(a.mMediaPlayerCompat.currentFile!=null)
                    properties.dedicatedTarget=a.mMediaPlayerCompat.currentFile.getName();
                properties.opt_dir.mkdirs();
                properties.extensions = ExtensionHelper.FOOTAGE;
                properties.title_id = R.string.app_name;
                properties.isDark = a.AppWhite==Color.BLACK;
                FilePickerDialog dialog = new FilePickerDialog(a, properties);
                CMNF.AssetMap = CMN.AssetMap;
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override public void onSelectedFilePaths(String[] files, String currentPath) {
                        a.OpenNewFiles(files);
                    }
                    @Override public void onEnterSlideShow(Window win, int delay) {
                        a.EnterSlideShowMode(win, delay);
                    }
                    @Override public void onExitSlideShow() {
                        if(a.globalmask!=null) a.globalmask.setVisibility(View.GONE);
                    }
                    @Override public Activity getDialogActivity() {
                        return a;
                    }
                });
                fpickerd=dialog;
                dialog.show();
                dialog.setOnCancelListener(dialogInterface -> {
                    a.fix_full_screen(null);a.onResume();
                });
                //a.fix_full_screen(dialog.getWindow().getDecorView());
                //CMN.recurseLogCascade(dialog.getView());
            break;
            case 3:{//视频输出
                a.FFStamp = a.opt.getFirstFlag();
                androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(a);
                builder2.setSingleChoiceItems(R.array.voutmode, a.opt.getPlayerType(), (dialog12, which) -> {
                    TextView tv  = (TextView) ((AlertDialog) dialog12).getListView().getTag();
                    a.opt.setPlayerType(which);
                    switch(which){
                        case 0:
                            tv.setText(R.string.use_native);
                            break;
                        case 1:
                            tv.setText(R.string.use_google);
                            break;
                        case 2:
                            tv.setText(R.string.use_vlc);
                            break;
                        case 3:
                            tv.setText(R.string.use_bilibili);
                            break;
                    }
                }).setOnDismissListener(dialog1 -> {
                    if(a.FFStamp != a.opt.getFirstFlag()) {
                        a.mVideoWidthReq=true;
                        boolean needRecreate = a.opt.getPlayerType()!=a.mMediaPlayerCompat.PlayerType;
                        if(!needRecreate){
                            if(a.opt.getAccurateSeek(a.FFStamp)!=a.opt.getAccurateSeek()||a.opt.get_USE_SURFACE_VIEW(a.FFStamp)!=a.opt.get_USE_SURFACE_VIEW())
                                needRecreate=true;
                            if(!needRecreate) {
                                if (!a.opt.getVoiceOnly(a.FFStamp) && a.opt.getVoiceOnly())
                                    needRecreate = a.mMediaPlayerCompat.detachRuninngView();
                                else if (a.opt.getVoiceOnly(a.FFStamp) && !a.opt.getVoiceOnly())//rising edge
                                    if(a.opt.getPlayerType()==FilmCapsule.Native_player ||a.opt.getPlayerType()==FilmCapsule.BZhan_player){
                                        needRecreate=true;
                                    }else
                                        a.mMediaPlayerCompat.attachViews();
                            }
                        }
                        if(needRecreate){
                            //CMN.Log("————needRecreate————");
                            a.mMediaPlayerCompat.onStartSTime = a.mMediaPlayerCompat.getTime();
                            a.mMediaPlayerCompat.refreshRenderViews();
                            a.mMediaPlayerCompat.createPlayer(a.opt.getPlayerType());
                            a.mMediaPlayerCompat.attachViews();
                            //a.mMediaPlayerCompat.ShutDownEqualizer();
                            a.mMediaPlayerCompat.playMediaAtPath(null);
                            if(a.opt.isEqualizerEnabled()){
                                a.mMediaPlayerCompat.ShutDownEqualizer();
                                a.mMediaPlayerCompat.equalizer=null;
                                isFirstTurnOn=true;
                                Turn_On_Equalizer(true);
                            }
                        }
                        a.ExLearnringMode();
                    }
                    a.fix_full_screen(null);
                })//.setPositiveButton(R.string.yes_button_label,null)
                        .setSingleChoiceLayout(R.layout.select_dialog_singlechoice_material_holo)
                ;
                androidx.appcompat.app.AlertDialog dTmp = builder2.create();
                dTmp.show();
                Window win = dTmp.getWindow();
                win.setBackgroundDrawable(null);
                win.setDimAmount(0.5f);
                win.getDecorView().setBackgroundResource(R.drawable.dm_dslitem_dragmy);
                win.getDecorView().getBackground().setColorFilter(new ColorMatrixColorFilter(Toastable_Activity.NEGATIVE));
                win.getDecorView().getBackground().setAlpha(128);
                AlertDialogLayout pp =  win.findViewById(R.id.parentPanel);
                pp.addView(getLayoutInflater().inflate(R.layout.circle_checker_item_menu_titilebar,null),0);
                ((ViewGroup)pp.getChildAt(0)).removeViewAt(0);
                ((ViewGroup)pp.getChildAt(0)).removeViewAt(1);
                TextView titlebar = ((TextView) ((ViewGroup) pp.getChildAt(0)).getChildAt(0));
                titlebar.setGravity(GravityCompat.START);
                titlebar.setPadding((int) (10*a.dm.density), (int) (6*a.dm.density),0,0);
                titlebar.setText(R.string.set_vout);
                dTmp.setCanceledOnTouchOutside(true);
                dTmp.getListView().setPadding(0,0,0,0);

                VoutClicker mVoutClicker = new VoutClicker(a.opt);
                CheckedTextView cb1 = (CheckedTextView) getLayoutInflater().inflate(R.layout.select_dialog_multichoice_material,null);
                cb1.setText(R.string.use_surfaceview);
                cb1.setId(R.string.use_surfaceview);
                CheckedTextView cb2 = (CheckedTextView) getLayoutInflater().inflate(R.layout.select_dialog_multichoice_material,null);
                cb2.setText(R.string.voice_only);
                cb2.setId(R.string.voice_only);
                CheckedTextView cb3 = (CheckedTextView) getLayoutInflater().inflate(R.layout.select_dialog_multichoice_material,null);
                //ViewGroup cb3_tweaker = (ViewGroup) getLayoutInflater().inflate(R.layout.select_dialog_multichoice_material_seek_tweaker,null);
                cb3.setText(R.string.accurate_seek);
                cb3.setId(R.string.accurate_seek);
                cb1.setChecked(a.opt.get_USE_SURFACE_VIEW());
                cb2.setChecked(a.opt.getVoiceOnly());
                cb3.setChecked(a.opt.getAccurateSeek());
                cb1.setOnClickListener(mVoutClicker);
                cb2.setOnClickListener(mVoutClicker);
                cb3.setOnClickListener(mVoutClicker);
                pp.addView(cb1,3);
                pp.addView(cb3,4);
                //tv.addView(cb3_tweaker,5);
                pp.addView(cb2,6);

                int maxHeight = (int) (a.root.getHeight() - 3.5 * getResources().getDimension(R.dimen._50_));
                if(getResources().getDimension(R.dimen.item_height)*(4+2)>=maxHeight)
                    dTmp.getListView().getLayoutParams().height=maxHeight;
                dTmp.getListView().setTag(titlebar);
                break;
            }
            case 5:{//全景视频
                a.FFStamp = a.opt.getFirstFlag();
                androidx.appcompat.app.AlertDialog.Builder builder2 = new androidx.appcompat.app.AlertDialog.Builder(a)
                        .setOnDismissListener(dialog1 -> {
                    if(a.FFStamp != a.opt.getFirstFlag()) {
                        a.opt.putFirstFlag(false);
                        boolean needRecreate = false;//a.opt.getPanoramaMode()!=a.opt.getPanoramaMode(a.FFStamp);
                        //needRecreate|=a.mMediaPlayerCompat.detachRuninngView();
                        a.mMediaPlayerCompat.deleteVideoView();
                        //if(!needRecreate)
                        //    a.mMediaPlayerCompat.attachViews();
                        if(!a.opt.getVoiceOnly()){
                            a.mVideoWidthReq=true;
                            //CMN.Log("————needRecreate————");
                            a.mMediaPlayerCompat.onStartSTime = a.mMediaPlayerCompat.getTime();
                            a.mMediaPlayerCompat.refreshRenderViews();
                            a.mMediaPlayerCompat.createPlayer(a.opt.getPlayerType());
                            a.mMediaPlayerCompat.attachViews();
                            //a.mMediaPlayerCompat.ShutDownEqualizer();
                            a.mMediaPlayerCompat.playMediaAtPath(null);
                            if(a.opt.isEqualizerEnabled()){
                                a.mMediaPlayerCompat.ShutDownEqualizer();
                                a.mMediaPlayerCompat.equalizer=null;
                                isFirstTurnOn=true;
                                Turn_On_Equalizer(true);
                            }
                        }
                    }
                    a.fix_full_screen(null);
                });
                androidx.appcompat.app.AlertDialog dTmp = builder2.create();
                dTmp.show();
                Window win = dTmp.getWindow();
                win.setDimAmount(0.2f);
                win.setBackgroundDrawable(null);
                win.getDecorView().setBackgroundResource(R.drawable.dm_dslitem_dragmy);
                win.getDecorView().getBackground().setColorFilter(new ColorMatrixColorFilter(Toastable_Activity.NEGATIVE));
                win.getDecorView().getBackground().setAlpha(98);
                AlertDialogLayout tv =  win.findViewById(R.id.parentPanel);
                tv.addView(getLayoutInflater().inflate(R.layout.circle_checker_item_menu_titilebar,null),0);
                ((ViewGroup)tv.getChildAt(0)).removeViewAt(0);
                ((ViewGroup)tv.getChildAt(0)).removeViewAt(1);
                TextView titlebar = ((TextView) ((ViewGroup) tv.getChildAt(0)).getChildAt(0));
                titlebar.setGravity(GravityCompat.START);
                titlebar.setPadding((int) (10*a.dm.density), (int) (6*a.dm.density),0,0);
                titlebar.setText("全景视频");
                dTmp.setCanceledOnTouchOutside(true);

                VoutClicker mVoutClicker = new VoutClicker(a.opt);
                CheckedTextView cb1 = (CheckedTextView) getLayoutInflater().inflate(R.layout.select_dialog_multichoice_material,null);
                cb1.setText(R.string.enabled);
                cb1.setId(R.string.enabled);
                cb1.setChecked(a.opt.getPanoramaMode());
                cb1.setOnClickListener(mVoutClicker);
                CheckedTextView cb2 = (CheckedTextView) getLayoutInflater().inflate(R.layout.select_dialog_multichoice_material,null);
                cb2.setText("自动检测");
                cb2.setId(R.string.use_surfaceview);
                tv.addView(cb1,3);
                break;
            }
            case 6:{
                //toggle seek buttons
                boolean val = a.opt.setSeekBtnShown(!a.opt.isSeekBtnShown());
                int targrtVis = val?View.VISIBLE:View.GONE;
                if(a.widget13.getVisibility()!=targrtVis){
                    a.widget13.setVisibility(targrtVis);
                    a.widget14.setVisibility(targrtVis);
                }
                if(a.widget13.getAlpha()!=1){
                    a.widget13.setAlpha(1);
                    a.widget14.setAlpha(1);
                }
                break;
            }
            case 7:
            case 11:{
				Intent intent = new Intent();
				//((AgentApplication)a.getApplication()).opt=a.opt;
				intent.setClass(a, SettingsActivity.class);
				a.startActivityForResult(intent,111);
				a.mDrawerLayout.closeDrawer(GravityCompat.START);
			}
            break;
        }
    }

	@Override
	public void onDismiss(DialogInterface dialog) {
		d = null;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
        switch(buttonView.getId()) {
            case R.id.sw1:
                if(isChecked){
                    Turn_On_Equalizer(true);
                    equaltweaker.setVisibility(View.VISIBLE);
                }else{
                    a.mMediaPlayerCompat.ShutDownEqualizer();
                    if(mEqualizerLayer!=null && mEqualizerLayer.getVisibility()==View.VISIBLE){
                        mEqualizerLayer.setVisibility(View.GONE);
                        AdjustEquLayer(mDrawerRootFrame.getWidth(), mDrawerRootFrame.getHeight());
                    }
                    equaltweaker.setVisibility(View.GONE);
                }
                if(buttonView.getTag()==null)
                    a.opt.setEqualizerEnabled(isChecked);
                else
                    buttonView.setTag(null);
            break;
            case R.id.sw2:
				a.opt.setRespectAudioManager(isChecked);
				if(a.mMediaPlayerCompat!=null)
				if(isChecked){
					a.mMediaPlayerCompat.requestAudioFocus();
				}else
					a.mMediaPlayerCompat.releaseAudioFocus();
            break;
            case R.id.sw3:
				a.opt.setMute(!isChecked);
                if(a.mMediaPlayerCompat!=null)
                if(!isChecked){
                    a.mMediaPlayerCompat.setVolume(0);
                    if(a.opt.getRespectAudioManager()) a.mMediaPlayerCompat.releaseAudioFocus();
                }else{
					a.mMediaPlayerCompat.setVolume((float) a.mVolume);
					if(a.opt.getRespectAudioManager()) a.mMediaPlayerCompat.requestAudioFocus();
                }
            break;
            case R.id.sw4:
				a.opt.setBackgroundPlayEnabled(isChecked);
            break;
            case R.id.sw5:
                if (isChecked) {
                    if(eqlv1!=null) {
                        ((MarginLayoutParams) mEqualizerLayer.getLayoutParams()).topMargin = HeaderView.getHeight();
                        mEqualizerLayer.setLayoutParams(mEqualizerLayer.getLayoutParams());
                    }
                    a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    if(a.opt.isFullscreenHideNavigationbar()) {
                        View decorView = a.getWindow().getDecorView();
                        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE;
                        decorView.setSystemUiVisibility(uiOptions);
                    }
                    if(Build.VERSION.SDK_INT>19 || buttonView.getTag()==null) {
                        ((MarginLayoutParams) mDrawerList.getLayoutParams()).topMargin = 0;
                        mDrawerList.setLayoutParams(mDrawerList.getLayoutParams());
                    }
                } else{
                    if(eqlv1!=null){
                        ((MarginLayoutParams)mEqualizerLayer.getLayoutParams()).topMargin=HeaderView.getHeight()+CMN.getStatusBarHeight(a);
                        mEqualizerLayer.setLayoutParams(mEqualizerLayer.getLayoutParams());
                    }
                    if(buttonView.getTag()==null) {
                        //CMN.Log("getStatusBarHeight",CMN.getStatusBarHeight(a));
                        a.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        if (a.opt.isFullscreenHideNavigationbar()) {
                            View decorView = a.getWindow().getDecorView();
                            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                            decorView.setSystemUiVisibility(uiOptions);
                        }
                    }
                    if(Build.VERSION.SDK_INT>19 || buttonView.getTag()==null) {
                        ((MarginLayoutParams) mDrawerList.getLayoutParams()).topMargin = CMN.getStatusBarHeight(a);
                        mDrawerList.setLayoutParams(mDrawerList.getLayoutParams());
                    }
                }
                a.opt.setFullScreen(isChecked);
                a.ExLearnringMode();
                if(buttonView.getTag()==null){
                    a.refreshSVLayout(-1);
                }else
                    buttonView.setTag(null);
            break;
            case R.id.sw6:
                a.opt.setVibrationEnabled(isChecked);
            break;
            case R.id.sw7:
                a.opt.setkeepBottomBar(isChecked);
                if(mEqualizerLayer!=null) {
                    AdjustEquLayer(mDrawerRootFrame.getWidth(), mDrawerRootFrame.getHeight());
                    mEqualizerLayer.setLayoutParams(mEqualizerLayer.getLayoutParams());
                }
                a.shunt(true);
            break;
            case R.id.sw8:
                a.opt.setShaffle(isChecked);
            break;
            case R.id.sw9:
                a.opt.setLoopMusic(isChecked);
            break;
            case R.id.sw10:
                a.opt.setSingleMusic(isChecked);
            break;
        }
	}

    /**0 no usage;1 use coded preset ; 2 use disk preset;*/
    int getPresetUsage(int type){
        switch (type){
            case 0://api
            return preset_api_idx & preset_usage_mask;
            case 1://vlc
            return preset_vlc_idx & preset_usage_mask;
        }
        return 0;
    }

    private void setPresetUsage_Coded(int type, int index) {
        switch (type){
            case 0://api
                preset_api_idx=index << 2 | 1;
            break;
            case 1://vlc
                preset_vlc_idx=index << 2 | 1;
            break;
        }
    }

    private void setPresetUsage_Disk(int type, int index) {
        switch (type){
            case 0://api
                preset_api_idx=index << 2 | 2;
            break;
            case 1://vlc
                preset_vlc_idx=index << 2 | 2;
            break;
        }
    }

    int getPresetIndex(int type){
        switch (type){
            case 0://api
            return preset_api_idx >> 2;
            case 1://vlc
            return preset_vlc_idx >> 2;
        }
        return -1;
    }

    public void Turn_On_Equalizer(boolean apply) {
        CMN.Log("preset_states:",preset_vlc_idx,preset_api_idx);
        VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
        if(a.disk_presets ==null) {
            File presetFile = new File(a.getExternalFilesDir(""), "presets.json");
            if (presetFile.exists()) {
                try {
                    FileInputStream fin = new FileInputStream(presetFile);
                    byte[] buffer = new byte[(int) presetFile.length()];
                    int len = fin.read(buffer);
                    String val = new String(buffer, 0, len);
                    //CMN.Log("JSONArray  ",val,len,buffer.length);
                    a.disk_presets = JSONArray.parseArray(val);
                } catch (IOException ignored) {}
            }
            if(a.disk_presets==null)
                a.disk_presets = new JSONArray();
        }
        OUT:
        if(isFirstTurnOn){//检查preset
            int type = a.mMediaPlayerCompat.isApiPlayer()?0:1;
            if(true) {//(checked_slots & (type==0?1:2))==0
                int usage = getPresetUsage(type);
                try {switch (usage) {
                    case 1:
                        CMN.Log("初始化使用编码预设  init_using_coded_preset!", getPresetIndex(type));
                        int index = getPresetIndex(type);
                        switch(a.mMediaPlayerCompat.usePreset(index)){
                            case -1:
                                break OUT;
                            case 0:
                                CurrentPresetName = a.mMediaPlayerCompat.getPresetNames().get(index);
                            case -2:
                            break;
                        }
                    break;
                    case 2:
                        CMN.Log("初始化使用磁盘预设  init_using_disk_preset!", getPresetIndex(type));
                        int bandCount = a.mMediaPlayerCompat.getBandCount();
                        if(bandCount<0)
                            break OUT;
                        JSONArray data = a.disk_presets.getJSONObject(getPresetIndex(type)).getJSONArray("data");
                        for (int i = 0; i < Math.min(bandCount, data.size()); i++) {
                           if(a.mMediaPlayerCompat.setAmp(i, data.getInteger(i))<0)
                               break OUT;
                        }
                        CurrentPresetName = a.disk_presets.getJSONObject(getPresetIndex(type)).getString("pname");
                    break;
                }}catch(Exception e){ e.printStackTrace(); }
                checked_slots |= (type==0?1:2);
            }
            isFirstTurnOn=false;
        }
        if(a.opt.isEqualizerShown() && mEqualizerLayer==null) {
            mEqualizerLayer = (ViewGroup) LayoutInflater.from(a.getApplicationContext()).inflate(R.layout.activity_main_navi_equalizer, mDrawerRootFrame, false);
            eqlv1 = mEqualizerLayer.findViewById(R.id.lv1);
            eqlv1.opt = a.opt;
            eqlv1.mEqualizerListener = a.mMediaPlayerCompat;
            //a.mMediaPlayer.setEqualizer(eqlv1.equalizer);

            mDrawerRootFrame.addView(mEqualizerLayer);
            eqlv1.mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //if(fromUser) {
                        int Amp = progress + eqlv1.baseLevel;
                        int id = seekBar.getId();
                        ((VerticalSeekBar) eqlv1.getChildAt(id)).btmTags.set(0, String.format(Locale.UK,"%.1f", Amp * 1.f / 100) + "db");
                        a.mMediaPlayerCompat.setAmp(id, Amp);
                        //a.mMediaPlayer.setEqualizer(MediaPlayer.Equalizer.createFromPreset(cc++% MediaPlayer.Equalizer.getPresetCount()));
                        //a.mMediaPlayerCompat.setEqualizer(eqlv1.equalizer);
                    //}
                }
                @Override public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override public void onStopTrackingTouch(SeekBar seekBar) { }
            };
        }
        if(apply) {
            try {
                int bandCount = a.mMediaPlayerCompat.getBandCount();//其必先知也，然后方可为。不然，则无以继。
                if(a.opt.isEqualizerShown() && eqlv1!=null) {
                    //eqlv1.bTintBackGround = a.opt.getTintEqBars();
                    eqlv1.setCapacity(bandCount, LayoutInflater.from(a.getApplicationContext()), true, true);
                    //instantiateEqualizer
                    //finger through every equalizer items in this drawer:
                    a.mMediaPlayerCompat.BatchAjustAmp(0,null);
                    eqlv1.post(() -> {
                        for (int i = 0; i < eqlv1.getChildCount(); i++) {
                            VerticalSeekBar skI = ((VerticalSeekBar) eqlv1.getChildAt(i));
                            skI.UpdateMyThumb();
                            //eqlv1.mOnSeekBarChangeListener.onProgressChanged(skI, skI.getProgress(), true);
                        }
                    });
                    mEqualizerLayer.setVisibility(View.VISIBLE);
                    if(sw1.getTag()==null) AdjustEquLayer(mDrawerRootFrame.getWidth(), mDrawerRootFrame.getHeight());
                }else
                    a.mMediaPlayerCompat.BatchAjustAmp(0,null);
            } catch (Exception e) {
                android.util.Log.e(CMN.APPTAG, "Turn_On_Equalizer failed");
                CMN.Log("Turn_On_Equalizer failed ",e);
                //if(mEqualizerLayer!=null) mEqualizerLayer.setVisibility(View.GONE);
            }
        }
    }

    public void decorateHeader(View footchechers) {
        VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
        View.OnClickListener clicker = v -> {
            int id = v.getId();
            switch (id) {
                case R.id.CKShowEq:
                    CheckBox ck = (CheckBox) v;
                    //ck.toggle();
                    a.opt.setEqualizerShown(ck.isChecked());
                    if(ck.isChecked()){
                        Turn_On_Equalizer(true);
                    }else if(mEqualizerLayer!=null){
                        mEqualizerLayer.setVisibility(View.GONE);
                    }
                    AdjustEquLayer(mDrawerRootFrame.getWidth(), mDrawerRootFrame.getHeight());
                break;
                case R.id.CKUseEq:
                    ck = (CheckBox) v;
                    a.opt.setEqualizerReallyEnabled(ck.isChecked());
                    if(ck.isChecked()){
                        Turn_On_Equalizer(true);
                    }else{
                        a.mMediaPlayerCompat.ShutDownEqualizer();
                    }
                    //opt.setInfoShowMore(ck.isChecked());
                break;
                case R.id.CKTintBars:
                    ck = (CheckBox) v;
                    a.opt.setTintEqBars(ck.isChecked());
                    if(a.opt.isEqualizerShown() && eqlv1!=null) {
                        int bandCount = a.mMediaPlayerCompat.getBandCount();
                        //forbid View.setDrawable -> RequestLayout on kitkat
                        VerticalSeekBar.bForbidRequestLayout=true;
                        eqlv1.setCapacity(bandCount, null, false, false);
                        VerticalSeekBar.bForbidRequestLayout=false;
                    }
                    //opt.setInfoShowMore(ck.isChecked());
                break;
            }
        };
        CheckBox ck = footchechers.findViewById(R.id.CKShowEq);
        ck.setChecked(a.opt.isEqualizerShown());
        ck.setOnClickListener(clicker);
        ck = footchechers.findViewById(R.id.CKUseEq);
        ck.setChecked(a.opt.isEqualizerReallyEnabled());
        ck.setOnClickListener(clicker);
        ck = footchechers.findViewById(R.id.CKTintBars);
        ck.setChecked(a.opt.getTintEqBars());
        ck.setOnClickListener(clicker);
    }

    public void decorateFooter(View footchechers) {
        VICMainActivity a = ((VICMainActivity) getActivity()); if(a==null) return;
        View.OnClickListener clicker = v -> {
            int id = v.getId();
            switch (id) {
                case R.id.check1:
                    CircleCheckBox ck = (CircleCheckBox) v;
                    ck.toggle(false);
                    a.opt.setPinTweakerDialog(ck.isChecked());
                break;
                case R.id.check2:
                    ck = (CircleCheckBox) v;
                    ck.toggle(false);
                    a.opt.setNotInOctopusMode(ck.isChecked());
                break;
                case R.id.check3:
                    ck = (CircleCheckBox) v;
                    ck.toggle(false);
                    a.opt.setAutoExpandPresets(ck.isChecked());
                break;
            }
        };
        CircleCheckBox ck = footchechers.findViewById(R.id.check1);
        ck.drawInnerForEmptyState = true;
        if(a.opt.getPinTweakerDialog()) ck.setChecked(true,false);
        ck.setOnClickListener(clicker);
        ck = footchechers.findViewById(R.id.check2);
        ck.drawInnerForEmptyState = true;
        if(a.opt.getNotInOctopusMode()) ck.setChecked(true,false);
        ck.setOnClickListener(clicker);
        ck = footchechers.findViewById(R.id.check3);
        ck.drawInnerForEmptyState = true;
        if(a.opt.getAutoExpandPresets()) ck.setChecked(true,false);
        ck.setOnClickListener(clicker);
    }
}