/* Copyright (C) 2019 KnIfER
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.knziha.ODPlayer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.appcompat.view.menu.MenuItemImpl;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.palette.graphics.Palette;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.colorpicker.ColorPickerDialog;
import com.jaredrummler.colorpicker.ColorPickerDialogListener;
import com.knziha.commands.ExeCommand;
import com.knziha.filepicker.model.AudioCover;
import com.knziha.filepicker.model.GlideCacheModule;
import com.knziha.filepicker.utils.ExtensionHelper;
import com.knziha.filepicker.utils.FU;
import com.knziha.filepicker.utils.FileComparator;
import com.knziha.filepicker.view.CMNF;
import com.knziha.filepicker.view.FilePickerDialog;
import com.knziha.filepicker.widget.CircleCheckBox;
import com.knziha.settings.SettingsActivity;
import com.knziha.text.ColoredAnnotationSpan;
import com.knziha.text.ColoredHighLightSpan;
import com.knziha.text.MonitorTextView;
import com.knziha.text.ScrollViewHolder;
import com.knziha.text.SelectableTextView;
import com.knziha.text.SelectableTextViewBackGround;
import com.knziha.text.SelectableTextViewCover;
import com.knziha.text.ViewPagerHolder;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import java.util.regex.Pattern;

import mp4meta.extractor.MMReader;

public class VICMainActivity extends Toastable_Activity implements Toolbar.OnMenuItemClickListener, SurfaceHolder.Callback, View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {
    public Comparator filecomparator;
    public View globalmask;
    File subscript_file;
    JSONArray disk_presets;
    
    public Drawer drawerFragment;
    public double mVolume=1;

    FrameLayout video_surface_frame = null;
    MonitorTextView mMonitorTextView = null;

    FilmCapsule mMediaPlayerCompat = null;

    int mVideoHeight = 0;
    int mVideoWidth = 0;
    boolean mVideoWidthReq;

    private int mVideoVisibleHeight = 0;
    private int mVideoVisibleWidth = 0;

    float lastMediaPostion = 0;
    long lastMediaTime = 0;
    private ViewGroup seekbargp;
    MarkableSeekBar mSeekBar;

    public ViewGroup bottombar;
    public ViewGroup bottombar2;
    TextView totalT;
    protected TextView currenT;
    boolean isTracking;
    boolean isReallyTracking;
    ImageView mPlay;
    private ImageView mLast;
    private ImageView mNext;
    String runmed;

    private long lastClickTime;
    Toolbar toolbar;
    public DrawerLayoutmy mDrawerLayout;
    private boolean RequestedTimeStamp;
    private DrawerArrowDrawable mDrawerArrow;
    ImageView IMPageCover;
    private TextView toptopT;
    private SeekBar volumeseekbar;
    private ImageView main_progress_bar;
    private RotateDrawable main_progress_bar_d;
    private TextView title;
    boolean isPlayingAsset;
    private ViewGroup main;
    private ImageButton NavigationIcon;
    private View OverflowIcon;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private SplitView textMain;
    private ArrayList<PageHolder> viewList = new ArrayList<>();
    private ArrayList<PageHolder> recycler_bin = new ArrayList<>();
    private long stst;
    private ArrayList<File> LyricsChain = new ArrayList<>();
    private String[] LyricsIncanationChain;
    private int CachedBBSize;
    private final int Match_Width=0;
    private final int Match_Height=1;
    private final int Match_Auto=2;
    private final int Match_None=3;
    private boolean bIsUIHidden;
    private int mAnnotationBG = Color.YELLOW;//Color.YELLOW;
    private int highLightBG = Color.YELLOW;//Color.YELLOW;
    private int mAnnotationUnderlineBG = Color.RED;
    private boolean doubleClickDeteced;
    private float lastPosOnClick;
    float capturedLPOC;
    private boolean isScrollSeeked;
    private float naScrollaco;
    private boolean HorizontalScrolltriggered;
    private boolean HorizontalScrolltriggeredSlite;
    private boolean VerticalScrolltriggered;
    private boolean singlaton_task_assigned;
    private boolean dual_task_assigned;
    private boolean isScrollTweaked;
    private long scrollSeekStart;
    private AudioManager audioManager;
    private LayerDrawable wiget5ld;
    private Configuration mConfiguration;
    private boolean bScreenLocked;
    private ViewPager.OnPageChangeListener mPageChangeListener;
    int adapter_idx=-1;
    private ListView MenuInfoPopup;
    private String InfoStamp;
    private boolean FakedClickRequested;
    double SpeedStamp;
    double VolumeStamp;
    private float SCALEDSLOP;
    private float SWITCHTHREOLD;
    private PopupWindow mConfirmationPopup;
    View widget13,widget14, bookmanagermanagerkit;//,widget15,widget16,widget17
    boolean isDelColorized;
    String PreparedStamp;
    TreeSet<Long> timeline=new TreeSet<>();
    private long lastAttach=-1;
    private Snackbar snackbar;
    private boolean projectdirty;
    private int timehash;
    private int notehash;
    private int permission_asker;

    float pendingTransX = 0;
    float pendingTransY = 0;
    int pendingWidth = 0;
    int pendingHeight = 0;
    float pendingScale = 0;
    int pendingMatchType = -1;
    int preferedMatchType = -1;
    private boolean clickconmsumed;
    private ODData projdatabase;

    private AdapterView.OnItemClickListener menu_clicker;
    private ListPopupWindow menupopup;
	private boolean bIsNoNeedToTakeAfterPW=Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1;
	private boolean bOnlyVoice;
	private int BGBStamp=0xc0333333;
	private int BGB2Stamp=0xc0333333;

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //CMN.Log("KEYCODE_VOLUME_DOWN", event.getAction());
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:{
                if(MenuInfoPopup!=null && MenuInfoPopup.getParent()!=null) {
                    try_dismiss_info();
                    return true;
                }

				if(!bIsNoNeedToTakeAfterPW){
					if(menupopup!=null && menupopup.isShowing()){
						menupopup.dismiss();
						return true;
					}
					if(mConfirmationPopup!=null && mConfirmationPopup.isShowing()){
						mConfirmationPopup.dismiss();
						return true;
					}
				}

                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }

                if(viewList.size()>0){
                    PageHolder holder = viewList.get(viewPager.getCurrentItem());
                    if(holder.tv.clearSelection())
                        return true;
                }

                //写入配置
                if(CheckProject()!=0){
                    return true;
                }else projectdirty = false;
                overridePendingTransition(0,0);
                break;
            }
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(video_surface_frame.getHeight()>=volumeseekbar.getLayoutParams().height){
					boolean isUp = keyCode==KeyEvent.KEYCODE_VOLUME_UP;
					mHandler.removeMessages(115);
					if(volumeseekbar.getAlpha()!=1)volumeseekbar.setAlpha(1);
					if(toptopT.getAlpha()!=1) toptopT.setAlpha(1);
					volumeseekbar.setVisibility(View.VISIBLE);
					audioManager.adjustStreamVolume(
							AudioManager.STREAM_MUSIC,
							isUp?AudioManager.ADJUST_RAISE:AudioManager.ADJUST_LOWER,
							0);
					int val = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
					volumeseekbar.setProgress(val);

					Integer targetRes = (val == 0) ? R.drawable.voice_ic_mute2 : R.drawable.voice_ic;
					if(toptopT.getTag()!=targetRes){
						Drawable d = getResources().getDrawable(targetRes).mutate();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) d.setTint(Color.WHITE);
						int w = (int) (56*opt.dm.density);
						d.setBounds(0,0,w,w);
						toptopT.setCompoundDrawables(d,null,null,null);
						toptopT.setTextSize(35);
						toptopT.setTag(targetRes);
					}
					toptopT.getCompoundDrawables()[0].setColorFilter(null);
					//toptopT.setCompoundDrawablesWithIntrinsicBounds();
					if(val!=0)
						toptopT.setText(String.valueOf(val));
					else
						toptopT.setText(null);
					toptopT.setVisibility(View.VISIBLE);
					mHandler.animator2=1f;
					mHandler.animatorD2=0.15f;
					mHandler.sendEmptyMessageDelayed(115,400);
					return true;
				}
			return false;
            default:
            break;
        }
        return event!=null?super.onKeyDown(keyCode,event):true;
    }

    private int CheckProject() {
        if(!projectdirty) return 0;
        boolean b1;
        if(opt.getSaveProjectToDB() && projdatabase==null)
            projdatabase = new ODData(this, "ODProjects");
        try {
            String keyfilename=mMediaPlayerCompat.currentFile.getAbsolutePath();
            String pName = keyfilename;
            int index = mMediaPlayerCompat.currentFile.getName().lastIndexOf(".");
            if(index!=-1) pName = pName.substring(0,pName.length()-mMediaPlayerCompat.currentFile.getName().length()+index);
            //Gather text spans
            JSONArray annotations = new JSONArray(LyricsChain.size());
            if(viewList.size()>0) {//if inflated
                for (int i = 0; i < LyricsChain.size(); i++) {
                    PageHolder holder = viewList.get(i);
                    ColoredAnnotationSpan[] spans = holder.baseSpan.getSpans(0, holder.baseSpan.length(), ColoredAnnotationSpan.class);
                    String lName = LyricsChain.get(i).getAbsolutePath();
                    boolean isRelative=false;
                    if (lName.startsWith(pName)) {
                        lName = lName.substring(pName.length());
                        isRelative = true;
                    }
                    if(spans.length>0 || !isRelative){
                        JSONArray annotationI = new JSONArray(spans.length + 2);
                        annotationI.add(lName);
                        annotationI.add(isRelative);
                        annotationI.add(holder.subs_timeNodeTree.size());
                        for (ColoredAnnotationSpan sI : spans) {
                            annotationI.add(holder.baseSpan.getSpanStart(sI));
                            annotationI.add(holder.baseSpan.getSpanEnd(sI));
                            annotationI.add(sI.type);
                            annotationI.add(sI.mColor);
                        }
                        annotations.add(annotationI);
                    }
                }
                b1=annotations.size()==0;
            }
            else {
                if(lyrics_projects.size()>0) annotations.addAll(lyrics_projects.values());
                b1=true;
            }
            boolean b2=timeline.size()==0;
            //CMN.Log(b1, b2);
            if(b1 && b2){//delete instead
                projects_cache.put(keyfilename, new JSONObject(0));
                //CMN.Log("I am a island I dont need a friend");
                if(opt.getSaveProjectToDB()){
                    return projdatabase.remove(keyfilename);
                }else{
                    CachedFile input = mMediaPlayerCompat.currentFile;
                    File p = new File(input.getParent(),"ODPlayer");
                    File file = new File(p, input.getName() + ".json");
                    int ret = FU.delete3(getApplicationContext(), file);
                    if(ret!=0) {
                        if(ret==-1) { AskPermissionSnack(root); }
                        else Toast.makeText(getApplicationContext(), getResources().getString(R.string.unno_err)+ret, Toast.LENGTH_LONG).show();
                        return 0;
                    }
                }
                return 0;
            }
            else{
                //Gather time marks
                JSONArray bookmarks = new JSONArray(1);
                JSONArray mk1 = new JSONArray(timeline.size());
                mk1.addAll(timeline);
                bookmarks.add(mk1);
                //create project
                JSONObject project = new JSONObject(2);
                project.put("bkmk", bookmarks);
                project.put("anno", annotations);
                CMN.Log("JSON out : ", project.toString());
                projects_cache.put(keyfilename, project);
                //save project
                if(opt.getSaveProjectToDB()){
                    return projdatabase.insertUpdate(keyfilename, project.toString())==-1?-1:0;
                }else {
                    FileOutputStream fout = getProjectletOutputStream(mMediaPlayerCompat.currentFile);
                    if (fout == null) {
                        return -1;
                    }
                    fout.write(project.toString().getBytes());
                    fout.close();
                    return 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0; //返回0，让它通过
        }
        //return -1;
    }

    private void try_dismiss_info() {
        root.removeView(MenuInfoPopup);
        if(FFStamp!=opt.getFirstFlag())
            opt.putFirstFlag(false);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //CMN.Log("onConfigurationChanged",mConfiguration==newConfig, dm==getResources().getDisplayMetrics());
        opt.dm = dm = getResources().getDisplayMetrics();
        if(newConfig.orientation!=mConfiguration.orientation){
            if(root.getTag()!=null){
                MarginLayoutParams lp = (MarginLayoutParams) root.getLayoutParams();
                int mT=DockerMarginT;
                int mB=DockerMarginB;
                DockerMarginT=DockerMarginL;
                DockerMarginB=DockerMarginR;
                DockerMarginL=mT;
                DockerMarginR=mB;
                lp.leftMargin =   DockerMarginL;
                lp.rightMargin =  DockerMarginR;
                lp.topMargin =    DockerMarginT;
                lp.bottomMargin = DockerMarginB;
                root.setLayoutParams(lp);
            }
            refreshSVLayout(-1);
            ExLearnringMode();
        }
		super.onConfigurationChanged(newConfig);
		mConfiguration.setTo(newConfig);
    }

    @Override
    public void onNewIntent(Intent launcher) {
        super.onNewIntent(launcher);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        stst = System.currentTimeMillis();
        CMNF.constants =0;
        super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,  WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

		checkLanguage();

		opt.getBackgroundLightColor();
		opt.getBackgroundLightColor2();
		GlideCacheModule.path = "/storage/emulated/0/PLOD/thm3";
        GlideCacheModule.bUseLruDiskCache=opt.getUseLruDiskCache();
        audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
        VICMainAppOptions.isLarge = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >=3 ;
        opt.setMatchtType(Match_Width);
        LyricsIncanationChain = "/.en/.ar/.fr/.zh-CN".split("/");
        SCALEDSLOP = ViewConfiguration.get(this).getScaledEdgeSlop();
        SWITCHTHREOLD = 6 * SCALEDSLOP;
        //CMN.Log(SCALEDSLOP,"SCALEDSLOP",SWITCHTHREOLD,"SWITCHTHREOLD");
        //CMN.Log("before onCreate1",System.currentTimeMillis()-stst);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //CMN.Log("before onCreate2 setContentView",System.currentTimeMillis()-stst);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColor(getWindow());
        }
        root = findViewById(R.id.root);
        main = findViewById(R.id.main);
        mSeekBar = findViewById(R.id.seekbar);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
        mSeekBar.tree=timeline;
        if(Build.VERSION.SDK_INT<=19)
			mSeekBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        seekbargp = findViewById(R.id.seekbargp);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        //mDrawerLayout.removeViewAt(mDrawerLayout.getChildCount()-1);
        //main.setFitsSystemWindows(false);
		((ApplicationFrameLayout)root).setOnInterceptListener(this);
        video_surface_frame = findViewById(R.id.video_surface_frame);
        mMonitorTextView = findViewById(R.id.MonitorTextView);
        mMediaPlayerCompat = new FilmCapsule(video_surface_frame, this, opt);
        mMonitorTextView.a = this;
        mMediaPlayerCompat.a = this;
        video_surface_frame.setOnTouchListener(this);
        video_surface_frame.setOnClickListener(this);
        mGestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener(){
            public boolean onDoubleTap(MotionEvent e) {
                //return super.onDoubleTap(e);
                isDoubleTapDetected=true;
                return false;
            }
            @Override
            public boolean onDown(MotionEvent e) {
                //scrollSeekStart = mSeekBar.getProgress();
                scrollSeekStart = mMediaPlayerCompat.getTime();
                return super.onDown(e);
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

                return super.onScroll(e1, e2, distanceX, distanceY);
            }
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e){
                if(useConfirmedClick && !clickconmsumed){
                    if(opt.isInLearningMode() && viewList.size()>0
                        &&viewList.get(viewPager.getCurrentItem()).tv.clearSelection()){
                            return true;
                    }
                    switch_ui_hidden(true);
                    return true;
                }
                return super.onSingleTapConfirmed(e);
            }
        });

        toolbar = findViewById(R.id.toolbar);
        title = toolbar.findViewById(R.id.text1);
        title.setOnClickListener(v -> {
            HashMap xx = MMReader.readMetadata(mMediaPlayerCompat.currentFile);
            CMN.Log(xx);
            CMN.Log(xx.get("dscp"));
            CMN.Log(xx.get("cmt"));
        });
        //toolbar.bReverseActionViews=true;
        toolbar.inflateMenu(R.menu.menu);
        //CMN.Log("before onCreate322",System.currentTimeMillis()-stst);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(this);
        View vTmp = toolbar.getChildAt(toolbar.getChildCount()-1);
        //CMN.recurseLogCascade(vTmp); CMN.Log(vTmp);
        if(vTmp instanceof ImageButton) {
            ViewGroup Menu = (ViewGroup) toolbar.getChildAt(toolbar.getChildCount() - 2);
            OverflowIcon = Menu.getChildAt(Menu.getChildCount() - 1);
            //CMN.Log(OverflowIcon); //ActionMenuPresenter$OverflowMenuButton
            OverflowIcon.setClickable(true);

            NavigationIcon = (ImageButton) vTmp;

            MarginLayoutParams lp = (MarginLayoutParams) NavigationIcon.getLayoutParams();
            //lp.setMargins(-10,-10,-10,-10);
            lp.width = (int) (45 * dm.density);
            NavigationIcon.setLayoutParams(lp);
        }

        toolbar.setOnMenuItemClickListener(this);
        if(opt.getDeletionScheme()==3) {
            toolbar.getMenu().findItem(R.id.toolbar_action14).setIcon(R.drawable.ic_delete_forever_black_24dp);
        }
        toolbar.getMenu().findItem(R.id.toolbar_action11).setTitle(getResources().getStringArray(R.array.decodetype)[opt.getHardWareDecaodeType()%4]);

//        item2.setIcon(mDrawerArrow);
//        item1.setTitle(getResources().getStringArray(R.array.decodetype)[opt.getHardWareDecaodeType()]);
//        item1.setOnClickListener(this);
//        item3.setOnClickListener(this);


        //item2.getLayoutParams().width = (int) getResources().getDimension(R.dimen.abarmax);
        //item3.getLayoutParams().width = (int) getResources().getDimension(R.dimen.abarmax);
        mDrawerArrow = new DrawerArrowDrawable(getApplicationContext());
        mDrawerArrow.setColor(Color.WHITE);
        //item1.getLayoutParams().width=-2;
        //item1.setLayoutParams(item1.getLayoutParams());

        bottombar = findViewById(R.id.bottombar);
        bottombar2 = bottombar.findViewById(R.id.bottombar2);
        mLast = bottombar.findViewById(R.id.wiget2);
        mPlay = bottombar.findViewById(R.id.wiget3);
        mNext = bottombar.findViewById(R.id.wiget4);
        bottombar.findViewById(R.id.wiget1).setOnClickListener(this);
        widget13 = root.findViewById(R.id.browser_widget13);
        widget13.setOnClickListener(this);
        widget14 = root.findViewById(R.id.browser_widget14);
        widget14.setOnClickListener(this);
        if(!opt.isSeekBtnShown()) {
            widget13.setVisibility(View.GONE);
            widget14.setVisibility(View.GONE);
        }
        bookmanagermanagerkit = findViewById(R.id.bookmanagermanagerkit);
        bookmanagermanagerkit.findViewById(R.id.browser_widget15).setOnClickListener(this);
        bookmanagermanagerkit.findViewById(R.id.browser_widget16).setOnClickListener(this);
        bookmanagermanagerkit.findViewById(R.id.browser_widget17).setOnClickListener(this);
        if(opt.isBookmarkManagerShown())
            bookmanagermanagerkit.setVisibility(View.VISIBLE);
        pushassidesidebuttons();
        //mLast.setImageResource(R.drawable.chevron_backward);
        //mNext.setImageResource(R.drawable.chevron_forward);
        //mLast.setColorFilter(Color.WHITE);
        //mNext.setColorFilter(Color.WHITE);

        mLast.setOnClickListener(this);
        mNext.setOnClickListener(this);
        bottombar.findViewById(R.id.wiget5).setOnClickListener(this);
        wiget5ld = (LayerDrawable) ((ImageView)bottombar.findViewById(R.id.wiget5)).getDrawable();
        int mt = opt.getMatchtType();
        wiget5ld.findDrawableByLayerId(R.id.wiget1).setAlpha(mt==Match_Width?255:0);
        wiget5ld.findDrawableByLayerId(R.id.wiget2).setAlpha(mt==Match_Height?255:0);
        mPlay.setOnClickListener(this);
        IMPageCover = findViewById(R.id.cover);
        totalT = findViewById(R.id.totalT);
        currenT = findViewById(R.id.currentT);
        volumeseekbar = findViewById(R.id.volumeseekbar);
        volumeseekbar.getLayoutParams().height= (int) (Math.min(dm.heightPixels,dm.widthPixels)*8.0/15);
        main_progress_bar = findViewById(R.id.main_progress_bar);
        main_progress_bar_d = (RotateDrawable) ((LayerDrawable)main_progress_bar.getBackground()).findDrawableByLayerId(android.R.id.progress);
        //main_progress_bar_d = (RotateDrawable) ((LayerDrawable)getDrawable(R.drawable.progress)).findDrawableByLayerId(android.R.id.progress);

		checkLog(savedInstanceState);

        mConfiguration = new Configuration(getResources().getConfiguration());
    }

	private void checkLanguage() {
		VICMainAppOptions.locale =null;
		String language=opt.getLocale();
		if(language!=null){
			Locale locale = null;
			if(language.length()==0){
				locale=Locale.getDefault();
			}else try {
				if(language.contains("-r")){
					String[] arr=language.split("-r");
					if(arr.length==2){
						locale=new Locale(arr[0], arr[1]);
					}
				}else
					locale=new Locale(language);
			} catch (Exception ignored) { }
			CMN.Log("language is : ", language, locale);
			if(locale!=null)
				forceLocale(this, locale);
		}
	}

	private void checkColor() {
		boolean b1=!systemIntialized || opt.getUseGradientBackground(SFStamp)!=opt.getUseGradientBackground();
		if(b1){
			if(opt.getUseGradientBackground()){
				toolbar.setBackgroundResource(R.drawable.gradient_background_flipped);
				bottombar.setBackgroundResource(R.drawable.gradient_background);
				bottombar.setTag(R.drawable.gradient_background, false);
				toolbar.setTag(R.drawable.gradient_background_flipped, false);
			}else{
				bottombar.setTag(R.drawable.gradient_background, null);
				toolbar.setTag(R.drawable.gradient_background_flipped, null);
			}
		}
		if(b1 || !systemIntialized || BGBStamp!=VICMainAppOptions.BGB || BGB2Stamp!=VICMainAppOptions.BGB2){
			BGBStamp=VICMainAppOptions.BGB;
			BGB2Stamp=VICMainAppOptions.BGB2;
			bottombar_setBackgroundColor(BGBStamp);
			toolbar_setBackgroundColor(BGB2Stamp);
		}
		if(systemIntialized && SFStamp!=opt.getSecondFlag()){
			opt.putSecondFlag();
			SFStamp=opt.getSecondFlag();
		}
	}

	private PointF fitSource(PointF SourceCoord) {
        SourceCoord.x=Math.max(0, Math.min(sWidth, SourceCoord.x));
        SourceCoord.y=Math.max(0, Math.min(sHeight, SourceCoord.y));
        return SourceCoord;
    }

    private void switch_ui_hidden(boolean isFromConfirmed) {
        if(opt.rewindOnPause())
            lastPosOnClick = mMediaPlayerCompat.getPosition();
        if(bIsUIHidden=!bIsUIHidden){
            if(opt.isSeekBtnShown() && opt.isSeekButtonsHideWithUI()){
                widget13.setVisibility(View.GONE);
                widget14.setVisibility(View.GONE);
            }
            if(opt.isBookmarkManagerShown() && opt.isBookmarkManagerHideWithUI()){
                bookmanagermanagerkit.setVisibility(View.GONE);
            }
            if(isFromConfirmed){
                bottombar.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
            }else{
                mHandler.animator=0.1f;
                mHandler.sendEmptyMessage(779);
            }
            if(doubleClickDeteced)
                lastHidClick=-1;
            else
                lastHidClick=System.currentTimeMillis();
            if(MenuInfoPopup!=null && MenuInfoPopup.getParent()!=null) {
                MenuInfoPopup.setVisibility(View.GONE);
            }
        }
        else{//将显
            if(opt.isSeekBtnShown() && opt.isSeekButtonsHideWithUI()){
                widget13.setVisibility(View.VISIBLE);
                widget14.setVisibility(View.VISIBLE);
                if(widget13.getAlpha()!=1){
                    widget13.setAlpha(1);
                    widget14.setAlpha(1);
                }
            }
            if(opt.isBookmarkManagerShown() && opt.isBookmarkManagerHideWithUI()){
                bookmanagermanagerkit.setVisibility(View.VISIBLE);
                if(bookmanagermanagerkit.getAlpha()!=1){
                    bookmanagermanagerkit.setAlpha(1);
                }
            }
            if(!doubleClickDeteced)
                lastHidClick=-1;
            boolean bTransInB = opt.BGB != 0 && opt.isUsingDoubleClick();
            if(bTransInB){
				bottombar_setBackgroundColor(Color.TRANSPARENT);
                if(!(opt.isInLearningMode()))
                    toolbar_setBackgroundColor(Color.TRANSPARENT);
            }
            toolbar.setVisibility(View.VISIBLE);
            showBottomBar();
            toolbar.setAlpha(1);
            if(isFromConfirmed){
                boolean quicker = true;//(System.currentTimeMillis() - lastHidClick) < 500;
                mHandler.animator=0.0f;
                mHandler.animatorD=quicker?0.15f:0.09f;
                mHandler.sendEmptyMessageDelayed(879,  quicker?0:145);
            }else if(bTransInB) {
                boolean quicker = (System.currentTimeMillis() - lastHidClick) < 500;
                mHandler.animator=0.0f;
                mHandler.animatorD=quicker?0.15f:0.09f;
                mHandler.sendEmptyMessageDelayed(879,  quicker?0:145);
            }else{//?
                bottombar_setBackgroundColor(opt.BGB);
                if(!(opt.isInLearningMode()))
					toolbar_setBackgroundColor(opt.BGB2);
            }
            lastHidClick=-1;
            if(MenuInfoPopup!=null && MenuInfoPopup.getParent()!=null) {
                MenuInfoPopup.setVisibility(View.VISIBLE);
                if(!InfoStamp.equals(mMediaPlayerCompat.currentFile.getAbsolutePath())){
                    FakedClickRequested = true;
                    onMenuItemClick(toolbar.getMenu().findItem(R.id.toolbar_action17));
                }
            }
        }
    }

    private void showBottomBar() {
        bottombar.setVisibility(View.VISIBLE);
        if(bottombar2.getVisibility()!=View.VISIBLE)bottombar2.setVisibility(View.VISIBLE);
        if(bottombar.getAlpha()!=1)bottombar.setAlpha(1);
    }

	private void bottombar_setBackgroundColor(int bgb) {
		if(bottombar.getTag(R.drawable.gradient_background)==null)
			bottombar.setBackgroundColor(bgb);
		else
			bottombar.getBackground().setColorFilter(bgb, PorterDuff.Mode.SRC_IN);
	}

	private void toolbar_setBackgroundColor(int bgb) {
		if(toolbar.getTag(R.drawable.gradient_background_flipped)==null)
			toolbar.setBackgroundColor(bgb);
		else
			toolbar.getBackground().setColorFilter(bgb, PorterDuff.Mode.SRC_IN);
	}

	@Override
	protected void onLaunch(Bundle savedInstanceState) {
        mMediaPlayerCompat.createPlayer(opt.getPlayerType());
        String contentpath = null;

        Intent intent = getIntent();
        Uri data = intent.getData();
        //![0]	当使用intent调用本程序
        if (data!=null) {//intent.getType()!=null&&(intent.getType().contains("video/") || intent.getType().contains("audio/"))
            runmed = data.getPath();
            if(data.getScheme()!=null && data.getScheme().startsWith("content")){
                runmed=contentpath;
                contentpath=runmed;
            }
        }

        if(runmed==null && savedInstanceState!=null){
            runmed=savedInstanceState.getString("runmed");
			mMediaPlayerCompat.onStartSTime=savedInstanceState.getLong("time");
            CMN.Log("remembered");
        }

        //CMN.Log("runmed",runmed, data, intent.getStringExtra("path"), data.isAbsolute(), data.isRelative(), data.getScheme());
        if(runmed==null)
            runmed = intent.getStringExtra("path");
        if(runmed==null)
            runmed = intent.getStringExtra("real_file_path_2");
        if(runmed==null && contentpath!=null)
            runmed = FU.getPathFromUri(this, data);

        if(runmed==null)
            runmed = opt.defaultReader.getString("lastRem","/ASSET/j.mp4");

        //CMN.Log("runmed", runmed);
        mMediaPlayerCompat.onLaunch(runmed);
    }

    public void onStart(){
        super.onStart();
    }

    public View getVideoView() {
        return mMediaPlayerCompat.getVideoView();
    }
    
    void shunt(boolean forceShow) {
        if (!opt.keepBottomBar()) {
            bottombar.setVisibility(View.GONE);
            if(widget13.getVisibility()!=View.GONE) {
                widget13.setVisibility(View.GONE);
                widget14.setVisibility(View.GONE);
            }
            if(bookmanagermanagerkit.getVisibility()!=View.GONE) {
                bookmanagermanagerkit.setVisibility(View.GONE);
            }
        }else {// I want to see you!!!
            if(forceShow || !bIsUIHidden) {
                showBottomBar();
            }
            if(opt.isSeekBtnShown() && (!bIsUIHidden || !opt.isSeekButtonsHideWithUI())) {
                widget13.setAlpha(1);
                widget14.setAlpha(1);
                widget13.setVisibility(View.VISIBLE);
                widget14.setVisibility(View.VISIBLE);
            }
            if(opt.isBookmarkManagerShown() && (!bIsUIHidden || !opt.isBookmarkManagerHideWithUI())) {
                bookmanagermanagerkit.setAlpha(1);
                bookmanagermanagerkit.setVisibility(View.VISIBLE);
            }
        }
        if(!bIsUIHidden) {
            toolbar.setVisibility(View.GONE);
        }
    }

    void bling() {
        if(bIsUIHidden){
            bottombar.setVisibility(View.GONE);
            if(opt.isSeekButtonsHideWithUI()){
                widget13.setVisibility(View.GONE);
                widget14.setVisibility(View.GONE);
            }
            if(opt.isBookmarkManagerHideWithUI()){
                bookmanagermanagerkit.setVisibility(View.GONE);
            }

        }
    }


    @Override
    protected void further_loading(Bundle savedInstanceState) {
        super.further_loading(savedInstanceState);
        toptopT = findViewById(R.id.toptopT);
        volumeseekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        isValidationExpected=true;

        reestablish_glidejournal();

        mDrawerLayout.setScrimColor(0x6f1f1f21);
        //mDrawerLayout.setScrimColor(0x3fff610a);
        //ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0);
        //mDrawerToggle.syncState();// 添加按钮


        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener(){
            int EqbarSpacingStamp;
            int EqbarSizeStamp;
            int preset_api_idx_Stamp=0;
            int preset_vlc_idx_Stamp=0;
            boolean bOnceOpenned;
            @Override
            public void onDrawerStateChanged(int arg0) {
               //CMN.Log("drawer", "drawer的状态：" + arg0);
            }

            @Override
            public void onDrawerSlide(@NonNull View arg0, float arg1) {
                SelectableTextView.isDragging=false;
                if(toptopT.getVisibility()==View.VISIBLE)
                    toptopT.setVisibility(View.GONE);
                if(!bIsUIHidden) {//!bIsUIHiddem
                    if (!opt.keepBottomBar()) {
                        bottombar.setAlpha(1 - arg1);
                    }
                    toolbar.setAlpha(1 - arg1);
                    if (arg1 < 0.9) {
                        if (bottombar.getVisibility() != View.VISIBLE) {
                            bottombar.setVisibility(View.VISIBLE);
                        }
                        if (toolbar.getVisibility() != View.VISIBLE)
                            toolbar.setVisibility(View.VISIBLE);
                    } else {
                        shunt(false);
                    }
                }
                if(opt.isBringbackBottombarFromHidden())
                if (opt.keepBottomBar() && bIsUIHidden){
                    showBottomBar();
                    bottombar.setAlpha(arg1);
                    if (opt.isSeekButtonsHideWithUI()) {
                        if (arg1 > 0.1) {
                            if (opt.isSeekBtnShown()) {
                                widget13.setAlpha(arg1);
                                widget14.setAlpha(arg1);
                            }
                            if (opt.isSeekBtnShown()) {
                                if (widget13.getVisibility() != View.VISIBLE) {
                                    widget13.setVisibility(View.VISIBLE);
                                    widget14.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            bling();
                        }
                    }
                    if (opt.isBookmarkManagerHideWithUI()) {
                        if (arg1 > 0.1) {
                            if (opt.isBookmarkManagerShown()) {
                                bookmanagermanagerkit.setAlpha(arg1);
                            }
                            if (opt.isBookmarkManagerShown()) {
                                if (bookmanagermanagerkit.getVisibility() != View.VISIBLE) {
                                    bookmanagermanagerkit.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            bling();
                        }
                    }
                }
                if (!opt.keepBottomBar() || !bIsUIHidden) {
                    if (!bIsUIHidden || !opt.isSeekButtonsHideWithUI()) {
                        if (!opt.keepBottomBar())
                        if (opt.isSeekBtnShown()) {
                            widget13.setAlpha(1 - arg1);
                            widget14.setAlpha(1 - arg1);
                        }
                        if (arg1 < 0.9) {
                            if (opt.isSeekBtnShown()) {
                                if (widget13.getVisibility() != View.VISIBLE) {
                                    widget13.setVisibility(View.VISIBLE);
                                    widget14.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            shunt(false);
                        }
                    }
                    if (!bIsUIHidden || !opt.isBookmarkManagerHideWithUI()) {
                        if (!opt.keepBottomBar())
                        if (opt.isBookmarkManagerShown()) {
                            bookmanagermanagerkit.setAlpha(1 - arg1);
                        }
                        if (arg1 < 0.9) {
                            if (opt.isBookmarkManagerShown()) {
                                if (bookmanagermanagerkit.getVisibility() != View.VISIBLE) {
                                    bookmanagermanagerkit.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            shunt(false);
                        }
                    }
                }
                mDrawerArrow.setProgress(arg1);
            }

            @Override
            public void onDrawerOpened(@NonNull View arg0) {
                EqbarSpacingStamp=opt.getEqBarSpacing();
                EqbarSizeStamp=opt.getEqBarSize();
                preset_api_idx_Stamp=drawerFragment.preset_api_idx;
                preset_vlc_idx_Stamp=drawerFragment.preset_vlc_idx;
                SpeedStamp=drawerFragment.mRate;
                VolumeStamp=mVolume;
                shunt(false);
                bOnceOpenned=true;
            }

            @Override
            public void onDrawerClosed(@NonNull View arg0) {
                if(!bIsUIHidden) {
                    bottombar.setAlpha(1);
                    bottombar.setVisibility(View.VISIBLE);
                    toolbar.setAlpha(1);
                    toolbar.setVisibility(View.VISIBLE);
                }else{
                    if(bottombar.getVisibility()==View.VISIBLE){
                        bottombar.setVisibility(View.GONE);
                    }
                }
                if (opt.isSeekBtnShown()) {
                    if(!bIsUIHidden || !opt.isSeekButtonsHideWithUI()) {
                            widget13.setAlpha(1);
                            widget14.setAlpha(1);
                            if(widget13.getVisibility()!=View.VISIBLE){
                                widget13.setVisibility(View.VISIBLE);
                                widget14.setVisibility(View.VISIBLE);
                            }
                    }else{
                        if(widget13.getVisibility()==View.VISIBLE){
                            widget13.setVisibility(View.GONE);
                            widget14.setVisibility(View.GONE);
                        }
                    }
                }
                if (opt.isBookmarkManagerShown()) {
                    if(!bIsUIHidden || !opt.isBookmarkManagerHideWithUI()) {
                            bookmanagermanagerkit.setAlpha(1);
                            if(bookmanagermanagerkit.getVisibility()!=View.VISIBLE){
                                bookmanagermanagerkit.setVisibility(View.VISIBLE);
                            }
                    }else{
                        if(bookmanagermanagerkit.getVisibility()==View.VISIBLE){
                            bookmanagermanagerkit.setVisibility(View.GONE);
                        }
                    }
                }
                if(bOnceOpenned) {
                    SharedPreferences.Editor putter = null;
                    if (SpeedStamp != drawerFragment.mRate) {
                        putter = opt.defaultputter();
                        putter.putFloat("Rate", (float) (SpeedStamp = drawerFragment.mRate));
                    }
                    if (EqbarSpacingStamp != opt.EqbarSpacing || EqbarSizeStamp != opt.EqbarSize) {
                        if (putter == null) putter = opt.defaultputter();
                        putter.putInt("EqGap", opt.EqbarSpacing);
                        putter.putInt("EqSize", opt.EqbarSize);
                    }
                    if (preset_api_idx_Stamp != drawerFragment.preset_api_idx || preset_vlc_idx_Stamp != drawerFragment.preset_vlc_idx) {
                        if (putter == null) putter = opt.defaultputter();
                        putter.putInt("psetapi", drawerFragment.preset_api_idx);
                        putter.putInt("psetvlc", drawerFragment.preset_vlc_idx);
                    }
                    if (VolumeStamp != mVolume) {
                        if (putter == null) putter = opt.defaultputter();
                        putter.putFloat("Vol", (float) (VolumeStamp = mVolume));
                    }
                    if (FFStamp != opt.getFirstFlag()) {
                        if (putter == null) putter = opt.defaultputter();
                        putter.putLong("MFF", FFStamp = opt.getFirstFlag());
                    }
                    if (SFStamp != opt.getSecondFlag()) {
                        if (putter == null) putter = opt.defaultputter();
                        putter.putLong("MSF", SFStamp = opt.getSecondFlag());
                    }
                    if (putter != null)
                        putter.apply();
                    if(drawerFragment.eq_presets_dirty){//need to save disk presets
                        File presetFile = new File(getExternalFilesDir(""), "presets.json");
                        File parentFile = presetFile.getParentFile();
                        if(parentFile!=null) {
                            if (!presetFile.exists())
                                presetFile.getParentFile().mkdirs();
                            CMN.Log("saving presets...", presetFile.getAbsolutePath());
                            if (parentFile.exists()) {
                                try {
                                    FileOutputStream writter = new FileOutputStream(presetFile);
                                    writter.write(disk_presets.toString().getBytes());
                                    writter.close();
                                } catch (Exception e) {
									showT("Failed to save presets！");
                                }
                            } else
                                showT("Failed to save presets！");
                        }
                        drawerFragment.eq_presets_dirty=false;
                    }
                    bOnceOpenned=false;
                }
            }
        });

        checkMargin();

        //test groups
		((MenuItemImpl)toolbar.getMenu().findItem(R.id.toolbar_action3)).isLongClicked=true;
		onMenuItemClick(toolbar.getMenu().findItem(R.id.toolbar_action3));

        try { getContentResolver().releasePersistableUriPermission( Uri.parse("content://com.android.externalstorage.documents/tree/"+"3532-6465"+"%3A"), Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ignored) {  }

        //if(true)throw new RuntimeException();
        //mDrawerLayout.openDrawer(GravityCompat.START);
        //CMN.debugingVideoFrame=true;
        if(CMN.debugingVideoFrame){
            getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
            MarginLayoutParams lp = (MarginLayoutParams) root.getLayoutParams();
            DockerMarginL = lp.leftMargin  = 50;
            DockerMarginR = lp.rightMargin = 50;
            DockerMarginT = lp.topMargin =    25;
            DockerMarginB = lp.bottomMargin = 25;
            root.setTag(false);
            root.setLayoutParams(lp);
            video_surface_frame.setBackgroundColor(0xff8f8f8f);
        }

        //((MenuItemImpl)toolbar.getMenu().findItem(R.id.toolbar_action8)).isLongClicked=true;
        //onMenuItemClick(toolbar.getMenu().findItem(R.id.toolbar_action8));
		checkColor();

        systemIntialized=true;
    }

	private void reestablish_glidejournal() {
        File rizhi = new File(GlideCacheModule.path, "journal");
        if(opt.getUseLruDiskCache()){
            if(!rizhi.exists()){
                Pattern p = Pattern.compile("[a-z0-9_-]+\\.[0-9]{1,3}");
                File[] arr = new File(GlideCacheModule.path).listFiles(name -> !name.isDirectory() && p.matcher(name.getName()).matches());
                if(arr!=null){
                    ArrayList<File> as = new ArrayList<>(Arrays.asList(arr));
                    Collections.sort(as, (f1, f2) ->
                    {long ret=f1.lastModified()-f2.lastModified();if(ret<0)return -1;if(ret>0)return 1;return 0;});
                    long size_count=0; int trim_start=-1;
                    for (int i = 0; i < as.size(); i++) {
                        size_count+=as.get(as.size()-i-1).length();
                        if(size_count>= DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE){
                            trim_start=i;
                            break;
                        }
                    }
                    if(trim_start!=-1)
                        as.subList(0, as.size() - trim_start + 1).clear();
                    //size_count=0;
                    //for (int i = 0; i < as.size(); i++)   size_count+=as.get(i).length();
                    //CMN.Log("oh oh",size_count-DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE);
                    try {
                        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(rizhi));
                        bo.write("libcore.io.DiskLruCache\n1\n1\n1\n\n".getBytes());
                        for (File fn:as) {String name=fn.getName();name=name.substring(0,name.lastIndexOf(".")); bo.write(("DIRTY "+name+  ("\nCLEAN "+name+" "+fn.length())  +"\n").getBytes(StandardCharsets.US_ASCII));}
                        bo.flush();bo.close();
                    } catch (Exception ignored) {}
                }
            }
        }else{
            rizhi.delete();
        }
    }


    //进度条监听类
    SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            //CMN.Log("onStopTrackingTouch...");
            lastClickTime=System.currentTimeMillis();
            RequestedTimeStamp=true;
            isReallyTracking=isTracking=false;
            //mMediaPlayer.setPosition(seekBar.getProgress()*(1.0f)/mMediaPlayer.getLength());
            //if(isScene2)
            //	mSeekBar.getThumb().setColorFilter(0x00FFFFFF, PorterDuff.Mode.MULTIPLY);
            if(!isDelColorized && !ispauseExpected && !mMediaPlayerCompat.isPlaying()){
                mMediaPlayerCompat.play();
            }
            if(bIsUIHidden)
            if (bottombar.getVisibility() == View.VISIBLE) {
                bottombar.setVisibility(View.GONE);
                bottombar_setBackgroundColor(opt.BGB);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
            isReallyTracking=isTracking=true;
            //if(isScene2)
            //	mSeekBar.getThumb().setColorFilter(null);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser)
        {
            if (fromUser)
            {
                //CMN.Log("seeking...");
                SelectableTextView.lastTouchTime=0;
                isTracking=true;
                capturedLPOC=
                TimeExpection=-1;
                mMediaPlayerCompat.setTime(progress);
                //mMediaPlayerCompat.setPosition(progress*(1.0f)/mMediaPlayerCompat.getLength());
                RequestedTimeStamp=true;
                currenT.setText(CMN.FormTime(progress,1));
            }
            refreshTextViewHighLight(progress);
        }
    };

	public void onTimeChanged(long progress){
        //CMN.Log("vlc_TimeChanged",mMediaPlayer.getVolume(),mMediaPlayer.getTime(),mMediaPlayer.getPosition());
        if (RequestedTimeStamp) {
            //CMN.Log("RequestedTimeStamp");
            lastClickTime = System.currentTimeMillis();
            RequestedTimeStamp = false;
        }

        if(progress>mSeekBar.getMax())
			isValidationExpected=true;

        if (isValidationExpected) {
            long duration = mMediaPlayerCompat.getLength();
            //CMN.Log("onTimeChanged isValidationExpected",duration);
            if(duration>0){
                totalT.setText(CMN.FormTime((int) duration, 1));
                mSeekBar.setMax((int) mMediaPlayerCompat.getLength());
            }else
                return;
        }

        //long progress = mMediaPlayerCompat.getTime();

//        if (TimeExpection > 0) {
//            if (Math.abs(TimeExpection - progress) < 1000)
//                TimeExpection = -1;
//        } else

        if (!ispauseExpected && !isTracking && System.currentTimeMillis() - lastClickTime > 300) {
            mSeekBar.setProgress((int) progress);
            currenT.setText(CMN.FormTime(progress,1));
        }

        //if (IMPageCover.getVisibility() == View.VISIBLE) {removeImageCover(); }

        if (isValidationExpected){
            mSeekBar.invalidate();
            isValidationExpected = false;
        }
    }

    @Deprecated
    void retainVideoPicture() {
        IMPageCover.setLayoutParams(getVideoView().getLayoutParams());
        IMPageCover.setTranslationX(getVideoView().getTranslationX());
        IMPageCover.setTranslationY(getVideoView().getTranslationY());
        IMPageCover.setScaleX(getVideoView().getScaleX());
        IMPageCover.setScaleY(getVideoView().getScaleY());
        if(IMPageCover.getTag()!=mMediaPlayerCompat.currentFile.getPath()) {
            IMPageCover.setImageBitmap(mMediaPlayerCompat.getVideoBitmap());
            IMPageCover.setTag(mMediaPlayerCompat.currentFile.getPath());
        }
        IMPageCover.setAlpha(1.f);
        IMPageCover.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
		CheckProject();
		try { mMediaPlayerCompat.onClose(); } catch (Exception ignored) {}
        FilePickerDialog.clearMemory(getBaseContext());
        if(projdatabase!=null){
            projdatabase.close();
            projdatabase=null;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        FilePickerDialog.clearMemory(getBaseContext());
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        FilePickerDialog.clearMemory(getBaseContext());
    }

    /** will ask for permissions automatically*/
    private FileOutputStream getProjectletOutputStream(File input) throws FileNotFoundException {
        CMN.Log("getProjectletOutputStream");
        StorageManager sm=(StorageManager)getSystemService(Context.STORAGE_SERVICE);
        File p = new File(input.getParent(),"ODPlayer");
        File file = new File(p, input.getName() + ".json");
        String filePath = input.getAbsolutePath();
        OUT: if (FU.bKindButComplexSdcardAvailable) {
            boolean isPrimary = false; String uuid = null;
            if (FU.bGoodStorageAvailable) {
                StorageVolume sv = sm != null ? sm.getStorageVolume(file) : null;
                if (sv != null) {
                    isPrimary = sv.isPrimary();
                    uuid = sv.getUuid();
                }
            }
            if (uuid == null) {//反射大法
                try {
                    if (FU.csw.init()) {
                        Object[] results = (Object[]) FU.csw.getVolumeList.invoke(sm);
                        if (results != null) {
                            for (Object rI : results) {
                                isPrimary = (boolean) FU.csw.getIsPrimary.invoke(rI);
                                String path = (String) FU.csw.getPath.invoke(rI);
                                if (filePath.startsWith(path = new File(path).getAbsolutePath())) {
                                    uuid = (String) FU.csw.getUuid.invoke(rI);
                                    filePath = filePath.substring(path.length());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int ret=FU.checkSdcardPermission(getApplicationContext(),input);
            if(ret!=0) {
                permission_asker = R.string.stg_ask_to_save;
                if(ret==-1) { AskPermissionSnack(root); }
                else Toast.makeText(getApplicationContext(), getResources().getString(R.string.unno_err)+"1: "+ret, Toast.LENGTH_LONG).show();
                return null;
            }
            if (uuid != null) {
                String fn = file.getAbsolutePath();
                fn = fn.substring(fn.indexOf(uuid) + uuid.length() + 1);
                String url = FU.DOCUMENTTREEURIBASE + uuid + FU.COLON + FU.DOCUMENT + uuid + FU.COLON;
                Uri uri_start = Uri.parse(url);

                String[] list = fn.split("/");
                String MIME;
                int cc = 0;
                for (String dir : list) {
                    MIME = DocumentsContract.Document.MIME_TYPE_DIR;
                    if (cc == list.length - 1)
                        MIME = DocumentsContract.Document.COLUMN_MIME_TYPE;
                    //long timelet = System.currentTimeMillis();
                    //Log.d("fatal createDocumentLet", dir);
                    url += "%2F" + dir;
                    DocumentFile targetUri = DocumentFile.fromSingleUri(getApplicationContext(), Uri.parse(url));
                    if(targetUri==null)
                        break OUT;
                    if (targetUri.exists()) {
                        uri_start = Uri.parse(url);
                    } else
                        uri_start = DocumentsContract.createDocument(getContentResolver(), uri_start, MIME, dir);
                    if (uri_start == null) return null;
                    cc++;
                }
                if (uri_start != null) {
                    ParcelFileDescriptor pFDes = this.getContentResolver().openFileDescriptor(uri_start, "w");
                    if(pFDes!=null)
                        return new FileOutputStream(pFDes.getFileDescriptor());
                }
            }
        }

        //CMN.Log("isPrimary");
        if (!p.exists()) {
            p.mkdir();
        }
        if (p.isDirectory()) {
            return new FileOutputStream(file);
        }
        return null;
    }

    boolean needReAttach = false;
    @Override
    public void onPause() {
        //CMN.Log("onPause!");
        super.onPause();
		if(!(opt.isBackgroundPlayEnabled() || opt.getVoiceOnly() && opt.getAutoNoPauseIfAudioOnly()))
        	mMediaPlayerCompat.onPause();
        stopRudeTL();
    }

    @Override
    public void onResume() {
        //CMN.Log("onresume!");
        super.onResume();
		if(!(opt.isBackgroundPlayEnabled() || opt.getVoiceOnly() && opt.getAutoNoPauseIfAudioOnly()))
		{
			if(drawerFragment.fpickerd==null || !drawerFragment.fpickerd.isShowing())
				mMediaPlayerCompat.onResume();
		}else if(mMediaPlayerCompat.isApiPlayer() && PreparedStamp!=null && PreparedStamp.equals(mMediaPlayerCompat.currentFile.getAbsolutePath())){
			setRudeTL();
		}
    }

	public void forceLocale(Context context, Locale locale) {
		Configuration conf = context.getResources().getConfiguration();
		conf.setLocale(locale);
		context.getResources().updateConfiguration(conf, context.getResources().getDisplayMetrics());

		//Configuration systemConf = Resources.getSystem().getConfiguration();
		//systemConf.setLocale(locale);
		//Resources.getSystem().updateConfiguration(conf, context.getResources().getDisplayMetrics());
		//context.createConfigurationContext(conf);
		//Locale.setDefault(locale);
	}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(systemIntialized && hasFocus){
            fix_full_screen(getWindow().getDecorView());
			checkColor();
			if(VICMainAppOptions.locale==null)
				recreate();
            //file-based UI-less command tool
            File additional_config = new File(Environment.getExternalStorageDirectory(),"PLOD/runtime-commands.txt");
            if(additional_config.exists()) {
                try {
                    BufferedReader in = new BufferedReader(new FileReader(additional_config));
                    String line;
                    while ((line = in.readLine()) != null) {
                        ProcessCMDLine(line);
                    }
                } catch (Exception e) {//CMN.Log(e);
                }
            }
        }
    }

    private void ProcessCMDLine(String line) {
        try {
            String[] arr = line.split(":", 2);
            if (arr.length == 2) {
                switch (arr[0]) {
                    case "shift span"://移动选中笔记
                        if(opt.isInLearningMode() && viewList.size()>0 && textMain!=null && viewPager.getCurrentItem()<viewList.size()){
                            PageHolder phC = viewList.get(viewPager.getCurrentItem());
                            int st = phC.tv.getSelectionStart();
                            int ed = phC.tv.getSelectionEnd();
                            if(st!=-1){
                                int offset = Integer.valueOf(arr[1]);
                                if(offset!=0){
                                    SpannableString baseSpan = phC.baseSpan;
                                    ColoredAnnotationSpan[] spans = baseSpan.getSpans(st, ed, ColoredAnnotationSpan.class);
                                    for (ColoredAnnotationSpan spI:spans) {
                                        baseSpan.setSpan(spI, baseSpan.getSpanStart(spI)+offset, baseSpan.getSpanEnd(spI)+offset, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                                    }
                                    projectdirty=true;
                                }
                            }
                        }
                    break;
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    public void fix_full_screen(@Nullable View decorView) {
        if(opt.isFullScreen() && opt.isFullscreenHideNavigationbar()) {
            if(decorView==null) decorView=getWindow().getDecorView();
            //int options = decorView.getSystemUiVisibility();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    ;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    boolean ispauseExpected;
    private boolean isValidationExpected;
    long TimeExpection=-1;
    Timer timer = new Timer();

    @SuppressLint({"ResourceType", "StaticFieldLeak"})
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        MenuItemImpl mmi = (MenuItemImpl)item;
        boolean isLongCliced=mmi.isLongClicked;
		switch(id) {
			case R.id.toolbar_action18:{//切割
				boolean accurate_recode=mmi.isLongClicked?true:false;
				new AsyncTask<String,Object,String>(){
					private ExeCommand cmd;
					int totalTasks=0;
					int processing=0;
					volatile float framestime=0;
					volatile String CurrentCommand;
					volatile boolean bAborted=false;
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						dv=getLayoutInflater().inflate(R.layout.dialog_progress, null);
						AlertDialog.Builder builder2 =
								new AlertDialog.Builder(VICMainActivity.this).setView(dv);
						AlertDialog dTmp = builder2.create();
						dTmp.setCancelable(false);
						dTmp.show();
						Window win = dTmp.getWindow();
						win.setBackgroundDrawableResource(R.drawable.popup_shadow);
						win.setDimAmount(0.5f);
						AlertDialogLayout pp =  win.findViewById(R.id.parentPanel);
						d=dTmp;

						win.getAttributes().height = (int) (250*dm.density);
						win.setAttributes(win.getAttributes());

						TextView tv = win.findViewById(R.id.title);
						tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
						String val=mMediaPlayerCompat.currentFile.getName();
						totalTasks=mSeekBar.tree.size()/2;
						processing=0;
						tv.setText(val);
						tv.post(() -> {//从中截断
							if(tv.getLineCount()==2){
								String val1 =tv.getText().toString();
								Layout layout = tv.getLayout();
								int length= val1.length();
								int off = layout.getOffsetForHorizontal(1, tv.getWidth());
								if(off<length){
									int PreserveTailLength=off/2+1;
									tv.setText(val1.subSequence(0,off-PreserveTailLength)+"……"+ val1.subSequence(length-PreserveTailLength,length));
								}
							}
						});
						dv.findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								if(cmd!=null) try {
									cmd.stop();
								} catch (Exception e) { }
							}
						});
						dv.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								bAborted=true;
								dv.findViewById(R.id.skip).performClick();
							}
						});
						dv.findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								if(CurrentCommand!=null){
									ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
									ClipData mClipData = ClipData.newPlainText(CMN.APPTAG, CurrentCommand);
									if(cm!=null){
										cm.setPrimaryClip(mClipData);
										sn(R.string.command_copied, null);
									}
								}
							}
						});
					}

					@Override
					protected void onProgressUpdate(Object... values) {
						super.onProgressUpdate(values);
						if(values[0] instanceof Float){
							((TextView)dv.findViewById(R.id.progressFrac)).setText(values[0]+"/"+framestime);
							((SeekBar)dv.findViewById(R.id.seekbar)).setProgress((int)(float)values[0]);
						}else if(values[0] instanceof File){
							if(mMediaPlayerCompat.mPlaylist==null)
								scanInFile();
							mMediaPlayerCompat.mPlaylist.add(adapter_idx+1, new CachedFile(((File)values[0]).getAbsolutePath()));
						}else{
							((TextView)dv.findViewById(R.id.progressFrac)).setText("seeking…");
							((TextView)dv.findViewById(R.id.tv)).setText((++processing)+"/"+totalTasks);
							framestime = (float) values[1];
							CurrentCommand= (String) values[2];
							((SeekBar)dv.findViewById(R.id.seekbar)).setMax((int) framestime);
						}
					}

					@Override
					protected String doInBackground(String... File) {
						Iterator<Long> iter = mSeekBar.tree.iterator();
						long start, end;
						String fname = mMediaPlayerCompat.currentFile.getAbsolutePath();
						int suffix_idx; String file_suffix=".mp4", baseName=fname;
						if((suffix_idx=fname.lastIndexOf("."))!=-1){
							file_suffix=fname.substring(suffix_idx).toLowerCase();
							baseName=fname.substring(0,suffix_idx);
						}

						int cc=0;
						while(iter.hasNext()){
							if(bAborted) break;
							if(iter.hasNext())start=iter.next();else break;
							if(iter.hasNext())end=iter.next();else break;
							String new_path; File new_file;
							while((new_file=new File(new_path=baseName+".schivoNo."+cc+file_suffix)).exists()) cc++;

							long tm = start / 1000;
							int h = (int) (tm / 60 / 60);
							int m = (int) (tm % (60 * 60) / 60);
							int s = (int) (tm % (60 * 60) % 60);
							String tm1 = h+":"+m+":"+s   +"."+start%1000;

							cmd = new ExeCommand().disPosable().listener(line -> {
								//if(line.toLowerCase().contains("android")) sn(line, null);
								//sn(line, null);
								if(line.contains("frame=")){
									int idx = line.indexOf("time=");
									if(idx!=-1){
										int idx2 = line.indexOf(" ", idx);
										if(idx!=-1){
											String timecode=line.substring(idx+5,idx2);
											String[] arr = timecode.split("[:\\\\.]");
											if(arr.length==4){
												publishProgress(
													Integer.valueOf(arr[0])*60*60+
													Integer.valueOf(arr[1])*60+
													Integer.valueOf(arr[2])+
													Integer.valueOf(arr[3])*0.01f
												);
											}
										}
									}
								}
							});
							String inputfile=" -accurate_seek "+"-i "+"\""+fname+"\"";
							String sst=" -ss "+ (tm1)+"  -t  "+1.0*(end-start)/1000;
							String codec,offset_and_input;

							codec=accurate_recode?" -codec copy -c:v libx264 -avoid_negative_ts 1 -strict -2 ":
									" -codec copy -avoid_negative_ts 1 -strict -2 ";
							offset_and_input=accurate_recode?inputfile+sst
									:sst+inputfile;

							String FFCOMMAND="./ffmpeg"+offset_and_input+codec+"\""+new_path+"\"";
							publishProgress(new_path, (end-start)*1.0f/1000, FFCOMMAND);
							int ret=cmd.run("export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/data/data/com.knziha.ODPlayer/ffmpeg\n"
									, "cd /data/user/0/com.knziha.ODPlayer/ffmpeg\n"
									, "chmod 777 ffmpeg\n"
									, FFCOMMAND, " && "
									,"exit\n"
									,"exit\n"
							);

							if(new_file.exists()){
								new_file.setLastModified(mMediaPlayerCompat.currentFile.lastModified()+cc*100);
								publishProgress(new_file);

								Vibrator vibrator = (Vibrator)VICMainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
								if(vibrator!=null) {
									long[] pattern = {zhengDongLevels[zhengDongLevelsIndex] / 10, zhengDongLevels[zhengDongLevelsIndex], zhengDongLevels[zhengDongLevelsIndex] / 10, zhengDongLevels[zhengDongLevelsIndex]};   // 停止 开启 停止 开启
									vibrator.cancel();
									vibrator.vibrate(pattern, -1);
								}
							}
						}
						return null;
					}

					@Override
					protected void onPostExecute(String s) {
						super.onPostExecute(s);
						if(d!=null){
							d.dismiss(); d=null;
							sn("done.",null);
						}
					}
				}.execute("wahaha");
				toolbar.getMenu().close();
				return true;
			}
			case R.id.toolbar_action3://阅读模式
				if(isLongCliced){
					Intent intent = new Intent();
					intent.putExtra("realm", 1);
					intent.setClass(this, SettingsActivity.class);
					startActivityForResult(intent,111);
					mDrawerLayout.closeDrawer(GravityCompat.START);
				}else{
					if(opt.setInLearningMode(!opt.isInLearningMode())){
						toptopT.setAlpha(1f);
						toptopT.setVisibility(View.VISIBLE);
						Integer textres = R.string.coursera_mode;
						if(textres!= toptopT.getTag()){
							toptopT.setText(textres);
							toptopT.setTextSize(35);
							mHandler.animator2=0.9f;
							toptopT.setCompoundDrawables(null,null,null,null);
							toptopT.setTag(textres);
						}
						mHandler.sendEmptyMessageDelayed(115,480);
						inflateLyrics(LyricsChain);
					}else{
						if(textMain!=null)
							textMain.setVisibility(View.GONE);
						toptopT.setVisibility(View.GONE);
					}
					opt.putFirstFlag(false);
					if(opt.getVoiceOnly())
						ExLearnringMode();
					else
						refreshSVLayout(-1);
					toolbar.getMenu().close();
				}
			return true;
		}

		if(isLongCliced){
            //if(!((MenuItemImpl) item).isActionButton())
            //    toolbar.getMenu().close();
            switch(id) {
                case R.id.toolbar_action1:{
                    if(opt.setBookmarkManagerShown(!opt.isBookmarkManagerShown())){
                        bookmanagermanagerkit.setVisibility(View.VISIBLE);
                    }else{
                        bookmanagermanagerkit.setVisibility(View.GONE);
                    }
                    opt.putFirstFlag(false);
                    if(!((MenuItemImpl) item).isActionButton())
                        toolbar.getMenu().close();
                    return true;
                }
                case R.id.toolbar_action8:{//管理菜单项
					AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
					builder2
							.setSingleChoiceItems(new String[] {}, 0,null);

					AlertDialog dTmp = builder2.create();
					dTmp.show();
					Window window = dTmp.getWindow();
					WindowManager.LayoutParams lp = window.getAttributes();
					lp.dimAmount =0f;
					window.setAttributes(lp);
					window.setBackgroundDrawable(null);
					window.getDecorView().setBackground(null);
					//window.setWindowAnimations(0);

					AlertDialogLayout tv =  window.findViewById(R.id.parentPanel);
					tv.addView(getLayoutInflater().inflate(R.layout.circle_checker_item_menu_titilebar,null),0);

					ToolbarTweakerAdapter adapter = new ToolbarTweakerAdapter(getApplicationContext(),toolbar,opt);
					dTmp.setOnDismissListener(adapter);
					dTmp.setCanceledOnTouchOutside(true);
					dTmp.getListView().setPadding(0,0,0,0);
					dTmp.getListView().setAdapter(adapter);
					int maxHeight = (int) (root.getHeight()-3.5*getResources().getDimension(R.dimen._50_));
					if(getResources().getDimension(R.dimen.item_height)*(toolbar.getMenu().size()+2)>=maxHeight)
						dTmp.getListView().getLayoutParams().height=maxHeight;
//                    dTmp.getListView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                        @Override
//                        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
//                                                   int oldRight, int oldBottom) {
//                            if (v.getMeasuredHeight() > maxHeight) {
//                                v.getLayoutParams().height=maxHeight;
//                            }
//                            v.removeOnLayoutChangeListener(this);
//                        }
//                    });


					if(!((MenuItemImpl) item).isActionButton())
						toolbar.getMenu().close();
					return true;
                }
                case R.id.toolbar_action13:{
					mHandler.removeMessages(115);
					toptopT.setAlpha(1);
					toptopT.setVisibility(View.VISIBLE);
					toptopT.setTag(false);
					if (bScreenLocked = !bScreenLocked) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
						toptopT.setText("Locked");
					} else {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
						toptopT.setText("Auto");
					}
					mHandler.animator2=0.9f;
					mHandler.sendEmptyMessageDelayed(115,400);
					if(!((MenuItemImpl) item).isActionButton())
						toolbar.getMenu().close();
					return true;
                }
                case R.id.toolbar_action17:{
					FFStamp = opt.getFirstFlag();
					AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
					builder2.setSingleChoiceItems(R.array.deletepopstyle, opt.getDeletionScheme(), (dialog, which) -> {
						TextView tv12 = (TextView) ((AlertDialog)dialog).getListView().getTag();
						int old = opt.getDeletionScheme();
						int targetIcon = 0;
						opt.setDeletionScheme(which);
						switch(which){
							case 0:
								if(old==3) targetIcon = R.drawable.ic_delete;
								tv12.setText(R.string.del_schema_1_info);
								break;
							case 1:
								if(old==3) targetIcon = R.drawable.ic_delete;
								tv12.setText(R.string.del_schema_2_info);
								break;
							case 2:
								if(old==3) targetIcon = R.drawable.ic_delete;
								tv12.setText(R.string.del_schema_3_info);
								break;
							case 3:
								if(old!=3) targetIcon = R.drawable.ic_delete_forever_black_24dp;
								tv12.setText(R.string.del_schema_4_info);
								break;
						}
						if(targetIcon!=0)toolbar.getMenu().findItem(R.id.toolbar_action14).setIcon(targetIcon);
					}).setOnDismissListener(dialog -> {
						if(FFStamp != opt.getFirstFlag()){
							opt.putFirstFlag(false);
						}
						fix_full_screen(null);
					})
							.setSingleChoiceLayout(R.layout.select_dialog_singlechoice_material_holo)
					;
					AlertDialog dTmp = builder2.create();
					dTmp.show();
					Window window = dTmp.getWindow();
					WindowManager.LayoutParams lp = window.getAttributes();
					lp.dimAmount =0f;
					window.setAttributes(lp);
					window.setBackgroundDrawable(null);
					window.getDecorView().setBackgroundResource(R.drawable.dm_dslitem_dragmy);
					window.getDecorView().getBackground().setColorFilter(new ColorMatrixColorFilter(Toastable_Activity.NEGATIVE));
					window.getDecorView().getBackground().setAlpha(125);
					ViewGroup tv =  window.findViewById(R.id.parentPanel);
					tv.addView(getLayoutInflater().inflate(R.layout.circle_checker_item_menu_titilebar,null),0);
					((ViewGroup)tv.getChildAt(0)).removeViewAt(0);
					((ViewGroup)tv.getChildAt(0)).removeViewAt(1);
					TextView titlebar = ((TextView) ((ViewGroup) tv.getChildAt(0)).getChildAt(0));
					titlebar.setGravity(GravityCompat.START);
					titlebar.setPadding((int) (10*dm.density), (int) (6*dm.density),0,0);
					titlebar.setText(R.string.del_schema_set);
					dTmp.setCanceledOnTouchOutside(true);
					dTmp.getListView().setPadding(0,0,0,0);
					int maxHeight = (int) (root.getHeight() - 3.5 * getResources().getDimension(R.dimen._50_));
					if(getResources().getDimension(R.dimen.item_height)*(4+2)>=maxHeight)
						dTmp.getListView().getLayoutParams().height=maxHeight;
					dTmp.getListView().setTag(titlebar);

					if(!((MenuItemImpl) item).isActionButton())
						toolbar.getMenu().close();
					return true;
                }
            }
            return false;
        }
        //CMN.Log(item.getClass());
        //showT(item.getClass());
        switch(id){
            case R.id.toolbar_action1://书签 corelet2
                if(lastAttach!=-1){
                    boolean ret = timeline.remove(lastAttach);
                    if(ret){mSeekBar.invalidate();}
                    lastAttach=-1;
					projectdirty=true;
                }
            return true;
            case R.id.toolbar_action2://管理书签
                CMN.Log("toolbar_action2");
                item.setTitle("new titile");
            return true;
            case R.id.toolbar_action13://切换横屏
                int ori = mConfiguration.orientation;
                int DesiredDir;
                if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                    DesiredDir=2;
                    bScreenLocked = DesiredDir!=2;
                    DesiredDir=DesiredDir==0?ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:DesiredDir==1?ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
                    DesiredDir=2;
                    bScreenLocked = DesiredDir!=2;
                    DesiredDir=DesiredDir==0?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:DesiredDir==1?ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
                    setRequestedOrientation(DesiredDir);
                    setRequestedOrientation(DesiredDir);
                }
                //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            break;
            case R.id.toolbar_action10://切换绘制层
                boolean val = opt.set_USE_SURFACE_VIEW(!opt.get_USE_SURFACE_VIEW());
                CMN.Log(val,"切换绘制层");
//                for(int i=0;i<2;i++){
//                    mMediaPlayer.stop();
//                    mMediaPlayer.detachRuninngView();
//                    mMediaPlayer.getVLCVout().detachRuninngView();
//                    //showT(val?"sur":"TTT");
//                    setVideoView(val);
//                    if(!val ){//&& mTextureView.getSurfaceTexture()==null
//                        if(mTextureView.getSurfaceTexture()!=null)
//                            attachViews();
//                        isInitialRun=false;
//                        mTextureView.post(TextureInitialRun);
//                    }else{
//                        attachViews();
//                        if(!ispauseExpected)mMediaPlayer.play();
//                    }
//                }
//                opt.putFirstFlag();
            break;
            case R.id.toolbar_action11://切换硬件加速解码
//                int stamp = opt.getHardWareDecaodeType();
//                int type = opt.setHardWareDecaodeType((opt.getHardWareDecaodeType()+1)%3);
//                CMN.Log("切换硬件加速解码",type);
//                item.setTitle(getResources().getStringArray(R.array.decodetype)[type]);
//                if(!(stamp==1 && type==2)){
//                    val = type != 0;
//                    mMediaPlayer.getMedia().setHWDecoderEnabled(val,val);
//                }
//                if(type==2 && stamp!=2){
//                    Bitmap b = getVideoBitmap();
//                    IMPageCover.setImageBitmap(b.copy(b.getConfig(),true));
//                    IMPageCover.setVisibility(View.VISIBLE);
//                    IMPageCover.setAlpha(1.0f);
//                    if(opt.get_USE_SURFACE_VIEW())
//                        IMPageCover.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                mMediaPlayer.stop();
//                                if(!ispauseExpected)mMediaPlayer.play();
//                            }
//                        });
//                    else{
//                        mMediaPlayer.stop();
//                        if(!ispauseExpected)mMediaPlayer.play();
//                    }
//                }
//                opt.putFirstFlag();
            break;
            case R.id.toolbar_action8:
                if(mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }else try{
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }catch (Exception ignored){}
            break;
            case R.id.toolbar_action12://播放列表
                scanInFile();
                if(mMediaPlayerCompat.mPlaylist==null)
                    break;
                DragSortListView lv = new DragSortListView(this, null);
                lv.setDragEnabled(true);lv.setDragScrollStart(0.1f);lv.setMaxScrollSpeed(1.5f);
                lv.setSlideShuffleSpeed(0.3f);lv.setCollapsedHeight((int) (2*dm.density));
                ((DragSortController)lv.mFloatViewManager).mDragHandleId=R.id.drag_handle;
                lv.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return mMediaPlayerCompat.mPlaylist.size();
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        convertView=inflater.inflate(R.layout.simple_column_dslv_litem, parent, false);
                        TextView tv = convertView.findViewById(android.R.id.text1);
                        convertView.getBackground().setColorFilter(new ColorMatrixColorFilter(Toastable_Activity.NEGATIVE));
                        convertView.getBackground().setAlpha(180);
                        tv.setText(mMediaPlayerCompat.mPlaylist.get(position).getName());
                        tv.setSingleLine();
                        tv.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                        if(position==adapter_idx) {
                            tv.setTextSize(20);
                            SpannableStringBuilder spbuilder = new SpannableStringBuilder(">> ");
                            spbuilder.append(tv.getText());
                            spbuilder.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, tv.getText().length()+3, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                            tv.setText(spbuilder);
                        }else
                            tv.setTextSize(18);
                        tv.setTextColor(Color.WHITE);
                        tv.setTextIsSelectable(false);
                        return convertView;
                    }
                });
                LinearLayout ll = new LinearLayout(getBaseContext());
                ll.setOrientation(LinearLayout.VERTICAL);
                View HeaderView = new View(getBaseContext());
                HeaderView.setBackgroundColor(Color.WHITE);
                ll.addView(HeaderView);
                ll.addView(lv);
                HeaderView.getLayoutParams().height= (int) (50*dm.density);
                BottomSheetDialog bottomPlaylist = new BottomSheetDialog(this);
                HeaderView.setOnClickListener(view -> {
                    int now = bottomPlaylist.getBehavior().getState();
                    if(now==BottomSheetBehavior.STATE_EXPANDED){
                        DisplayMetrics dm2 = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getRealMetrics(dm2);
                        lv.getLayoutParams().height= dm2.heightPixels/2;
                        lv.setLayoutParams(lv.getLayoutParams());
                        bottomPlaylist.getBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
                        view.setBackgroundColor(Color.WHITE);
                    }else{
                        lv.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
                        lv.setLayoutParams(lv.getLayoutParams());
                        bottomPlaylist.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
                        view.setBackgroundColor(Color.BLUE);
                    }
                });
                bottomPlaylist.setContentView(ll);
                bottomPlaylist.show();
                bottomPlaylist.getWindow().setDimAmount(0.2f);
                //bottomPlaylist.getWindow().setBackgroundDrawable(null);
                //bottomPlaylist.getWindow().getDecorView().setBackground(null);
                bottomPlaylist.getWindow().findViewById(R.id.design_bottom_sheet).setBackground(null);
                //fix_full_screen(bottomPlaylist.getWindow().getDecorView());
                lv.setSelection(adapter_idx-2);
                DisplayMetrics dm2 = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getRealMetrics(dm2);
                //CMN.recurseLogCascade(lv);
                lv.getLayoutParams().height= (int) (dm2.heightPixels*bottomPlaylist.getBehavior().getHalfExpandedRatio());
                lv.setLayoutParams(lv.getLayoutParams());
                lv.setFastScrollEnabled(true);
                lv.setOnItemClickListener((adapterView, view, position, l) -> {
					CheckProject();
					if(position!=adapter_idx){
						adapter_idx=position;
						mMediaPlayerCompat.playMediaAtPath(mMediaPlayerCompat.mPlaylist.get(position).getAbsolutePath());
						((BaseAdapter)adapterView.getAdapter()).notifyDataSetChanged();
					}
				});
            break;
            case R.id.toolbar_action14://删除
                int scheme = opt.getDeletionScheme();
                switch (scheme){
                    case 1:
                    case 2:
                        if(mmi.isActionButton()){
                            View actionview = toolbar.findViewById(R.id.toolbar_action14);
                            if(mConfirmationPopup==null){
                                TextView vTmp= new TextView(getApplicationContext());
                                vTmp.setTextColor(Color.WHITE);
                                vTmp.setGravity(Gravity.CENTER);
                                vTmp.setId(android.R.drawable.ic_delete);
								vTmp.setOnClickListener(this);
                                vTmp.setBackgroundResource(R.drawable.round_corner_card);
                                vTmp.setText(R.string.delete_confirm);
                                mConfirmationPopup = new PopupWindow(vTmp,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT,true);
                                //mConfirmationPopup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                //mConfirmationPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                                mConfirmationPopup.setAnimationStyle(R.style.pop_animation);
                            }
                            mConfirmationPopup.getContentView().setTag(scheme==1?false:null);
                            if(mConfirmationPopup.isShowing())
                                break;
                            else{
                                mConfirmationPopup.setHeight(actionview.getHeight()*5/6);
								int xoff = mConfirmationPopup.getContentView().getWidth();
                                if(root.getLayoutDirection()==View.LAYOUT_DIRECTION_RTL)
                                	xoff = -actionview.getWidth();
								else{
									if(xoff==0){
										TextView vTmp = ((TextView)mConfirmationPopup.getContentView());
										xoff=(int) vTmp.getPaint().measureText(vTmp.getText().toString());
									}
									xoff =-(xoff-actionview.getWidth())/2;
								}
								if(bIsNoNeedToTakeAfterPW) {
									//mConfirmationPopup.setTouchModal(true);
									mConfirmationPopup.setFocusable(true);
									mConfirmationPopup.setOutsideTouchable(false);
								}else{
									mConfirmationPopup.setFocusable(false);
									mConfirmationPopup.setOutsideTouchable(false);
									mDrawerLayout.popupToGuard=mConfirmationPopup;
									mConfirmationPopup.setOnDismissListener(() -> mDrawerLayout.popupToGuard=null);
								}
                                mConfirmationPopup.showAsDropDown(actionview, xoff, -actionview.getHeight()*5/6, Gravity.TOP|Gravity.START);
                                mConfirmationPopup.update(actionview, xoff, -actionview.getHeight()*5/6, -1, -1);
                                //mConfirmationPopup.setClippingEnabled(false);
                            }
                            break;
                        }
                    case 0:
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                        builder2.setTitle(mMediaPlayerCompat.currentFile.getName() + " ("+mp4meta.utils.CMN.formatSize(mMediaPlayerCompat.currentfilesize!=0?mMediaPlayerCompat.currentfilesize:(mMediaPlayerCompat.currentfilesize=mMediaPlayerCompat.currentFile.length()))+")")
                                .setPositiveButton(R.string.delete, (dialog, which) -> deleteCurrent());
                        AlertDialog dTmp = builder2.create();
                        dTmp.show();
                        ((TextView)dTmp.findViewById(R.id.alertTitle)).setSingleLine(false);
                        Window window = dTmp.getWindow();
                        WindowManager.LayoutParams lp = window.getAttributes();
                        lp.dimAmount =0f;
                        window.setAttributes(lp);
                    break;
                    case 3:
                        deleteCurrent();
                    break;
                }
            break;
            case R.id.toolbar_action17://get media infomationn
                FFStamp = opt.getFirstFlag();
                if(MenuInfoPopup==null){
                    MenuInfoPopup = new ListView(this);
                    MediaInfoAdapter adapter = new MediaInfoAdapter(getApplicationContext(),null,opt,null,getResources().getStringArray(R.array.media_info));
                    View footchechers = getLayoutInflater().inflate(R.layout.checker2,null);
                    MenuInfoPopup.setAdapter(adapter);
                    MenuInfoPopup.addFooterView(footchechers);
                    MenuInfoPopup.setTag(getLayoutInflater().inflate(R.layout.circle_checker_item_menu_titilebar,null));
                    decorateFooter(footchechers);
                    adapter.windowwidth = (int) (dm.widthPixels-20*dm.density);
                }

                if(!mMediaPlayerCompat.currentFile.getAbsolutePath().equals(InfoStamp)){
                    if(MenuInfoPopup.getHeaderViewsCount()==0){
                        ViewGroup list = (ViewGroup) MenuInfoPopup.getTag();
                        list.removeViewAt(0);
                        TextView tv1 = (TextView) list.getChildAt(1);
                        tv1.setText(R.string.file_info);
                        tv1 = (TextView) list.getChildAt(0);
                        tv1.setGravity(Gravity.CENTER|Gravity.START);
                        MenuInfoPopup.setTag(tv1);
                        MenuInfoPopup.addHeaderView(list);
                    }
                    ((TextView)MenuInfoPopup.getTag()).setText(mMediaPlayerCompat.currentFile.getName());
                    MediaInfoAdapter adapter = ((MediaInfoAdapter)((HeaderViewListAdapter)MenuInfoPopup.getAdapter()).getWrappedAdapter());
                    SimpleDateFormat timemachine = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
                    ArrayList<Object> mArr = new ArrayList<>(10);
                    mArr.add(mMediaPlayerCompat.currentFile.getAbsolutePath());
                    mMediaPlayerCompat.currentfilesize = mMediaPlayerCompat.currentfilesize!=0?mMediaPlayerCompat.currentfilesize:(mMediaPlayerCompat.currentfilesize=mMediaPlayerCompat.currentFile.length());
                    mArr.add(timemachine.format(new Date(mMediaPlayerCompat.currentFile.lastModified())));//1 date
                    mArr.add(adapter.dscp[2]+" : "+mp4meta.utils.CMN.formatSize(mMediaPlayerCompat.currentfilesize));//2 size
                    HashMap directory = null;
                    int suffix_idx;String file_suffix="";
                    if((suffix_idx=mMediaPlayerCompat.currentFile.getName().lastIndexOf("."))!=-1){file_suffix=mMediaPlayerCompat.currentFile.getName().substring(suffix_idx).toLowerCase();}
                    if(ExtensionHelper.MPEGS.contains(file_suffix)){
                        directory = MMReader.readMetadata(mMediaPlayerCompat.currentFile);
                        CMN.Log(directory);
                        if(directory!=null)
                        if(directory.containsKey("ftyp")){
                            directory.put("fform" ,adapter.dscp[3]+" : "+((Object[])directory.get("ftyp"))[0]);
                            //mArr.add(0);//type
                            //mArr.add(1);
                            //mArr.add(2);
                            if(directory.containsKey("moov")){
                                mArr.add(3);//duration
                                //mArr.add(4);//overall bitrate
                                long len = (long)((Object[])directory.get("moov"))[1];
                                String durationTag = adapter.dscp[6] + " : "+ CMN.FormTime(len,1);
                                directory.put("ftime" ,durationTag);
                                durationTag = adapter.dscp[7] + " : "+ (int)(mMediaPlayerCompat.currentfilesize*8.0/1024/len*1000) + " kbit/s";
                                directory.put("frate" ,durationTag);
                            }
                            if(directory.containsKey("dscp") || directory.containsKey("cmt")){
                                mArr.add(5);//comments
                            }
                        }
                    }
                    if(directory==null)
                        directory = new HashMap();
                    directory.put("fsize", mMediaPlayerCompat.currentfilesize);

                    adapter.invalidate(directory,opt,mArr);
                    InfoStamp = mMediaPlayerCompat.currentFile.getAbsolutePath();
                }

                if(!FakedClickRequested) {
                    if(MenuInfoPopup!=null) {
                        if (MenuInfoPopup.getParent() == null) {
                            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -2, Gravity.CENTER);
                            root.addView(MenuInfoPopup, lp);
                        } else
                            try_dismiss_info();
                    }
                }else
                    FakedClickRequested=false;
//                ListView listpopup = new ListView(this);
//                PopupWindow listPopupWindow = new PopupWindow(listpopup,-1,-2,true);
//                listpopup.setAdapter(adapter);
//                listpopup.addFooterView(footchechers);
//                final int maxHeight = (int) (root.getHeight()-3.5*getResources().getDimension(R.dimen._50_));
//                if(getResources().getDimension(R.dimen.item_height)*(adapter.getCount()+2)>=maxHeight)
//                    listpopup.getLayoutParams().height=maxHeight;
//                listPopupWindow.setOutsideTouchable(false);
//                listPopupWindow.showAtLocation(root,Gravity.CENTER,0,0);
//                listPopupWindow.getContentView().setFocusable(true);

//                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
//                builder2.setSingleChoiceItems(new String[] {}, 0,null);
//                AlertDialog dTmp = builder2.create();
//                dTmp.show();
//                Window window = dTmp.getWindow();
//                WindowManager.LayoutParams lp = window.getAttributes();
//                lp.dimAmount =0f;
//                window.setAttributes(lp);
//                window.setBackgroundDrawable(null);
//                window.getDecorView().setBackground(null);
//                dTmp.setOnDismissListener(adapter);
//                dTmp.setCanceledOnTouchOutside(false);
//                dTmp.getListView().setPadding(0,0,0,0);
//                dTmp.getListView().setAdapter(adapter);
//                dTmp.getListView().addFooterView(footchechers);
//                final int maxHeight = (int) (root.getHeight()-3.5*getResources().getDimension(R.dimen._50_));
//                if(getResources().getDimension(R.dimen.item_height)*(adapter.getCount()+2)>=maxHeight)
//                    dTmp.getListView().getLayoutParams().height=maxHeight;
            break;
            case R.id.toolbar_action23:
                drawerFragment.onListItemClick(1);
            break;
        }
        toolbar.getMenu().close();
        return true;
    }


    private boolean filexists(CachedFile cachedFile) {
        return cachedFile.exists()||(cachedFile.getPath().startsWith("/ASSET/") && CMN.AssetMap.containsKey(cachedFile.getPath()));
    }

    private void scanInFile() {
        //CMN.Log("scanInFile??"+mMediaPlayerCompat.mPlaylist);
        if(mMediaPlayerCompat.mPlaylist==null){
            boolean b0=mMediaPlayerCompat.currentFile.getPath().startsWith("/ASSET/");
            if(b0 && (new File("/ASSET/").listFiles()==null || !mMediaPlayerCompat.currentFile.exists())){
                isPlayingAsset=true;
                mMediaPlayerCompat.mPlaylist=new ArrayList<>();
                for(String aI:CMN.AssetMap.keySet()){
                    CachedFile fI = new CachedFile(aI);
                    if(CMN.AssetMap.get(aI)!=null)
                        mMediaPlayerCompat.mPlaylist.add(fI);
                }
            }
            FileFilter mFileFilter = pathname -> {
                int idx = pathname.getName().lastIndexOf(".");
                if(idx!=-1){
					String suffix = pathname.getName().substring(idx).toLowerCase();
                    if((bOnlyVoice?ExtensionHelper.SOUNDS:ExtensionHelper.FOOTAGE).contains(suffix))
                        return true;
                }
                return false;
            };

            filecomparator = new FileComparator(1);
            stst = System.currentTimeMillis();
            File p = mMediaPlayerCompat.currentFile.getParentFile();
            String[] ss = p==null?null:p.list();
            if (ss == null) {
                if(isPlayingAsset){
                    Collections.sort(mMediaPlayerCompat.mPlaylist, filecomparator);
                    adapter_idx = ArrayListTree.binarySearch(mMediaPlayerCompat.mPlaylist,mMediaPlayerCompat.currentFile, filecomparator);
                }
                return;
            }
            ArrayListTree<CachedFile> filetree = new ArrayListTree<CachedFile>(ss.length, filecomparator);
            for (String s : ss) {
                CachedFile f = new CachedFile(p, s);
                if (mFileFilter.accept(f))
                    filetree.insert(f);
            }
            mMediaPlayerCompat.mPlaylist = filetree.data;
            adapter_idx = filetree.lookUpKey(mMediaPlayerCompat.currentFile,true);
            CMN.Log(adapter_idx,"ArrayListTree排序时间", System.currentTimeMillis()-stst);
            //CMN.Log(mMediaPlayerCompat.mPlaylist.size(),mMediaPlayerCompat.mPlaylist.get(mMediaPlayerCompat.mPlaylist.size()-1));
        }
    }


    private Integer[] zhengDongLevels = {20,200};
    public int zhengDongLevelsIndex=0;


    private void deleteCurrent() {
        scanInFile();
        stst = System.currentTimeMillis();
        //if (sv != null)
        //if (!sv.isPrimary()) {
        //    int ret = FU.checkSdcardPermission3(this, currentFile);
        //    if (ret != 0) {
        //        if (ret == -1)
        //            Toast.makeText(getApplicationContext(), "删除需要sd卡读写权限。", Toast.LENGTH_LONG).show();
        //        else
        //            Toast.makeText(getApplicationContext(), "未知错误1:" + ret, Toast.LENGTH_LONG).show();
        //        return;
        //    }
        //}
        Vibrator vibrator = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        if(vibrator!=null) {
            long[] pattern = {zhengDongLevels[zhengDongLevelsIndex] / 10, zhengDongLevels[zhengDongLevelsIndex], zhengDongLevels[zhengDongLevelsIndex] / 10, zhengDongLevels[zhengDongLevelsIndex]};   // 停止 开启 停止 开启
            vibrator.cancel();
            vibrator.vibrate(pattern, -1);
        }

        toptopT.setCompoundDrawables(null,null,null,null);
        toptopT.setText(R.string.deleting);
        toptopT.setVisibility(View.VISIBLE);
        toptopT.setAlpha(1.f);
        int ret = FU.delete3(VICMainActivity.this, mMediaPlayerCompat.currentFile);
        CMN.Log( System.currentTimeMillis()-stst,"shanchu");
        if(ret==0){
            if(mMediaPlayerCompat.mPlaylist!=null)
                mMediaPlayerCompat.mPlaylist.remove(mMediaPlayerCompat.currentFile);
            mMediaPlayerCompat.stop();
            toptopT.setText(R.string.deleted);
            adapter_idx--;
            if(mMediaPlayerCompat.mPlaylist!=null){
                if(mMediaPlayerCompat.mPlaylist.size()>0) {
                    if (adapter_idx == mMediaPlayerCompat.mPlaylist.size() - 1)
                        adapter_idx--;
                }else
                    mMediaPlayerCompat.stop();
            }
            VICMainActivity.this.onClick(mNext);
        }
        else {
            CMN.Log("删除失败！", ret);
            if(ret==-1)
                toptopT.setText(R.string.stg_nopermission);
            else
                toptopT.setText(R.string.deleted_not);
        }
        mHandler.sendEmptyMessageDelayed(115,800);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.browser_widget11://long-click core
                v.setTag(false);
                onClick(v);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.wiget1:
                break;
            case android.R.drawable.ic_delete:
                deleteCurrent();
                if(v.getTag()!=null)
                    mConfirmationPopup.dismiss();
            break;
            case R.id.home:
                onKeyDown(KeyEvent.KEYCODE_BACK, null);
				overridePendingTransition(0,R.anim.activity_anim_out_left);
                finish();
            break;
            case R.id.browser_widget7://时间回溯
                if(viewList.size()>0) {
                    PageHolder holder = viewList.get(viewPager.getCurrentItem());
                    if (holder.tv.hasSelection()) {
                        int offsetIdx = holder.subs_timeNodeTree.lookUpVal(holder.tv.getSelectionStart(), false);
                        if (offsetIdx != -1) {
                            long newT = holder.subs_timeNodeTree.data.get(offsetIdx - 1);
                            mMediaPlayerCompat.setTime(newT);
                            // mMediaPlayer.setPosition(1.f * newT / mMediaPlayerCompat.getLength());
                            refreshTextViewHighLight(newT);
                            //CMN.Log("seek timeprogress", newT);
                            //CMN.Log("seek timeprogress2", (1.f * newT / mMediaPlayer.getLength()) * mMediaPlayer.getLength());
                            //CMN.Log("seek timeprogress3", mMediaPlayer.getPosition() * mMediaPlayer.getLength());
                            //CMN.Log("seek timeprogress3", mMediaPlayer.getTime());
                            lastClickTime = System.currentTimeMillis();
                        }
                    }
                }
            break;
            case R.id.browser_widget10://core2
                 if(viewList.size()==0)return;
                PageHolder holder = viewList.get(viewPager.getCurrentItem());
                if(holder.tv.hasSelection()) {
                    Intent intent = new Intent();
                    intent.setAction("colordict.intent.action.SEARCH");//或者SEARCH
                    intent.putExtra("EXTRA_QUERY", holder.baseSpan.subSequence(holder.tv.mSStart, holder.tv.mSEnd).toString());
					try {
						startActivity(intent);
					} catch (Exception e) {showT(R.string.no_suitable_app);}
				}
            break;
            case R.id.browser_widget13:
                long newT = mMediaPlayerCompat.getTime()-2020;
                mMediaPlayerCompat.setTime(newT);
            break;
            case R.id.browser_widget14:
                newT = mMediaPlayerCompat.getTime()+2020;
                mMediaPlayerCompat.setTime(newT);
            break;
            case R.id.browser_widget11:{//core
                if(viewList.size()==0)return;
                holder = viewList.get(viewPager.getCurrentItem());
                boolean isLong = v.getTag()!=null;
                if(isLong) v.setTag(null);
                if(isLong || holder.tv.hasSelection()){
                    int st = holder.tv.getSelectionStart();
                    int ed = holder.tv.getSelectionEnd();
                    int targetType=1;
                    if(isLong){
                        targetType=2;
                    }
                    ColoredAnnotationSpan[] spans = holder.baseSpan.getSpans(st, ed, ColoredAnnotationSpan.class);
                    for (ColoredAnnotationSpan sI : spans) {
                        if(sI.type==targetType && holder.baseSpan.getSpanStart(sI)==st && holder.baseSpan.getSpanEnd(sI)==ed){
                            holder.baseSpan.removeSpan(sI);
                            holder.tv.clearSelection();
                            reset_time_highlighting(st,ed);
                            return;
                        }
                    }
                    ColoredAnnotationSpan annotion = new ColoredAnnotationSpan(mAnnotationBG);
                    if(isLong){
                        annotion.type=targetType;
                        annotion.mColor = mAnnotationUnderlineBG;
                    }
                    holder.baseSpan.setSpan(annotion,st,ed, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    reset_time_highlighting(st,ed);
                    //TODO str.setSpan(new BackgroundColorSpan(Color.YELLOW), prevTimeNode.getKey().value, nxtTimeNode.getKey().value, Spannable.SPAN_MARK_MARK);
                    //str.setSpan(new BackgroundColorSpan(Color.YELLOW), prevTimeNode.getKey().value, nxtTimeNode.getKey().value, Spannable.SPAN_MARK_MARK);
                    holder.tv.clearSelection();
                    projectdirty=true;
                }else{
                    Intent intent = new Intent();
                    intent.setAction("colordict.intent.action.SEARCH");//或者SEARCH
                    intent.putExtra("EXTRA_QUERY",holder.baseSpan.subSequence(holder.tv.mSStart,holder.tv.mSEnd).toString());
					try {
						startActivity(intent);
					} catch (Exception e) {showT(R.string.no_suitable_app);}
				}
                break;
            }
            case R.id.browser_widget12:{//文本菜单
            	if(menupopup!=null && menupopup.isShowing())
					menupopup.dismiss();
                if(opt.isInLearningMode() && viewList.size()>0 && textMain!=null && viewPager.getCurrentItem()<viewList.size()) {
                    PageHolder phC = viewList.get(viewPager.getCurrentItem());

					ListPopupWindow popup1 = new ListPopupWindow(
							getApplicationContext()
							//, null,androidx.appcompat.R.attr.popupMenuStyle,0
					);
                    TextView textmp = new TextView(getApplicationContext());
                    textmp.setTextSize(15.5f);
                    TextPaint painter = textmp.getPaint();
                    DisplayMetrics dm = getResources().getDisplayMetrics();
                    final int pad = (int) (10*dm.density);
                    final float fontHeight = painter.getFontMetrics().bottom - painter.getFontMetrics().top;
                    MenuAdapter mada;
                    popup1.setAdapter(mada=new MenuAdapter(phC.tv, getApplicationContext()));
                    popup1.setAnchorView(v);
                    popup1.setEnterTransition(null);
                    popup1.setExitTransition(null);
                    int maxchar=0;String LongestItem="";
                    String[] curritems = phC.tv.hasSelection()?mada.menu_selecting:mada.menu_common;
                    for(String sI:curritems){
                        if(sI.length()>maxchar){maxchar=sI.length();LongestItem=sI;}
                    }
                    popup1.setWidth((int) (painter.measureText("  "+LongestItem)+fontHeight+2*pad));
                    popup1.setVerticalOffset(-pad/2);
                    popup1.setDropDownGravity(GravityCompat.START);
                    popup1.setHorizontalOffset((int) (  -3*dm.density));
                    if (menu_clicker == null) {
                        menu_clicker= (parent, view, position, id12) -> {
                            if(opt.isInLearningMode() && viewList.size()>0 && textMain!=null && viewPager.getCurrentItem()<viewList.size()) {
                                PageHolder phCC = viewList.get(viewPager.getCurrentItem());
                                int id_true = ((MenuAdapter) parent.getAdapter()).getId(position);
                                switch (id_true) {
                                    case R.drawable.abc_ic_menu_selectall_mtrl_alpha:
                                        phCC.tv.selectAll();
                                        if(menupopup!=null)menupopup.dismiss();
                                    break;
                                    case R.drawable.fp_copy:
                                        if (phCC.tv.hasSelection()){
                                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                            ClipData mClipData = ClipData.newPlainText(CMN.APPTAG, phCC.tv.getSelectedText());
                                            if(cm!=null){
                                                cm.setPrimaryClip(mClipData);
                                                if(menupopup!=null)menupopup.dismiss();
                                            }
                                        }
                                    break;
                                    case R.drawable.ic_send_black_24dp:
                                        if (phCC.tv.hasSelection()){
                                            Intent intent = new Intent(Intent.ACTION_SEND);
                                            intent.setType("text/plain");
                                            intent.putExtra(Intent.EXTRA_TEXT, phCC.tv.getText().toString());
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            try {
                                                startActivity(Intent.createChooser(intent, CMN.APPTAG));
                                            } catch (Exception ignored) { }
                                        }
                                        break;
                                    case R.drawable.abc_ic_search_api_material:
                                        if (phCC.tv.hasSelection()) {
                                            String text = phCC.tv.getSelectedText();
                                            //Intent Xiiaror = new Intent(Intent.ACTION_SEARCH);//这个方式用不了
                                            //Intent shareIntent = Xiiaror;
                                            //shareIntent.setType("text/html;text/plain");
                                            //shareIntent.putExtra(SearchManager.QUERY, text.replace(".", " "));
                                            //shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, text);
                                            //shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                                            //shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            Uri uri = Uri.parse("https://www.baidu.com/s?wd="+text);
                                            Intent shareIntent = new Intent(Intent.ACTION_VIEW, uri);
                                            try {
                                                startActivity(shareIntent);
                                            } catch (Exception ignored) { }
                                        }
                                        break;
                                    case R.drawable.ic_delete:
                                        if (phCC.tv.hasSelection()) {
                                            SpannableString span = phCC.baseSpan;
                                            ColoredAnnotationSpan[] spans = span.getSpans(phCC.tv.getSelectionStart(), phCC.tv.getSelectionEnd(), ColoredAnnotationSpan.class);
                                            if(spans.length>0){
                                                for (ColoredAnnotationSpan spI:spans) {
                                                    span.removeSpan(spI);
                                                }
                                            }
                                            if(menupopup!=null)menupopup.dismiss();
                                        }
                                        break;
                                    case R.drawable.ic_palette_black_24dp://有两个奥？
                                        ColorPickerDialog asd =
                                                ColorPickerDialog.newBuilder()
                                                        .setDialogId(123123)
														.setInitialColor(Color.YELLOW)
                                                        .create();
                                        asd.setColorPickerDialogListener(new ColorPickerDialogListener() {
                                            @Override
                                            public void onColorSelected(int dialogId, int Color) {
                                            }

                                            @Override
                                            public void onJudging(int color) {

                                            }

                                            @Override
                                            public void onDialogDismissed(int dialogId) {

                                            }});
                                        asd.show(getSupportFragmentManager(),"color-picker-dialog");
                                        break;
                                    case R.mipmap.intervalsele:
                                        phCC.tv.bIntervalSelectionIntented=true;
                                        if(menupopup!=null)menupopup.dismiss();
                                        showT(R.string.ex_sele_nxtlong);
                                        break;
                                    case R.drawable.abc_ic_clear_material:
                                        phCC.tv.clearSelection();
                                        if(menupopup!=null)menupopup.dismiss();
                                        break;
                                }
                            }
                        };
                    }
                    menupopup=popup1;
                    popup1.setOnItemClickListener(menu_clicker);
                    popup1.setOnDismissListener(() -> menupopup=null);
					if(bIsNoNeedToTakeAfterPW)
						popup1.setModal(true);
					popup1.show();
                    if(!bIsNoNeedToTakeAfterPW){
						mDrawerLayout.popupToGuard=popup1.mPopup;
						popup1.setOnDismissListener(() -> mDrawerLayout.popupToGuard=null);
                    }
                }
                break;
            }
            case R.id.wiget3:
                bNautyScroll=false;
                mMediaPlayerCompat.switch_play_state();
            break;
            case R.id.wiget5:
                //int matcht_type = opt.setMatchtType((opt.getMatchtType()+1)%opt.getScreenPhaseCount());
                int matcht_type = (pendingMatchType+1)%opt.getScreenPhaseCount();
                if(opt.getMatchtType()!=Match_Auto)opt.setMatchtType(matcht_type);
                refreshSVLayout(preferedMatchType=matcht_type);
                //CMN.Log("matcht_type",matcht_type, opt.getMatchtType());
                wiget5ld.findDrawableByLayerId(R.id.wiget1).setAlpha(matcht_type==Match_Width?255:0);
                wiget5ld.findDrawableByLayerId(R.id.wiget2).setAlpha(matcht_type==Match_Height?255:0);
            break;
            case R.id.wiget2:
            case R.id.wiget4:
                DeColor();
                mMediaPlayerCompat.PreSwitch();
                scanInFile();
                if(mMediaPlayerCompat.mPlaylist==null)
                    return;
				boolean isLast = id==R.id.wiget2;
                if(mMediaPlayerCompat.mPlaylist.size()>1) {
                    if(ispauseExpected){
                        ispauseExpected=false;
                        mPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                    }
                    if(adapter_idx<-1){
                        adapter_idx=-(adapter_idx+2);
                        CMN.Log("拨乱返正",adapter_idx, mMediaPlayerCompat.mPlaylist.size());
                    }
                    if(isLast) {
                        if(adapter_idx==0) {
                            break;
                        }else{
                            while(--adapter_idx>0 && !filexists(mMediaPlayerCompat.mPlaylist.get(adapter_idx)));
                        }
                    }else {
                        if(adapter_idx==mMediaPlayerCompat.mPlaylist.size()-1) {
                            break;
                        }else{
                            while(++adapter_idx<mMediaPlayerCompat.mPlaylist.size()-1 && !filexists(mMediaPlayerCompat.mPlaylist.get(adapter_idx)));
                        }
                    }
                    mMediaPlayerCompat.pause();  //TODO hmm……
                    //mMediaPlayerCompat.stop();  //TODO hmm……
                    adapter_idx=Math.min(mMediaPlayerCompat.mPlaylist.size()-1, Math.max(0, adapter_idx));
                    //if(adapter_idx<0) adapter_idx+=mMediaPlayerCompat.mPlaylist.size();
                    //adapter_idx = adapter_idx % mMediaPlayerCompat.mPlaylist.size();
                    CheckProject();
                    mMediaPlayerCompat.playMediaAtPath(mMediaPlayerCompat.mPlaylist.get(adapter_idx).getAbsolutePath());
                }else{
                    mMediaPlayerCompat.play();
                }
                if(MenuInfoPopup!=null && MenuInfoPopup.getParent()!=null && MenuInfoPopup.getVisibility()==View.VISIBLE){
                    FakedClickRequested=true;
                    onMenuItemClick(toolbar.getMenu().findItem(R.id.toolbar_action17));
                }
            break;
            case R.id.video_surface_frame:
                //if(opt.isInLearningMode() && viewList.size()>0
                //    &&viewList.get(viewPager.getCurrentItem()).tv.clearSelection()){
                //        clickconmsumed=true;
                //        break;
                //}
                if(!useConfirmedClick){
                    switch_ui_hidden(false);
                }
            break;
            case R.id.browser_widget15://corelet
                timeline.add((long) mSeekBar.getProgress());
                RotateAnimation anima = new RotateAnimation(wheelShiftDegree +0.0f, wheelShiftDegree +180.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
                anima.setDuration(300);
                anima.setFillAfter(true);
                v.startAnimation(anima);
                wheelShiftDegree +=180;
                mSeekBar.invalidate();
                projectdirty=true;
            break;
            case R.id.browser_widget16://官方的类就是好啊?
            case R.id.browser_widget17://下一个！
                isLast = id==R.id.browser_widget16;
                if(root.getLayoutDirection()==View.LAYOUT_DIRECTION_RTL)isLast=!isLast;
                Long progress = isLast?timeline.lower((long) mSeekBar.getProgress()):timeline.higher((long) mSeekBar.getProgress());
                if(progress==null) try { progress=isLast?timeline.last():timeline.first(); } catch (Exception ignored){}
                if(progress==null)break;
                lastClickTime=System.currentTimeMillis();
                mMediaPlayerCompat.setTime(lastAttach=TimeExpection=progress);
                mSeekBar.setProgress(progress.intValue());
                currenT.setText(CMN.FormTime(progress,1));
            break;
        }
    }

	public void onComfyClick(View view){
		if(drawerFragment.fpickerd!=null) {
			int mode = -1;
			switch (view.getId()) {
				case R.id.viewmode1:
					drawerFragment.fpickerd.SelectGridMode(view);
					break;
				case R.id.viewmode2:
					drawerFragment.fpickerd.SelectGridMode(view);
					break;
				case R.id.viewmode3:
					drawerFragment.fpickerd.SelectGridMode(view);
					break;
				case R.id.viewmode4:
					mode=0;
				break;
				case R.id.viewmode5:
					mode = 1;
				break;
				case R.id.viewmode6:
					mode = 2;
				break;
				case R.id.viewmode7:
					mode = 3;
				break;
				case R.id.viewmode8:
					mode = 4;
				break;
				case R.id.viewmode9:
					mode = 5;
				break;
				case R.id.viewmode10:
					mode = 6;
				break;
				case R.id.viewmode11:
					mode = 7;
				break;
			}
			if(mode!=-1)
				drawerFragment.fpickerd.SortModeCommon(view, mode);
		}
	}

    private float wheelShiftDegree =0;
    public long lastHidClick;
    /** 新媒体加载，此时mediaplayer正在准备状态。*/
    public void OnLoadingNewMedia(String path, String Alias) {
		projectdirty = false;
		int suffix_idx;String file_suffix="";
		String filename=mMediaPlayerCompat.currentFile.getName();
		if((suffix_idx=filename.lastIndexOf("."))!=-1){file_suffix=filename.substring(suffix_idx).toLowerCase();}
		bOnlyVoice = ExtensionHelper.SOUNDS.contains(file_suffix);
        preferedMatchType = -1;
        stopRudeTL();
        mSeekBar.setProgress(0);
        currenT.setText("00:00");
        totalT.setText("00:00");
        mVideoWidthReq=true;
        lyrics_projects.clear();
        LyricsChain.clear();
        timeline.clear();
        subscript_file=null;
        if(isPlayingAsset){
            String lyricPath = path.subSequence(0, path.lastIndexOf(".")) + ".txt";
            if(CMN.AssetMap.containsKey(lyricPath))
                subscript_file = new File(lyricPath);
        }
        else {
            int index=path.lastIndexOf(".");
            String qianzhui = index!=-1?path.substring(0, index):path;
            //todo add manually set lyrics.
            for (String sI:LyricsIncanationChain){
                subscript_file = new File(qianzhui + sI + ".srt");
                if(subscript_file.exists()) {
                    LyricsChain.add(subscript_file);
                }
            }
        }
        Read_In_Json_Proj();
        if (opt.isInLearningMode()) {
            inflateLyrics(LyricsChain);
        }
        if(Alias!=null)
            title.setText(Alias);
        else
            decorateTitle();
        ExLearnringMode();
    }

    HashMap<String, JSONArray> lyrics_projects = new HashMap<>();
    HashMap<String, JSONObject> projects_cache = new HashMap<>();
    /**
     * 读取配置*/
    private void Read_In_Json_Proj() {
        String pName = null;
        timehash=0;
        notehash=0;
        if(opt.getSaveProjectToDB() && projdatabase==null)
            projdatabase = new ODData(this, "ODProjects");
        try {
            String keyfilename=mMediaPlayerCompat.currentFile.getAbsolutePath();
            JSONObject project = projects_cache.get(keyfilename);
            if(project==null) {
                File p = new File(mMediaPlayerCompat.currentFile.getParent(), "ODPlayer");
                if (p.exists()) {
                    File record = new File(p, mMediaPlayerCompat.currentFile.getName() + ".json");
                    if (record.exists()) {
                        FileInputStream fin = new FileInputStream(record);
                        byte[] buffer = new byte[(int) record.length()];
                        int len = fin.read(buffer);
                        String val = new String(buffer, 0, len);
                        CMN.Log("JSON in (disk) ", val, len, buffer.length);
                        project = JSONObject.parseObject(val);
                        projects_cache.put(keyfilename, project);
                    }
                }
            }else CMN.Log("JSON in (memory) ", project);
            if(opt.getSaveProjectToDB() && project==null){
                projdatabase.prepareContain();
                String val = projdatabase.get(keyfilename);
                CMN.Log("JSON in (database) ",val);
                project = JSONObject.parseObject(val);
                projects_cache.put(keyfilename, project);
            }
            if(project!=null){
                JSONArray bookmarks = project.getJSONArray("bkmk");
                JSONArray annotations = project.getJSONArray("anno");
                JSONArray bookmark1=bookmarks.size()>0?bookmarks.getJSONArray(0):null;
                //![0] huanyuan starting...
                //![1] 还原时间线
                if(bookmark1!=null){
                    for (int i = 0; i < bookmark1.size(); i++) {
                        timeline.add(bookmark1.getLongValue(i));
                    }
                    timehash=timeline.size();
                    //mSeekBar.invalidate();
                }
                //![2] 还原时之笔记
                notehash=annotations.size();
                for (int i = 0; i < annotations.size(); i++) {
                    JSONArray annI = annotations.getJSONArray(i);
                    String name = annI.getString(0);
                    boolean isRelative = annI.getBoolean(1);
                    if(isRelative){
                        if(pName==null){
                            pName = keyfilename;
                            int index = mMediaPlayerCompat.currentFile.getName().lastIndexOf(".");
                            if(index!=-1)
                                pName = pName.substring(0,pName.length()-mMediaPlayerCompat.currentFile.getName().length()+index);
                        }
                        name = pName+name;
                    }
                    lyrics_projects.put(name, annI);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            projectdirty=true;
        }
    }

    long tracksyncstarttime=0;
    boolean isRudeTLEnabled;
    void setRudeTL(){
        //CMN.Log("setRudeTL");
        isRudeTLEnabled=true;
        Message msg = new Message();
        tracksyncstarttime = System.currentTimeMillis();
        msg.what=123456;
        msg.obj = tracksyncstarttime;
        mHandler.sendMessage(msg);
    }
    void stopRudeTL(){
        isRudeTLEnabled=false;
        mHandler.removeMessages(123456);
    }

    public void OnError() {
        CMN.Log("Error");
        currenT.setText("----");
        totalT.setText("----");
        if(!isDelColorized) {
            long length = -1;
            if(!(mMediaPlayerCompat.player instanceof MediaPlayer))
                length = mMediaPlayerCompat.getLength();
            if (length <= 0) {
                boolean isRedAlert = opt.getPlayerType() != 0 && mMediaPlayerCompat.player instanceof MediaPlayer;
                toolbar.getMenu().findItem(R.id.toolbar_action14).getIcon().setColorFilter(isRedAlert?Color.RED:Color.BLUE, PorterDuff.Mode.SRC_IN);
                isDelColorized = true;
            }
        }
        if(bIsUIHidden){
            switch_ui_hidden(true);
        }
    }

    public void OnPrepared(boolean bInterceptValidation) {
        //CMN.Log("OnPrepared received");
        long duration = mMediaPlayerCompat.getLength();
        if(duration>0){
            //CMN.Log("OnPrepared received duration",duration);
            totalT.setText(CMN.FormTime((int) duration, 1));
            mSeekBar.setMax((int) mMediaPlayerCompat.getLength());
            if(bInterceptValidation)isValidationExpected=false;
        }
        PreparedStamp=mMediaPlayerCompat.currentFile.getAbsolutePath();
        DeColor();
		//mHandler.sendEmptyMessageDelayed(15576, 2000);
		AmendEq();
    }

    private void AmendEq() {
        if(opt.isEqualizerEnabled()) {
            int id = mMediaPlayerCompat.getAudioSessionId();
            if (id != 0) {
                if (mMediaPlayerCompat.session_id_stamp != id) {
                    CMN.Log("—————— Amending...");
                    mMediaPlayerCompat.BatchAjustAmp(0, null);
                    int bc = mMediaPlayerCompat.getBandCount();
                    if (drawerFragment.eqlv1 == null || drawerFragment.eqlv1.getChildCount() != bc) {
                        drawerFragment.Turn_On_Equalizer(true);
                    }
                }
            }else{
                CMN.Log("—————— Amend later !!!");
                mHandler.sendEmptyMessageDelayed(15576, 100);
            }
        }
    }

    private void DeColor() {
        if(isDelColorized){
            toolbar.getMenu().findItem(R.id.toolbar_action14).getIcon().setColorFilter(null);
            isDelColorized=false;
        }
    }

    public void setValidationExpected(boolean inval) {
        isValidationExpected=inval;
    }

    public void EnterSlideShowMode(Window win, int delay) {
        if(globalmask==null){
            globalmask=new View(getApplicationContext());
            globalmask.setBackgroundColor(Color.BLACK);
        }
        if(globalmask.getParent()==null) root.addView(globalmask);
        globalmask.setAlpha(0.f);
        globalmask.setVisibility(View.VISIBLE);
        mHandler.animator = 0.1f;
        mHandler.animatorD = 0.18f;
        Message msg = new Message();
        msg.obj=win;
        msg.what=76998;
        mHandler.sendMessageDelayed(msg, delay);
    }

	public void OpenNewFiles(String[] files) {
		CheckProject();
		if(files.length==0) return;
		boolean samefile = mMediaPlayerCompat.currentFile.getAbsolutePath().equals(files[0]);
		if(files.length==1 && !samefile)mMediaPlayerCompat.mPlaylist=null;
		if(!samefile){
			mMediaPlayerCompat.PreSwitch();
			if(ispauseExpected){
				ispauseExpected=false;
				mPlay.setImageResource(R.drawable.ic_pause_black_24dp);
			}
			mMediaPlayerCompat.playMediaAtPath(files[0]);
		}
		//if(mMediaPlayerCompat.mPlaylist!=null)
		//    adapter_idx = ArrayListTree.binarySearch(mMediaPlayerCompat.mPlaylist, mMediaPlayerCompat.currentFile, filecomparator);
		if(files.length>1){mMediaPlayerCompat.mPlaylist=new ArrayList<>(files.length);
			for(String sI:files)mMediaPlayerCompat.mPlaylist.add(new CachedFile(sI));}
		if(!samefile)adapter_idx=-1;
		mDrawerLayout.closeDrawer(GravityCompat.START);
		fix_full_screen(null);
	}

	class TimetaskHolder extends TimerTask{
        int message;
        TimetaskHolder(int _message){
            message=_message;
        }
        @Override
        public void run() {
            mHandler.sendEmptyMessage(message);
        }
    }

    private boolean bNautyScroll;
    private
    static class MyHandler extends Handler{
        //HashSet<Integer> invalidatepool = new HashSet(1);
        private final WeakReference<Toastable_Activity> activity;
        float animator = 0.1f;
        float animatorD = 0.15f;
        float animator2 = 0.1f;
        float animatorD2 = 0.15f;
        MyHandler(Toastable_Activity a) {
            this.activity = new WeakReference<>(a);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            //CMN.Log("timetime",msg);
            if(activity.get()==null)
                return;
            VICMainActivity a = ((VICMainActivity)activity.get());
            //if(invalidatepool.contains(msg.what)) return;
            switch (msg.what) {
                case 123456:
                    if(!a.isRudeTLEnabled) break;
                    long timetime = (long) msg.obj;
                    // CMN.Log("timetime",timetime,a.tracksyncstarttime,timetime>=a.tracksyncstarttime);
                    if(timetime>=a.tracksyncstarttime) {
                        //if(!a.isValidationExpected) {
                            if (a.mMediaPlayerCompat.currentFile.getAbsolutePath().equals(a.PreparedStamp))
                                a.onTimeChanged(a.TimeExpection = a.mMediaPlayerCompat.getTime());
                        //}
                        Message msgmsg = new Message();
                        msgmsg.what=123456;
                        msgmsg.obj = timetime;
                        sendMessageDelayed(msgmsg, 250);
                    }
                break;
                case 80:
                    a.refreshSVLayout(-1);
                break;
                case 115:
                    animator2+=animatorD2;
                    if(animator2>=0.9) {
                        if(a.volumeseekbar.getVisibility()==View.VISIBLE)a.volumeseekbar.setVisibility(View.GONE);
                        a.toptopT.setVisibility(View.GONE);
                    }else{
                        float alpha = 1-animator2;
                        if(a.volumeseekbar.getVisibility()==View.VISIBLE)a.volumeseekbar.setAlpha(alpha);
                        a.toptopT.setAlpha(alpha);
                        sendEmptyMessage(115);
                    }
                break;
                case 879://for click on view frame to show ui
                    removeMessages(879);
                    if(!a.bIsUIHidden) {
                        animator+=animatorD;
                        if(animator>=0.9) {
                            a.bottombar_setBackgroundColor(a.opt.BGB);
                            if(!(a.opt.isInLearningMode()))
                                a.toolbar_setBackgroundColor(a.opt.BGB2);
                        }else{
                            int filteredColor = ColorUtils.blendARGB(Color.TRANSPARENT,a.opt.BGB, animator);
                            a.bottombar_setBackgroundColor(filteredColor);
                            if(!(a.opt.isInLearningMode())){
								int filteredColor2 = ColorUtils.blendARGB(Color.TRANSPARENT,a.opt.BGB2, animator);
								a.toolbar_setBackgroundColor(filteredColor2);
							}
                            sendEmptyMessage(879);
                        }
                    }
                break;
                case 779://for click on view frame to hide ui
                    removeMessages(779);
                    if(a.bIsUIHidden){
                        animator+=animatorD;
                        if(animator>=1) {
                            a.bottombar.setVisibility(View.GONE);
                            a.toolbar.setVisibility(View.GONE);
                        }else{
                            float alpha = 1-animator;
                            a.bottombar.setAlpha(alpha);
                            a.toolbar.setAlpha(alpha);
                            //sendEmptyMessageDelayed(779,100);
                            sendEmptyMessage(779);
                        }
                    }
                break;
                case 9:
                    a.findViewById(R.id.wiget2).performClick();
                    if(a.bNautyScroll){
                        sendEmptyMessageDelayed(9,500);
                    }
                break;
                case 2:
                    if(a.viewList.size()>0){
                        PageHolder holder = a.viewList.get(a.viewPager.getCurrentItem());
                        if(holder.tv.bNeedInvalidate){///检测选择
                            holder.tv.clearSelection();
                        }
                    }
                break;
                case 10086002:
                    //CMN.Log(a.mMediaPlayer.getPosition(), a.mMediaPlayer.getTime(), a.lastMediaPostion);
                    //mMediaPlayer.setTime(mMediaPlayer.getTime());
                    //mMediaPlayer.setTime((long) (mMediaPlayer.getLength() * lastMediaPostion));

                        a.mMediaPlayerCompat.setPosition(a.lastMediaPostion);
                        if (!a.ispauseExpected) {
                            a.mMediaPlayerCompat.play();
                        }
                    //CMN.Log(111,a.mMediaPlayer.getPosition(), a.mMediaPlayer.getTime(), a.lastMediaPostion);
                    //mMediaPlayer.getVLCVout().attachViews(VICMainActivity.this);
                break;
                case 15576:
                    a.AmendEq();
                break;
                case 666:
                    if(System.currentTimeMillis()>=a.animation_start_stamp){
                        a.handle_animation();
                        if(a.anim!=null)
                            sendEmptyMessage(666);
                    }
                break;
                case 76998:
                    Window win = (Window) msg.obj;
                    msg.obj=null;
                    if(!a.bIsUIHidden) {
                        animator+=animatorD;
                        if(animator>=0.9) {
                            a.globalmask.setAlpha(1);
                            win.setDimAmount(1);
                        }else{
                            a.globalmask.setAlpha(animator);
                            win.setDimAmount((float) Math.max(0.2,animator));
                            msg = new Message();
                            msg.obj=win;
                            msg.what=76998;
                            sendMessage(msg);
                        }
                    }
                break;
            }
        }
    }

    final MyHandler mHandler =  new MyHandler(this);





    class PageHolder{
        PageHolder(int id,ViewGroup parent){
            textlet = LayoutInflater.from(getApplicationContext()).inflate(id, parent, false);
            tv = textlet.findViewById(R.id.text1);
			tv.setTextSize(VICMainAppOptions.isLarge ?40:20);
            textlet.setTag(this);

            ScrollViewHolder svmy = textlet.findViewById(R.id.sv);
            SelectableTextViewCover textCover = textlet.findViewById(R.id.cover);
            SelectableTextViewBackGround textCover2 = textlet.findViewById(R.id.cover2);
            tv.instantiate(textCover, textCover2, svmy, null);
            textlet.setBackgroundColor(0xFFC7EDCC);
            //tvmy.setTheme(0xFF000000,0xffffffff,0xa82b43e1);
            tv.setTheme(0xFFC7EDCC, Color.BLACK, 0x883b53f1);
            tv.setTextViewListener(selectableTextView -> {
                TimetaskHolder tk = new TimetaskHolder(2);
                timer.schedule(tk, 110);
            });

        }
        int lastHighLightStart,lastHighLightEnd;
        long nxtTime=0l,prevTime=0l;
        /** time, text-offset*/
        ParralelListTree<Long,Integer> subs_timeNodeTree;//时间-文本偏移
        SpannableString baseSpan;
        View textlet;
        SelectableTextView tv;
    }



    private void inflateLyrics(ArrayList<File> lyricsChain) {
        //CMN.Log("inflateLyrics",viewList.size());
        if(textMain!=null) {
            if(textMain.getTag() == mMediaPlayerCompat.currentFile.getPath()) return;
        }
        if(textMain==null){
            textMain = (SplitView) LayoutInflater.from(getApplicationContext()).inflate(R.layout.textbook_layout, main, false);
            viewPager = textMain.findViewById(R.id.viewpager);
            pagerAdapter = new PagerAdapter() {
                @Override public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
                    return arg0 == arg1;
                }
                @Override public int getCount() {
                    return viewList.size();
                }
                @Override public void destroyItem(@NonNull ViewGroup container, int position,
                                        @NonNull Object object) {
                    //CMN.Log("destroyItem", object);
                    container.removeView((View) object);
                }
                @Override public int getItemPosition(@NonNull Object object) {
                    return super.getItemPosition(object);
                }
                @NonNull @Override
                public Object instantiateItem(@NonNull ViewGroup container, int position) {
                    container.addView(viewList.get(position).textlet);
                    return viewList.get(position).textlet;
                }
            };
            mPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //CMN.Log("onPageSelected",position,viewPager.getCurrentItem());
                    PageHolder holder = viewList.get(position);
                    ((ViewPagerHolder) viewPager).tv2guard = holder.tv;
                    //holder.prevTime=holder.nxtTime=0;
                    refreshTextViewHighLight(mMediaPlayerCompat.getTime());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            };
            viewPager.addOnPageChangeListener(mPageChangeListener);
            viewPager.setAdapter(pagerAdapter);
            textMain.findViewById(R.id.browser_widget7).setOnClickListener(this);
            textMain.findViewById(R.id.browser_widget10).setOnClickListener(this);
            textMain.findViewById(R.id.browser_widget11).setOnClickListener(this);
            textMain.findViewById(R.id.browser_widget11).setOnLongClickListener(this);
            textMain.findViewById(R.id.browser_widget12).setOnClickListener(this);
            CachedBBSize=500;
            CachedBBSize=(int)Math.max(20*dm.density, Math.min(CachedBBSize, 50*dm.density));
            textMain.setPrimaryContentSize(CachedBBSize,true);
            textMain.multiplier=1;
            textMain.isSlik=true;
            textMain.setPageSliderInf(new SplitView.PageSliderInf() {//这是底栏的动画特效
                int height;
                @Override public void onPreparePage(int val) {

                }
                @Override public void onMoving(SplitView webcontentlist,float val) {
                }
                @Override public void onPageTurn(SplitView webcontentlist) {
                }
                @Override public void onHesitate() {
                }
                @Override public void SizeChanged(int newSize, float delta) {}
                @Override public void onDrop(int size) {
                }
                @Override
                public int preResizing(int size) {
                    int ret = (int) Math.max(20*dm.density, Math.min(50*dm.density, size));
                    return ret;
                }
            });
            main.addView(textMain);
			CMN.Log("为文字视图关闭硬件加速",viewPager.getLayerType(),View.LAYER_TYPE_NONE);
			//viewPager.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) textMain.getLayoutParams();
            lp.gravity = Gravity.BOTTOM;
        }
        recycler_bin.ensureCapacity(recycler_bin.size()+viewList.size());
        recycler_bin.addAll(0, viewList);
        viewList.clear();
        viewList.ensureCapacity(lyricsChain.size());
        for(File sfI:lyricsChain){
            PageHolder holder = recycler_bin.size()>0?recycler_bin.remove(0):new PageHolder(R.layout.textbook_page_item, main);
            viewList.add(holder);
            holder.tv.clearSelection();
            loadLyrics(sfI, holder, lyrics_projects);
        }
        for(PageHolder phI:recycler_bin){
            if(phI.textlet.getParent()!=null)((ViewGroup)phI.textlet.getParent()).removeView(phI.textlet);
        }
        viewPager.setAdapter(pagerAdapter);//不容易啊
        viewPager.setCurrentItem(0, false);
        if(viewList.size()>0){
            PageHolder holder = viewList.get(0);
            ((ViewPagerHolder) viewPager).tv2guard = holder.tv;
        }
        textMain.setTag(mMediaPlayerCompat.currentFile.getPath());

        //textMain.setVisibility(View.INVISIBLE);
    }

    private void loadLyrics(File subscript_file, PageHolder holder, HashMap<String, JSONArray> lyrics_projects) {
        JSONArray annotations = lyrics_projects.get(subscript_file.getAbsolutePath());

        String subname = subscript_file.getName();
        String houzhui = subname.substring(subname.lastIndexOf(".")+1);

        StringBuffer sb;
        //读取txt,设置textView内容
        sb = new StringBuffer();
        BufferedReader br;
        int initialCap = 0;
        if(annotations!=null){
            initialCap = annotations.getInteger(2);
        }
        //todo enssurecap
        holder.subs_timeNodeTree = new ParralelListTree<>(initialCap);
        try {
            String line;
            //自动检测编码
            String charset;
            //chatsetDec cd = new chatsetDec();
            //Toast.makeText(this,cd.guessFileEncoding(subscript_file),Toast.LENGTH_SHORT).show();
            //charset = isPlayingAsset?"utf8":cd.guessFileEncoding(subscript_file).split(",")[0];
            //if(charset.startsWith("windows"))
            //    charset = "UTF-16LE";

            byte[] buf = new byte[4096];
            java.io.FileInputStream fis = new java.io.FileInputStream(subscript_file);
            UniversalDetector detector = new UniversalDetector(null);
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            charset = detector.getDetectedCharset();
            CMN.Log("charset: ",charset,subscript_file.getName());

            if(charset==null)charset="utf8";
            //todo todo
            br = new BufferedReader(new InputStreamReader(isPlayingAsset?getAssets().open(subscript_file.getPath().substring(7)):new FileInputStream(subscript_file),charset));
            //歌曲字幕格式
            if(houzhui.equals("lrc")) {
                long time = -1;
                boolean isSongLrc = false;//用于操控lrc文件显示格式
                StringBuilder sb2 = new StringBuilder();
                sb2.append(subname).append("\n\n");
                sb.append(subname).append("\n\n");
                holder.subs_timeNodeTree.insert(0l, sb.length());
                while ((line = br.readLine()) != null) {
                    int offa = line.indexOf("[");
                    int offb = line.indexOf("]");
                    boolean isLyric = true;//行格式检查
                    if(offa!=0||offb<0)//此行无[]标记,要么是翻译歌词,否则不予理会。
                    {
                        if(time!=-1){//是翻译歌词
                            if(!isSongLrc)
                                sb2.append(" ").append(line);
                            else
                                sb2.append("\r\n  ").append(line);
                        }
                        continue;
                    }
                    String boli = line.substring(offa+1,offb);
                    String[] duco = boli.split("[: .]");
                    //格式检查
                    if(duco.length!=3)
                        isLyric=false;
                    else{
                        if(duco[0].length()!=2 || duco[1].length()!=2) isLyric=false;
                        if(duco[2].length()==2) duco[2] = duco[2]+"0";
                        if(duco[2].length()!=3) isLyric=false;
                    }
                    if(boli.equals("is:songLrc")){
                        isSongLrc = true;
                        holder.tv.setGravity(Gravity.CENTER_HORIZONTAL);
                        continue;
                    }
                    String text = (offb+1)<line.length()?line.substring(offb+1):"";
                    if(isLyric) {//是主歌词
                        time = Integer.valueOf(duco[0])*60000+Long.valueOf(duco[1])*1000+Integer.valueOf(duco[2]);
                        holder.subs_timeNodeTree.insert(time, sb.length());
                    }else{//只是歌曲信息
                        text = "  "+boli+"\n\n";
                        holder.subs_timeNodeTree.insert(time, sb.length());
                    }
                    if(!isSongLrc)
                        sb.append(" ").append(text);
                    else
                        sb.append("\r\n").append(text).append("\r\n");
                }
            }else {
                //默认-srt电影字幕格式
                //读取首部
                //br.readLine();
                while ((line = br.readLine())!=null){
                    if(line.trim().replaceAll(new String(new byte[] {(byte) 0xef,(byte) 0xbb,(byte) 0xbf}, StandardCharsets.UTF_8), "").equals("1"))
                        break;
                    else
                        sb.append(line).append("\n");
                    //CMN.Log("寻首部",line.trim().replaceAll(new String(new byte[] {(byte) 0xef,(byte) 0xbb,(byte) 0xbf}, StandardCharsets.UTF_8), ""));
                }
                //读取时间码
                line = br.readLine();
                //CMN.Log(line);
                if (line != null) {
                    holder.subs_timeNodeTree.insert(Integer.valueOf(line.substring(9, 12)) + Integer.valueOf(line.substring(6, 8)) * 1000l + Integer.valueOf(line.substring(3, 5)) * 60000l + Integer.valueOf(line.substring(0, 2)) * 3600000l, sb.length());
                }

                while ((line = br.readLine()) != null) {
                    OUT:
                    if (line.equals("")) {//以空行为间隔
                        String lineMark = br.readLine();//行标记
                        line = br.readLine();//下一个时间码
                        if (line != null) {
                            try {
                                holder.subs_timeNodeTree.insert(Integer.valueOf(line.substring(9, 12)) + Integer.valueOf(line.substring(6, 8)) * 1000l + Integer.valueOf(line.substring(3, 5)) * 60000l + Integer.valueOf(line.substring(0, 2)) * 3600000l, sb.length());
                            } catch (Exception e) {
                                line = lineMark+"\n"+lineMark;
                                break OUT;
                            }
                        }
                        continue;
                    }
                    sb.append(" ").append(line);
                }
            }
            //CMN.Log("lyrics load done!...",holder.subs_timeNodeTree.data.size(), holder.subs_timeNodeTree.value.size());
            br.close();
            if(isPlayingAsset){
                InputStream in = getAssets().open("infocmode.txt");
                byte[] buffer = new byte[in.available()];
                int len = in.read(buffer);
                in.close();
                sb.append("\n").append(new String(buffer,0,len));
            }



            holder.baseSpan = new SpannableString(sb.toString()+"\r\n\r\n\r\n\n");
//            prevTimeNode = subs_timeNodeTree.getRoot();
//            nxtTimeNode = subs_timeNodeTree.sxing(prevTimeNode.key);
//            if(shouldCreate2ndtv){
//                prevTimeNode2 = subs_timeNodeTree3.getRoot();
//                nxtTimeNode2 = subs_timeNodeTree3.sxing(prevTimeNode2.key);
//            }
//
//            scanFavorite();
//            scanFavorite2();
            //str.setSpan(new BackgroundColorSpan(Color.YELLOW), prevTimeNode.getKey().value, nxtTimeNode.getKey().value, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);





        } catch (Exception e) {
            CMN.Log(e);
        }

        if(annotations!=null){
            for (int i = 3; i+3 < annotations.size(); i+=4) {
                ColoredAnnotationSpan annI = new ColoredAnnotationSpan(annotations.getInteger(i+3));
                annI.type = annotations.getInteger(i+2);
                holder.baseSpan.setSpan(annI, annotations.getInteger(i),annotations.getInteger(i+1), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
            lyrics_projects.remove(subscript_file.getAbsolutePath());
        }

        holder.tv.setText(holder.baseSpan, TextView.BufferType.SPANNABLE);//TODO cash
        holder.baseSpan = (SpannableString) holder.tv.getText();
    }

    private void decorateTitle() {
        String filename = mMediaPlayerCompat.currentFile.getName();
        String upper0 = String.valueOf(filename.charAt(0)).toUpperCase();
        int idx = filename.lastIndexOf(".");
        if(idx>0)
            filename = upper0 + filename.substring(1,idx);
        title.setText(filename);
    }


    public void decorateFooter(View footchechers) {
        View.OnClickListener clicker = v -> {
            int id = v.getId();
            switch (id) {
                case R.id.check1:
                    CircleCheckBox ck = (CircleCheckBox) v;
                    ck.toggle(false);
                    opt.setInfoShowMore(ck.isChecked());
                break;
                case R.id.check2:
                    ck = (CircleCheckBox) v;
                    ck.toggle(false);
                    opt.setInfoPinned(ck.isChecked());
                break;
                case R.id.wiget1:
                    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(Uri.fromFile(mMediaPlayerCompat.currentFile), "video/*")
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        );
                    } catch (Exception e) {
                        showT(R.string.no_suitable_app);
                    }
                break;
                case R.id.wiget2:
                    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW)
                                .setDataAndType(Uri.fromFile(mMediaPlayerCompat.currentFile.getParentFile()), "resource/folder")
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                );
                    } catch (Exception e) {
                        showT(R.string.no_suitable_app);
                    }
                break;
            }
        };
        footchechers.getBackground().setColorFilter(new ColorMatrixColorFilter(Toastable_Activity.NEGATIVE));
        footchechers.getBackground().setAlpha(175);
        CircleCheckBox ck = footchechers.findViewById(R.id.check1);
        ck.drawInnerForEmptyState = true;
        if(opt.getInfoShowMore()) ck.setChecked(true,false);
        ck.setOnClickListener(clicker);
        ck = footchechers.findViewById(R.id.check2);
        ck.drawInnerForEmptyState = true;
        if(opt.getInfoPinned()) ck.setChecked(true,false);
        ck.setOnClickListener(clicker);
        footchechers.findViewById(R.id.wiget1).setOnClickListener(clicker);
        footchechers.findViewById(R.id.wiget2).setOnClickListener(clicker);
    }

    GestureDetector mGestureDetector;

    boolean rotationEnabled=false;
    boolean panEnabled=true;
    boolean zoomEnabled=true;
    // Current scale and scale at startof zoom
    public float scale;
    private float scaleStart;
    private float zoomInStart;
    public float maxScale=10;

    private boolean isZooming;
    private boolean isPanning;

    // Rotation parameters
    private float rotation = 0;
    private float lastAngle;
    private PointF vCenterStart=new PointF();
    private PointF sCenterStart=new PointF();
    private PointF vCenterStartNow=new PointF();
    private float vDistStart;
    // Stored to avoid unnecessary calculation
    public double cos = Math.cos(0);
    public double sin = Math.sin(0);
    // Screen coordinate of top-left corner of source image
    public PointF vTranslate = new PointF();
    public PointF vTranslateOrg = new PointF();
    private PointF vTranslateStart = new PointF();
    private PointF vTranslateBefore = new PointF();
    // Source image dimensions and orientation - dimensions relate to the unrotated image
    public int sWidth;
    public int sHeight;

    float lastX;
    float lastY;
    float orgX;
    float orgY;
    int orgEdge;
    int first_touch_id;
    boolean isDoubleTapDetected=false;
    boolean isFocusTaken=false;
    boolean useConfirmedClick=true;
    HashSet<Integer> touch_partisheet = new HashSet<>();
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(v.getId()==R.id.video_surface_frame){
            int touchCount = event.getPointerCount();
            int touch_type = event.getAction() & MotionEvent.ACTION_MASK;
            int actual_index = event.getActionIndex();
            int touch_id = event.getPointerId(actual_index);
            switch(touch_type) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:{//qidian
                    clickconmsumed=false;
                    touch_partisheet.remove(touch_id);
                    if(touch_id==first_touch_id) {
                        if (toptopT.getVisibility() == View.VISIBLE) {
                            if (!doubleClickDeteced) toptopT.setVisibility(View.GONE);
                        }
                        if (isScrollSeeked && !opt.isScrollSeekImmediate()) {
                            mMediaPlayerCompat.setTime(mSeekBar.getProgress());
                        }
                        if (bIsUIHidden) {
                            if(!isReallyTracking)
                            if (bottombar.getVisibility() == View.VISIBLE) {
                                bottombar.setVisibility(View.GONE);
								bottombar_setBackgroundColor(opt.BGB);
                            }
                            if (toolbar.getVisibility() == View.VISIBLE) {
                                toolbar.setVisibility(View.GONE);
                            }
                        }
                        isTracking = false;
                        if (VerticalScrolltriggered)//naScrollaco!=0
                            isFocusTaken = true;
                        if (Math.abs(naScrollaco) > SWITCHTHREOLD) {//上下切换
                            if(opt.getVibrationEnabled()){
                                Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                                if(vibrator!=null) {
                                    long[] pattern = {zhengDongLevels[zhengDongLevelsIndex] / 10, zhengDongLevels[zhengDongLevelsIndex], zhengDongLevels[zhengDongLevelsIndex] / 10, zhengDongLevels[zhengDongLevelsIndex]};   // 停止 开启 停止 开启
                                    vibrator.cancel();
                                    vibrator.vibrate(pattern, -1);
                                }
                            }

                            Drawable d = getResources().getDrawable(naScrollaco > 0 ? R.drawable.ic_skip_previous_black_24dp : R.drawable.ic_skip_next_black_24dp);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                                d.setTint(Color.WHITE);
                            int w = (int) (50 * opt.dm.density);
                            d.setBounds(0, 0, w, w);
                            boolean isShown=false;
                            if(mMediaPlayerCompat.mPlaylist==null || naScrollaco > 0 && adapter_idx>0 || naScrollaco < 0 && adapter_idx<mMediaPlayerCompat.mPlaylist.size()-1) {
                                toptopT.setText(null);
                                toptopT.setCompoundDrawables(d, null, null, null);
                                toptopT.setVisibility(View.VISIBLE);
                                toptopT.setAlpha(1.f);
                                toptopT.setTag(false);
                                isShown=true;
                            }

                            if (naScrollaco > 0) {
                                mLast.performClick();
                            } else {
                                mNext.performClick();
                            }

                            if(isShown)
                                mHandler.sendEmptyMessageDelayed(115, 200);
                            naScrollaco = 0;
                        }
                        main_progress_bar.setVisibility(View.GONE);
                        first_touch_id=-1;
                        naScrollaco=0;
                        singlaton_task_assigned=
                        VerticalScrolltriggered=
                        HorizontalScrolltriggered=
                        HorizontalScrolltriggeredSlite=
                        isScrollTweaked=
                        isScrollSeeked=false;
                    }
                    //if(false)//回弹
                    OUT:
                    if(getVideoView()!=null){
                        if(anim!=null && anim.origin==ORIGIN_DOUBLE_TAP_ZOOM){
                            break OUT;
                        }
                        vTranslate.set(getVideoView().getTranslationX(), getVideoView().getTranslationY());
                        if(!(vTranslate.x==pendingTransX&&vTranslate.y==pendingTransY&&scale==pendingScale)) {
                            float currentW = scale * sWidth;
                            float currentH = scale * sHeight;
                            float scX = getCenter().x, scY = getCenter().y;
                            float viewscale = scale / pendingScale;
                            int rebounceReason=-1;
                            boolean isBoundInvalid = getVideoView().getTranslationX()>getScreenWidth() || getVideoView().getTranslationX()+pendingWidth*viewscale<0
                                    || getVideoView().getTranslationY()>getScreenHeight() || getVideoView().getTranslationY()+pendingHeight*viewscale<0;
                            boolean isShrinked = scale<pendingScale;
                            if(isBoundInvalid) rebounceReason=0;
                            else if(isShrinked && opt.getRebounceScheme()<=1){
                                if(opt.getRebounceRespectMatchType()){//
                                    if(pendingMatchType==Match_Width){//check —
                                        rebounceReason=5;
                                    }else if(pendingMatchType==Match_Height){//check |
                                        rebounceReason=6;
                                    }
                                }else
                                    rebounceReason=1;
                            }
                            if(rebounceReason<0){//let's find more reasons!
                                boolean isBoundValid = getVideoView().getTranslationX()<=0 && getVideoView().getTranslationX()+pendingWidth*viewscale>getScreenWidth()
                                        && getVideoView().getTranslationY()<=0 && getVideoView().getTranslationY()+pendingHeight*viewscale>getScreenHeight();
                                //CMN.Log("isBoundValid", isBoundValid);
                                if(!isBoundValid){
                                    if(opt.getRebounceScheme()==0){//This is the 2nd most strict mode!
                                        if(scX!=sWidth/2||scY!=sHeight/2){//check is our center deviated.
                                            if(opt.getRebounceRespectMatchType()){//This make it not that strict.
                                                if(pendingMatchType==Match_Width){//check —
                                                    if(getVideoView().getTranslationX()>0||getVideoView().getTranslationX()+pendingWidth*viewscale<getScreenWidth())
                                                        rebounceReason=3;
                                                }else if(pendingMatchType==Match_Height){//check |
                                                    if(getVideoView().getTranslationY()>0||getVideoView().getTranslationY()+pendingHeight*viewscale<getScreenHeight())
                                                        rebounceReason=4;
                                                }
                                            }else
                                                rebounceReason=2;
                                        }
                                    }
                                }
                            }
                            //CMN.Log("recouncereason", rebounceReason);
                            if(rebounceReason>=0){
                                if(rebounceReason!=4)
                                if(rebounceReason==6){
                                    scX=sWidth/2+(scX-sWidth/2)*viewscale;
                                    boolean b1=getVideoView().getTranslationX()>0,b2=getVideoView().getTranslationX()+sWidth*scale<getScreenWidth();
                                    if(b1 ^ b2 || sHeight*pendingScale<=getScreenHeight()){
                                        if(b1)
                                            scX=Math.min(getScreenWidth()/(2*pendingScale), scX);//防左溢出
                                        if(b2)
                                            scX=Math.max((sWidth-getScreenWidth()/(2*pendingScale)), scX);//防右溢出
                                    }
                                }else
                                if (currentW <= getScreenWidth()) {//小靠近中间
                                    scX = sWidth/2;
                                } else {//大，且出格，往最近的边缘靠
                                    if (getVideoView().getTranslationX() > 0) {
                                        scX = getScreenWidth() / scale / 2;
                                    } else if (getVideoView().getTranslationX() + currentW < getScreenWidth()) {
                                        scX = (currentW - getScreenWidth() * 1.0f / 2) / scale;
                                    }
                                }

                                if(rebounceReason!=3)
                                if(rebounceReason==5){
                                    scY=sHeight/2+(scY-sHeight/2)*viewscale;
                                    boolean b1=getVideoView().getTranslationY()>0,b2=getVideoView().getTranslationY()+sHeight*scale<getScreenHeight();
                                    if(b1 ^ b2 || sWidth*pendingScale<=getScreenWidth()) {
                                        if (b1)
                                            scY = Math.min(getScreenHeight() / (2 * pendingScale), scY);//防上溢出
                                        if (b2)
                                            scY = Math.max((sHeight - getScreenHeight() / (2 * pendingScale)), scY);//防下溢出
                                    }
                                }else
                                if (currentH <= getScreenHeight()) {//小靠近中间
                                    scY = sHeight / 2;
                                } else {//大，且出格，往最近的边缘靠
                                    if (getVideoView().getTranslationY() > 0) {
                                        scY = getScreenHeight() / scale / 2;
                                    } else if (getVideoView().getTranslationY() + currentH < getScreenHeight()) {
                                        scY = (currentH - getScreenHeight() * 1.0f / 2) / scale;
                                    }
                                }
                                new AnimationBuilder(overshrinked() ? pendingScale : scale, new PointF(scX, scY))
                                        .withEasing(EASE_IN_OUT_QUAD).withPanLimited(false).withOrigin(ORIGIN_ANIM)
                                        .withDuration(250)
                                        .startani();
                            }
                        }
                    }
                    if(isFocusTaken || isScrollSeeked || isScrollTweaked)
                        return true;
                    if(isDoubleTapDetected) handleDoubleClick(event);
                    break;
                }
                case MotionEvent.ACTION_DOWN:{
                    //CMN.Log("ACTION_DOWN");
                    if(getVideoView()!=null)vTranslate.set(getVideoView().getTranslationX(), getVideoView().getTranslationY());
                    first_touch_id=touch_id;
                    touch_partisheet.clear();
                    vCenterStart.set((event.getX(0) + event.getX(0))/2, (event.getY(0) + event.getY(0))/2);
                    naScrollaco=0;
                    isDoubleTapDetected=
                    singlaton_task_assigned=
                    dual_task_assigned=
                    VerticalScrolltriggered=
                    HorizontalScrolltriggered=
                    HorizontalScrolltriggeredSlite=
                    isScrollTweaked=
                    isScrollSeeked=
                    doubleClickDeteced=false;
                    isFocusTaken=false;
                    orgX = lastX = event.getX(0);
                    orgY = lastY = event.getY(0);
                    orgEdge = event.getEdgeFlags();
                    //CMN.Log("orgEdge", orgEdge&MotionEvent.EDGE_TOP, orgEdge&MotionEvent.EDGE_LEFT);
                }
                case MotionEvent.ACTION_POINTER_DOWN:{
                    if(first_touch_id==-1){
                        first_touch_id=touch_id;
                        orgX = lastX = event.getX(0);
                        orgY = lastY = event.getY(0);
                    }
                    //for (int i = 0; i < touchCount; i++) {
                    //    CMN.Log("touch iter",i, "=",  event.getPointerId(i), first_touch_id);
                    //}
                    if(touch_partisheet.size()==0 && touch_type==MotionEvent.ACTION_POINTER_DOWN) {
                        break;
                    }

                    int touch_seat_count = 2-touch_partisheet.size();
                    for(int i=0;i<Math.min(touch_seat_count, touchCount);i++) {
                        if(touch_partisheet.contains(event.getPointerId(i))) {
                            touch_seat_count++;
                        }else
                            touch_partisheet.add(event.getPointerId(i));
                    }

                    if(touchCount>=2){
                        float distance = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                        scaleStart = scale;
                        zoomInStart = mMediaPlayerCompat.getPanoramaZoomInStart();
                        vDistStart = distance;
                        vTranslateStart.set(vTranslate.x, vTranslate.y);
                        vCenterStart.set((event.getX(0) + event.getX(1))/2, (event.getY(0) + event.getY(1))/2);
                        viewToSourceCoord(vCenterStart, sCenterStart);

                        if (rotationEnabled) {
                            lastAngle = (float) Math.atan2((event.getY(0) - event.getY(1)), (event.getX(0) - event.getX(1)));
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE:{
                    boolean scaled = getVideoView() == null || getVideoView().getScaleX() != 1;
                    if(!touch_partisheet.contains(touch_id))
                        return true;
                    if(mDrawerLayout.isDrawerVisible(GravityCompat.START))
                        return false;

                    if(!opt.getVoiceOnly())
                    if(opt.getPanoramaMode()){
                        float dy = event.getY(actual_index) - lastY;
                        float dx = event.getX(actual_index) - lastX;
                        lastX = event.getX(actual_index);
                        lastY = event.getY(actual_index);

                        if(touchCount>=2){
                            if (touch_partisheet.size() >= 2) {
                                dual_task_assigned = true;
                                // Calculate new distance between touch points, to scale and pan relative to startvalues.
                                float vDistEnd = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                                float vCenterEndX = (event.getX(0) + event.getX(1))/2;
                                float vCenterEndY = (event.getY(0) + event.getY(1))/2;
                                if (distance(vCenterStart.x, vCenterEndX, vCenterStart.y, vCenterEndY) > 5
                                        || Math.abs(vDistEnd - vDistStart) > 5) {
                                    mMediaPlayerCompat.ZoomPanorama(vDistEnd - vDistStart, zoomInStart);
                                }
                            }
                        }


                        if(!dual_task_assigned && touch_id==first_touch_id){
                            mMediaPlayerCompat.PanPannoramaXY(dx, dy);
                            if(!singlaton_task_assigned)
                            if (Math.abs(lastY - orgY) > SCALEDSLOP || Math.abs(lastX - orgX) > SCALEDSLOP)
                                singlaton_task_assigned=true;
                        }


                        refreshPanorama(false); //好玩
                        break;
                    }

                    if(getVideoView()!=null && pendingScale!=0)
                    OUT:
                    if(!singlaton_task_assigned){
                        boolean consumed = false;
                        if (touch_partisheet.size() >= 2) {
                            if(anim!=null){
                                if(anim.interruptible) anim=null;
                                else break OUT;
                            }
                            dual_task_assigned = true;
                            // Calculate new distance between touch points, to scale and pan relative to startvalues.
                            float vDistEnd = distance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
                            float vCenterEndX = (event.getX(0) + event.getX(1))/2;
                            float vCenterEndY = (event.getY(0) + event.getY(1))/2;

                            if (rotationEnabled) {
                                float angle = (float) Math.atan2((event.getY(0) - event.getY(1)), (event.getX(0) - event.getX(1)));
                                setRotationInternal(rotation + angle - lastAngle);
                                lastAngle = angle;
                                //consumed = true;
                            }

                            if (distance(vCenterStart.x, vCenterEndX, vCenterStart.y, vCenterEndY) > 5 || Math.abs(vDistEnd - vDistStart) > 5 || isPanning) {
                                isZooming = true;
                                isPanning = true;
                                consumed = true;

                                double previousScale = scale;
                                if (zoomEnabled) {
                                    scale = Math.min(maxScale, (vDistEnd / vDistStart) * scaleStart);
                                }
                                //android.util.Log.e("fatal","scale"+scale);

                                if (scale <= minScale()) {
                                    if(false){
                                        // Minimum scale reached so don't pan. Adjust startsettings so any expand will zoom in.
                                        vDistStart = vDistEnd;
                                        scaleStart = minScale();
                                        vCenterStart.set(vCenterEndX, vCenterEndY);
                                        vTranslateStart.set(vTranslate);
                                    }
                                }
                                //else
                                if (panEnabled) {
                                    // Translate to place the source image coordinate that was at the center of the pinch at the start
                                    // at the center of the pinch now, to give simultaneous pan + zoom.
                                    sourceToViewCoord(sCenterStart, vCenterStartNow);

                                    final float dx = (vCenterEndX - vCenterStartNow.x);
                                    final float dy = (vCenterEndY - vCenterStartNow.y);

                                    float dxR = (float) (dx * cos - dy * -sin);
                                    float dyR = (float) (dx * -sin + dy * cos);

                                    vTranslate.x += dxR;
                                    vTranslate.y += dyR;

                                    // TODO: Account for rotation
                                    boolean b1 = true || scale * sHeight >= dm.heightPixels;
                                    boolean b2 = true || scale * sWidth >= dm.widthPixels;
                                    boolean b3 = true || previousScale * sHeight < dm.heightPixels;
                                    boolean b4 = true || previousScale * sWidth < dm.widthPixels;
                                    if (true) {//(b3 && b1) || (b4 && b2)
                                        //fitToBounds(true,true);
                                        vCenterStart.set(vCenterEndX, vCenterEndY);
                                        vTranslateStart.set(vTranslate);
                                        scaleStart = scale;
                                        vDistStart = vDistEnd;
                                    }
                                }
                            }
							if(pendingScale!=0){
								boolean apply_quickly = true;
								if(apply_quickly){
									getVideoView().setPivotX(0);
									getVideoView().setPivotY(0);
									getVideoView().setScaleX(scale / pendingScale);
									getVideoView().setScaleY(scale / pendingScale);
									getVideoView().setTranslationX(vTranslate.x);
									getVideoView().setTranslationY(vTranslate.y);
								}else {
									//if(rotation!=rotationStamp)
									getVideoView().setRotation((float) (rotation / Math.PI * 180));
									//if(scale!=scaleStamp){
									getVideoView().setScaleX(scale / minScale());
									getVideoView().setScaleY(scale / minScale());
									//}
									//if(scale!=scaleStamp || rotation!=rotationStamp || translationStamp==null || !translationStamp.equals(vTranslate)){

									vTranslateOrg.x = pendingWidth / 2 - scale * sWidth / 2;
									vTranslateOrg.y = pendingHeight / 2 - scale * sHeight / 2;
									float deltaX = vTranslate.x - vTranslateOrg.x;
									float deltaY = vTranslate.y - vTranslateOrg.y;
									PointF vTranslateDelta = new PointF();
									vTranslateDelta.x = (float) (deltaX * cos + deltaY * -sin - deltaX);  // 0
									vTranslateDelta.y = (float) (deltaX * sin + deltaY * cos - deltaY);   // 0
									float targetTransX = vTranslate.x - vTranslateOrg.x + vTranslateDelta.x;
									float targetTransY = vTranslate.y - vTranslateOrg.y + vTranslateDelta.y;
									getVideoView().setTranslationX(targetTransX);
									getVideoView().setTranslationY(targetTransY);
									//}
								}
							}
                        }
                    }

                    if(touchCount>=2){
                        if(naScrollaco!=0){naScrollaco=0;}
                    }

                    //check for single finger tasks.
                    if(!dual_task_assigned && touch_id==first_touch_id) {
                        //boolean isTouchNotFromTBEdge=opt.isFullScreen()?(orgY>2*dm.density && (opt.isFullscreenHideNavigationbar()?orgY<getScreenHeight()-10*dm.density:true)):true;
                        boolean isTouchFromTBEdge=(orgEdge&MotionEvent.EDGE_TOP)!=0 || (orgEdge&MotionEvent.EDGE_BOTTOM)!=0
                                || opt.isFullScreen()?(orgY<=10*dm.density || (opt.isFullscreenHideNavigationbar()?orgY>=getScreenHeight()-10*dm.density:false)):false;
                        float targetTranslationYDelta = 0;
                        float targetTranslationXDelta = 0;
                        //CMN.Log("ACTION_MOVE", getVideoView().getHeight(),opt.dm.heightPixels);
                        int dy = (int) (event.getY(actual_index) - lastY);
                        int dx = (int) (event.getX(actual_index) - lastX);

                        if(false)//check to scroll Horizontally ( x-axis)
                        if(!scaled && getVideoView()!=null)
                        if (!VerticalScrolltriggered && !isScrollSeeked && !isScrollTweaked) {
                                if (dx != 0 && opt.getMatchtType() == Match_Height) {
                                    int w = opt.dm.widthPixels - DockerMarginL - DockerMarginR;
                                    if (getVideoView().getWidth() > w) {
                                        float targetTranslationX = Math.min(0, Math.max(getVideoView().getTranslationX() + dx, video_surface_frame.getWidth() - getVideoView().getWidth()));
                                        if ((targetTranslationXDelta = targetTranslationX - getVideoView().getTranslationY()) != 0) {
                                            getVideoView().setTranslationX(targetTranslationX);
                                            if (Math.abs(event.getX(actual_index) - orgX) > SCALEDSLOP) {
                                                HorizontalScrolltriggered = true;
                                                naScrollaco = 0;
                                            }
                                        }
                                        if (IMPageCover.getVisibility() == View.VISIBLE)
                                            IMPageCover.setTranslationX(getVideoView().getTranslationX());
                                        if (HorizontalScrolltriggered)
                                            isFocusTaken = true;
                                        HorizontalScrolltriggeredSlite = true;
                                    }
                                }
                            }
                        if (!VerticalScrolltriggered && !HorizontalScrolltriggeredSlite && !isScrollSeeked && !isScrollTweaked) {
                            if (dx != 0 && Math.abs(dx) > Math.abs(dy)
                                    && Math.abs(event.getX(actual_index) - orgX) > SCALEDSLOP / 3
                                    && Math.abs(event.getX(actual_index) - orgX) > 1.35 * Math.abs(event.getY(actual_index) - orgY)
                                    && !isTouchFromTBEdge) {
                                isScrollSeeked = true;
                                orgX = event.getX(actual_index);
                            } else if (false && dy != 0 && Math.abs(dx) < Math.abs(dy))
                                isScrollTweaked = true;
                        }

                        //if(false)//check to scroll Vertically ( y-axis)
                        if(!scaled && getVideoView()!=null)
                        if (!isScrollSeeked && !isScrollTweaked) {
                                if (dy != 0 && opt.getMatchtType() == Match_Width) {
                                    int h; if(opt.isFullScreen() && opt.isFullscreenHideNavigationbar())
                                    {getWindowManager().getDefaultDisplay().getRealMetrics(dm); h=dm.heightPixels;}
                                    else h = opt.dm.heightPixels - CMN.getStatusBarHeight(this); h-= DockerMarginT + DockerMarginB;
                                    int maxH = opt.isInLearningMode() ? h / 2 : h;
                                    if (getVideoView().getHeight() > maxH) {
                                        float targetTranslationY = Math.min(0, Math.max(getVideoView().getTranslationY() + dy, video_surface_frame.getHeight() - getVideoView().getHeight()));
                                        if ((targetTranslationYDelta = targetTranslationY - getVideoView().getTranslationY()) != 0)
                                            getVideoView().setTranslationY(targetTranslationY);
                                        if (IMPageCover.getVisibility() == View.VISIBLE)
                                            IMPageCover.setTranslationY(getVideoView().getTranslationY());
                                        isFocusTaken = true;
                                    }
                                }
                        }

                        if (targetTranslationYDelta != 0) {
                            naScrollaco = 0;
                        }
                        if (!isScrollSeeked && targetTranslationYDelta == 0 && !HorizontalScrolltriggered && !isTouchFromTBEdge) {
                            naScrollaco += dy;
                            if (Math.abs(event.getY(actual_index) - orgY) > SCALEDSLOP)
                                VerticalScrolltriggered = true;
                        }

                        if (Math.abs(naScrollaco) < 100) {
                            if (main_progress_bar.getVisibility() == View.VISIBLE)
                                main_progress_bar.setVisibility(View.GONE);
                        } else {
                            if (main_progress_bar.getVisibility() != View.VISIBLE)
                                main_progress_bar.setVisibility(View.VISIBLE);
                            main_progress_bar_d.setLevel((int) (Math.abs(naScrollaco) / SWITCHTHREOLD * 10000));
                            ImageView targetView = naScrollaco < 0 ? mNext : mLast;
                            if(mMediaPlayerCompat.mPlaylist==null || naScrollaco > 0 && adapter_idx>0 || naScrollaco < 0 && adapter_idx<mMediaPlayerCompat.mPlaylist.size()-1)
                                main_progress_bar.setImageDrawable(targetView.getDrawable());
                            else
                                main_progress_bar.setImageDrawable(null);
                        }

                        if (isScrollSeeked) {//左右滑动
                            //if (toptopT.getTag() != null) {
                                toptopT.setCompoundDrawables(null, null, null, null);
                                toptopT.setTextSize(40);
                                toptopT.setAlpha(1f);
                                toptopT.setTag(null);
                            //}
                            dx = (int) ((event.getX(actual_index) - orgX)*(mSeekBar.getMax()<10000?0.618f:1));
                            toptopT.setVisibility(View.VISIBLE);
                            //onSeekTo((endX - beginX) / 20);
                            isTracking = true;

                            long newT = scrollSeekStart + ((dx) * 20 * 4)*(root.getLayoutDirection()==View.LAYOUT_DIRECTION_RTL?-1:1);
                            newT = newT < 0 ? 0 : newT;
                            newT = (int) Math.min(newT, mMediaPlayerCompat.getLength());
                            int delta = (int) (newT - scrollSeekStart);
                            currenT.setText(CMN.FormTime(newT, 1));
                            toptopT.setText(new StringBuilder(  CMN.FormTime(newT, 1  )  )
                                            .append("\n")
                                            .append(  CMN.FormTime(delta, 2)  )
                            );

                            if (newT != TimeExpection) {
                                SelectableTextView.lastTouchTime=0;
                                //mMediaPlayer.setTime(newT);
                                if (mMediaPlayerCompat.getLength() != newT)
                                    TimeExpection = newT;
                                //mMediaPlayerCompat.setPosition(newT*(1.0f)/mMediaPlayerCompat.getLength());
								if(opt.isScrollSeekImmediate())
                                	mMediaPlayerCompat.setTime(newT);
                                //mMediaPlayer.setTime(newT);
                                mSeekBar.setProgress((int) newT);
                                if (bIsUIHidden) {
                                    for (int i = 0; i < bottombar.getChildCount(); i++) {
                                        if (bottombar.getChildAt(i) != seekbargp)
                                            bottombar.getChildAt(i).setVisibility(View.GONE);
                                    }
                                    bottombar.setAlpha(1);
									bottombar_setBackgroundColor(0);
                                    bottombar.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        singlaton_task_assigned = main_progress_bar.getVisibility() == View.VISIBLE || toptopT.getVisibility() == View.VISIBLE;
                        lastX = event.getX(actual_index);
                        lastY = event.getY(actual_index);
                    }

                    if(isFocusTaken)//屏蔽单机
                        return true;
                    break;
                }
            }
            mMonitorTextView.invalidate();
            mGestureDetector.onTouchEvent(event);
            return false;
        }else if(v.getId() == R.id.root){
            if(MenuInfoPopup!=null && MenuInfoPopup.getParent()!=null){
                if(!opt.getInfoPinned()) {
                    float chajia = MenuInfoPopup.getTop()-event.getY();
                    if(chajia<0) chajia = event.getY()-MenuInfoPopup.getBottom();
                    if (chajia>0) {
                        if (chajia > 6 * dm.density)
                            try_dismiss_info();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void refreshPanorama(boolean forceUpdate) {
        if(ispauseExpected || forceUpdate)
            mMediaPlayerCompat.refreshPanorama();
    }

    private boolean handleDoubleClick(MotionEvent e) {
        mHandler.removeMessages(115);
        int action = (opt.getVoiceOnly()||bOnlyVoice)?0:opt.getDoubleTapAction();
        switch (action){
            case 0:{//双击暂停
                mPlay.performClick();
                int targetRes = !ispauseExpected ? R.drawable.ic_play_arrow_black_24dp :
                        R.drawable.ic_pause_black_24dp;
                toptopT.setTag(targetRes);
                toptopT.setText(null);
                Drawable d = getResources().getDrawable(targetRes).mutate();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) d.setTint(Color.WHITE);
                int w = (int) (66*opt.dm.density);
                d.setBounds(0,0,w,w);
                toptopT.setCompoundDrawables(d,null,null,null);
                toptopT.setVisibility(View.VISIBLE);
                capturedLPOC=lastPosOnClick;
                doubleClickDeteced=true;
                mHandler.animator2=0.05f;
                mHandler.animatorD2=0.045f;
                mHandler.sendEmptyMessage(115);
                break;
            }
            case 1:{//双击放大
                if(anim==null || anim.interruptible){
                    anim=null;
                    float targetScale;
                    PointF center;
                    if(opt.getPanoramaMode()){
                        int targetZoomIn;
                        if(mMediaPlayerCompat.getPanoramaZoomInStart()<=0){//放大
                            targetZoomIn= (int) (mMediaPlayerCompat.getMaxZoom()*0.618);
                        }
                        else{//缩小
                            targetZoomIn=0;
                        }
                        new AnimationBuilder(targetZoomIn, new PointF())
                                .withEasing(EASE_OUT_QUAD).withOrigin(ORIGIN_DOUBLE_TAP_ZOOM_PANORAMA)
                                .withDuration(250)
                                .startani()
                                ;
                        break;
                    }
                    if(scale<pendingScale*1.0001){//放大
                        //todo match bound limitation
                        //todo conform with less strict reboud scheme
                        //fitBound(FPoint SourcePoint)
                        center = fitSource(viewToSourceCoord(e.getX(), e.getY()));
                        targetScale=pendingScale*8;
                        targetScale=pendingScale*Math.max(getScreenWidth()/pendingWidth, getScreenHeight()/pendingHeight)*2.2f;
                        float viewscale = scale / pendingScale;
                        float targetSize = targetScale*sWidth;
                        float halfBand = getScreenWidth() / (2 * targetScale);
                        if(opt.getRebounceRespectMatchType() && pendingMatchType==Match_Height)
                            center.x=sWidth/2+(center.x-sWidth/2)*viewscale;
                        if(targetSize>getScreenWidth()){
                            center.x=Math.max(halfBand, Math.min(sWidth-halfBand, center.x));
                        }

                        targetSize = targetScale*sHeight;
                        halfBand = getScreenHeight() / (2 * targetScale);
                        if(opt.getRebounceRespectMatchType() && pendingMatchType==Match_Width)
                            center.y=sHeight/2+(center.y-sHeight/2)*viewscale;
                        else if(targetSize>getScreenHeight()) {
                            center.y = Math.max(halfBand, Math.min(sHeight - halfBand, center.y));
                        }
                    }
                    else{//缩小
                        center = new PointF(sWidth/2, sHeight/2);
                        targetScale=pendingScale;
                        float viewscale = scale / pendingScale;
                        float targetSize = targetScale*sWidth;
                        float halfBand = getScreenWidth() / (2 * targetScale);
                        if(targetSize>getScreenWidth()){
                            center.x=Math.max(halfBand, Math.min(sWidth-halfBand, viewToSourceX(e.getX())));
                        }
                        targetSize = targetScale*sHeight;
                        halfBand = getScreenHeight() / (2 * targetScale);
                        if(targetSize>getScreenHeight()){
                            center.y = Math.max(halfBand, Math.min(sHeight - halfBand,  viewToSourceX(e.getY())));
                        }
                    }
                    new AnimationBuilder(targetScale, center)
                            .withEasing(EASE_OUT_QUAD).withPanLimited(false).withOrigin(ORIGIN_DOUBLE_TAP_ZOOM)
                            .withDuration(250)
                            .startani();
                    break;
                }
            }
            default:return false;
        }
        if(!bIsUIHidden && opt.isDoubleTapDissmissUI()) switch_ui_hidden(true);
        return false;
    }

    private boolean overshrinked() {
        boolean reallyovershrinked=false;
        switch (pendingMatchType){
            case Match_Width:
                reallyovershrinked=getVideoView().getScaleX()*pendingWidth<getScreenWidth();
            break;
            case Match_Height:
                reallyovershrinked=getVideoView().getScaleY()*pendingHeight<getScreenHeight();
            break;
        }
        return reallyovershrinked;
    }

    /////////////////////////////////////////////////////////////////
    /*![] Start Animation Logic Copied from SubscamplingScaleImageVeiw*/
    private Anim anim;
    /** Progress animation */
    private void handle_animation() {
        //if(true) return;
        if (anim != null && anim.vFocusStart != null) {
            // Store current values so we can send an event if they change
            float scaleBefore = scale;
            float rotationBefore = rotation;
            if (vTranslateBefore == null) { vTranslateBefore = new PointF(0, 0); }
            vTranslateBefore.set(vTranslate);

            long scaleElapsed = System.currentTimeMillis() - anim.time;
            boolean finished = scaleElapsed > anim.duration;
            scaleElapsed = Math.min(scaleElapsed, anim.duration);
            scale = ease(anim.easing, scaleElapsed, anim.scaleStart, anim.scaleEnd - anim.scaleStart, anim.duration);
            //CMN.Log("fatal_scalanim", anim.scaleStart+","+(anim.scaleEnd - anim.scaleStart)+","+scale);
            if(anim.origin == ORIGIN_DOUBLE_TAP_ZOOM_PANORAMA){
                mMediaPlayerCompat.setPanoramaZoomIn(scale);
                //refreshPanorama(false);
                if (finished) {
                    anim = null;
                }
                return;
            }

            // Apply required animation to the focal point
            float vFocusNowX = ease(anim.easing, scaleElapsed, anim.vFocusStart.x, anim.vFocusEnd.x - anim.vFocusStart.x, anim.duration);
            float vFocusNowY = ease(anim.easing, scaleElapsed, anim.vFocusStart.y, anim.vFocusEnd.y - anim.vFocusStart.y, anim.duration);

            if (rotationEnabled) {
                float target = ease(anim.easing, scaleElapsed, anim.rotationStart, anim.rotationEnd - anim.rotationStart, anim.duration);
                Log.e("fatal_rotanim", anim.rotationStart+","+(anim.rotationEnd - anim.rotationStart)+","+target);
                setRotationInternal(target);
            }

            // Find out where the focal point is at this scale and adjust its position to follow the animation path
            PointF animVCenterEnd = sourceToViewCoord(anim.sCenterEnd);
            final float dX = animVCenterEnd.x - vFocusNowX;
            final float dY = animVCenterEnd.y - vFocusNowY;
            vTranslate.x -= (dX * cos + dY * sin);
            vTranslate.y -= (-dX * sin + dY * cos);
            //vTranslate.x -= sourceToViewX(anim.sCenterEnd.x) - vFocusNowX;
            //vTranslate.y -= sourceToViewY(anim.sCenterEnd.y) - vFocusNowY;


            if (finished) {
                anim = null;
            }
            handle_proxy_simul(scaleBefore, vTranslateBefore, rotationBefore);
        }
    }

    private void handle_proxy_simul(float scaleStamp, PointF translationStamp, float rotationStamp) {
        if(pendingScale!=0){
			getVideoView().setPivotX(0);
			getVideoView().setPivotY(0);
			getVideoView().setScaleX(scale / pendingScale);
			getVideoView().setScaleY(scale / pendingScale);
			getVideoView().setTranslationX(vTranslate.x);
			getVideoView().setTranslationY(vTranslate.y);
		}
    }

    /** State change originated from animation. */
    public static final int ORIGIN_ANIM = 1;
    /** State change originated from touch gesture. */
    public static final int ORIGIN_TOUCH = 2;
    /** State change originated from a fling momentum anim. */
    public static final int ORIGIN_FLING = 3;
    /** State change originated from a double tap zoom anim. */
    public static final int ORIGIN_DOUBLE_TAP_ZOOM = 4;
    public static final int ORIGIN_DOUBLE_TAP_ZOOM_PANORAMA = 5;
    /** Quadratic ease out. Not recommended for scale animation, but good for panning. */
    public static final int EASE_OUT_QUAD = 1;
    /** Quadratic ease in and out. */
    public static final int EASE_IN_OUT_QUAD = 2;
    private static class Anim {
        private float scaleStart; // Scale at startof anim
        private float scaleEnd; // Scale at end of anim (target)
        private float rotationStart; // Rotation at startof anim
        private float rotationEnd; // Rotation at end o anim
        private PointF sCenterStart; // Source center point at start
        private PointF sCenterEnd; // Source center point at end, adjusted for pan limits
        private PointF sCenterEndRequested; // Source center point that was requested, without adjustment
        private PointF vFocusStart; // View point that was double tapped
        private PointF vFocusEnd; // Where the view focal point should be moved to during the anim
        private long duration = 500; // How long the anim takes
        private boolean interruptible = true; // Whether the anim can be interrupted by a touch
        private int easing = EASE_IN_OUT_QUAD; // Easing style
        private int origin = ORIGIN_ANIM; // Animation origin (API, double tap or fling)
        private long time = System.currentTimeMillis(); // Start time
    }

    /** Apply a selected type of easing.
     * @param type Easing type, from static fields
     * @param time Elapsed time
     * @param from Start value
     * @param change Target value
     * @param duration Anm duration
     * @return Current value */
    private float ease(int type, long time, float from, float change, long duration) {
        switch (type) {
            case EASE_IN_OUT_QUAD:
                return easeInOutQuad(time, from, change, duration);
            case EASE_OUT_QUAD:
                return easeOutQuad(time, from, change, duration);
            default:
                throw new IllegalStateException("Unexpected easing type: " + type);
        }
    }

    /** Quadratic easing for fling. With thanks to Robert Penner - http://gizma.com/easing/
     * @param time Elapsed time
     * @param from Start value
     * @param change Target value
     * @param duration Anm duration
     * @return Current value */
    private float easeOutQuad(long time, float from, float change, long duration) {
        float progress = (float)time/(float)duration;
        return -change * progress*(progress-2) + from;
    }

    /** Quadratic easing for scale and center animations. With thanks to Robert Penner - http://gizma.com/easing */
    private float easeInOutQuad(long time, float from, float change, long duration) {
        float timeF = time/(duration/2f);
        if (timeF < 1) {
            return (change/2f * timeF * timeF) + from;
        } else {
            timeF--;
            return (-change/2f) * (timeF * (timeF - 2) - 1) + from;
        }
    }

    public final class AnimationBuilder {
        private final float targetScale;
        private final PointF targetSCenter;
        private final float targetRotation;
        private final PointF vFocus;
        private long duration = 500;
        private int easing = EASE_IN_OUT_QUAD;
        private int origin = ORIGIN_ANIM;
        private boolean interruptible = true;
        private boolean panLimited = true;
        private AnimationBuilder(float scale, PointF sCenter) {
            targetScale = scale;
            targetSCenter = sCenter;
            targetRotation = rotation;
            vFocus = null;
        }

        private AnimationBuilder(float scale, PointF sCenter, PointF vFocus_) {
            targetScale = scale;
            targetSCenter = sCenter;
            targetRotation = rotation;
            vFocus = vFocus_;
        }

        private AnimationBuilder(PointF sCenter, float rotation) {
            targetScale = scale;
            targetSCenter = sCenter;
            targetRotation = rotation;
            vFocus = null;
        }

        private AnimationBuilder(float scale, PointF sCenter, float rotation) {
            targetScale = scale;
            targetSCenter = sCenter;
            targetRotation = rotation;
            vFocus = null;
        }

        /**
         * Desired duration of the anim in milliseconds. Default is 500.
         * @param duration duration in milliseconds.
         * @return this builder for method chaining.
         */
        @NonNull
        public AnimationBuilder withDuration(long duration) {
            this.duration = duration;
            return this;
        }

        /**
         * Whether the animation can be interrupted with a touch. Default is true.
         * @param interruptible interruptible flag.
         * @return this builder for method chaining.
         */
        @NonNull
        public AnimationBuilder withInterruptible(boolean interruptible) {
            this.interruptible = interruptible;
            return this;
        }

        /**
         * Set the easing style. See static fields. {@link #EASE_IN_OUT_QUAD} is recommended, and the default.
         * @param easing easing style.
         * @return this builder for method chaining.
         */
        @NonNull
        public AnimationBuilder withEasing(int easing) {
            this.easing = easing;
            return this;
        }
        /**
         * Only for internal use. When set to true, the animation proceeds towards the actual end point - the nearest
         * point to the center allowed by pan limits. When false, animation is in the direction of the requested end
         * point and is stopped when the limit for each axis is reached. The latter behaviour is used for flings but
         * nothing else.
         */
        @NonNull
        private AnimationBuilder withPanLimited(boolean panLimited) {
            this.panLimited = panLimited;
            return this;
        }

        /**
         * Only for internal use. Indicates what caused the animation.
         */
        @NonNull
        private AnimationBuilder withOrigin(int origin) {
            this.origin = origin;
            return this;
        }

        public void startani() {
            int vxCenter = (int) (0 + (getScreenWidth())/2);
            int vyCenter = (int) (0+ (getScreenHeight())/2);
            //float targetScale = limitedScale(this.targetScale);
            //PointF targetSCenter = panLimited ? limitedSCenter(this.targetSCenter.x, this.targetSCenter.y, targetScale, new PointF()) : this.targetSCenter;
            boolean isPanorama = origin==ORIGIN_DOUBLE_TAP_ZOOM_PANORAMA;
            anim = new Anim();
            anim.scaleStart = isPanorama?mMediaPlayerCompat.getPanoramaZoomInStart():scale;
            anim.scaleEnd = targetScale;
            anim.rotationStart = rotation;
            anim.rotationEnd = targetRotation;
            anim.time = System.currentTimeMillis();
            anim.sCenterEndRequested = targetSCenter;
            anim.sCenterStart = getCenter();
            anim.sCenterEnd = targetSCenter;
            anim.vFocusStart = sourceToViewCoord(targetSCenter);
            anim.vFocusEnd = new PointF(
                    vxCenter,
                    vyCenter
            );
            anim.duration = duration;
            anim.interruptible = interruptible;
            anim.easing = easing;
            anim.origin = origin;
            anim.time = System.currentTimeMillis();

            if (vFocus != null) {
                // Calculate where translation will be at the end of the anim
                float vTranslateXEnd = vFocus.x - (targetScale * anim.sCenterStart.x);
                float vTranslateYEnd = vFocus.y - (targetScale * anim.sCenterStart.y);
                //ScaleTranslateRotate satEnd = new ScaleTranslateRotate(targetScale, new PointF(vTranslateXEnd, vTranslateYEnd), targetRotation);
                // Fit the end translation into bounds
                // Adjust the position of the focus point at end so image will be in bounds
                anim.vFocusEnd = new PointF(
                        vFocus.x + 0,
                        vFocus.y + 0
                );
            }
            animation_start_stamp = System.currentTimeMillis();
            mHandler.sendEmptyMessage(666);
        }

    }
    long animation_start_stamp=Long.MAX_VALUE;

    public final PointF getCenter() {
        int mX = (int) (getScreenWidth()/2);
        int mY = (int) (getScreenHeight()/2);
        return viewToSourceCoord(mX, mY);
    }
    public final PointF viewToSourceCoord(float vx, float vy) {
        return viewToSourceCoord(vx, vy, new PointF());
    }
    public final PointF viewToSourceCoord(PointF vxy, @NonNull PointF sTarget) {
        return viewToSourceCoord(vxy.x, vxy.y, sTarget);
    }
    public final PointF viewToSourceCoord(float vx, float vy, @NonNull PointF sTarget) {
        if (vTranslate == null) {
            Log.e("fatal","viewToSourceCoord vTranslate==null!");
            return null;
        }

        float sXPreRotate = viewToSourceX(vx);
        float sYPreRotate = viewToSourceY(vy);

        if (rotation == 0f) {
            sTarget.set(sXPreRotate, sYPreRotate);
        } else {
            // Calculate offset by rotation
            final float sourceVCenterX = viewToSourceX(getScreenWidth() / 2);
            final float sourceVCenterY = viewToSourceY(getScreenHeight() / 2);
            sXPreRotate -= sourceVCenterX;
            sYPreRotate -= sourceVCenterY;
            sTarget.x = (float) (sXPreRotate * cos + sYPreRotate * sin) + sourceVCenterX;
            sTarget.y = (float) (-sXPreRotate * sin + sYPreRotate * cos) + sourceVCenterY;
        }

        return sTarget;
    }
    private float viewToSourceX(float vx) {
        if (vTranslate == null) { return Float.NaN; }
        return (vx - vTranslate.x)/scale;
    }

    private float viewToSourceY(float vy) {
        if (vTranslate == null) { return Float.NaN; }
        return (vy - vTranslate.y)/scale;
    }

    public float minScale() {
        int vPadding = 0;
        int hPadding = 0;
        return Math.min((getScreenWidth() - hPadding) / (float) sWidth, (getScreenHeight() - vPadding) / (float) sHeight);
    }
    public final PointF sourceToViewCoord(PointF sxy, @NonNull PointF vTarget) {
        return sourceToViewCoord(sxy.x, sxy.y, vTarget);
    }
    @NonNull public final PointF sourceToViewCoord(PointF sxy) {
        return sourceToViewCoord(sxy.x, sxy.y, new PointF());
    }
    @NonNull public final PointF sourceToViewCoord(float sx, float sy, @NonNull PointF vTarget) {
        float xPreRotate = sourceToViewX(sx);
        float yPreRotate = sourceToViewY(sy);

        if (rotation == 0f) {
            vTarget.set(xPreRotate, yPreRotate);
        } else {
            // Calculate offset by rotation
            final float vCenterX = getScreenWidth() / 2;
            final float vCenterY = getScreenHeight() / 2;
            xPreRotate -= vCenterX;
            yPreRotate -= vCenterY;
            vTarget.x = (float) (xPreRotate * cos - yPreRotate * sin) + vCenterX;
            vTarget.y = (float) (xPreRotate * sin + yPreRotate * cos) + vCenterY;
        }

        return vTarget;
    }

    public float getScreenHeight() {
        return video_surface_frame.getHeight();
    }

    public float getScreenWidth() {
        return video_surface_frame.getWidth();
    }

    private float sourceToViewX(float sx) {
        if (vTranslate == null) { return Float.NaN; }
        return (sx * scale) + vTranslate.x;
    }

    private float sourceToViewY(float sy) {
        if (vTranslate == null) { return Float.NaN; }
        return (sy * scale) + vTranslate.y;
    }

    /**  Pythagoras distance between two points. */
    private float distance(float x0, float x1, float y0, float y1) {
        float x = x0 - x1;
        float y = y0 - y1;
        return (float) Math.sqrt(x * x + y * y);
    }

    /** Sets rotation without invalidation */
    private void setRotationInternal(float rot) {
        // Normalize rotation between 0..2pi
        this.rotation = rot % (float) (Math.PI * 2);
        if (this.rotation < 0) this.rotation += Math.PI * 2;

        this.cos = Math.cos(rot);
        this.sin = Math.sin(rot);
    }

    /*![] End Animation Logic Copied from SubscamplingScaleImageVeiw*/
    /////////////////////////////////////////////////////////////////

    private void pushassidesidebuttons(){
        ((MarginLayoutParams)widget13.getLayoutParams()).rightMargin= (int) (45*dm.density);
        ((MarginLayoutParams)widget14.getLayoutParams()).rightMargin=0;
        widget13.setLayoutParams(widget13.getLayoutParams());
        widget14.setLayoutParams(widget14.getLayoutParams());
    }

    private void reset_time_highlighting(int st, int ed) {
        if(viewList.size()>0 && viewPager.getCurrentItem()<viewList.size()) {
            PageHolder holder = viewList.get(viewPager.getCurrentItem());
            if (holder.prevTime < holder.nxtTime) {
                SpannableString span = holder.baseSpan;
                ColoredHighLightSpan[] spans = span.getSpans(st, ed, ColoredHighLightSpan.class);
                if(spans.length>0){
                    ColoredHighLightSpan spC = spans[0];
                    int start=span.getSpanStart(spC), end=span.getSpanEnd(spC);
                    span.removeSpan(spC);
                    span.setSpan(spC, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }

    private void refreshTextViewHighLight(final long progress){
		//try {
		//	CMN.Log("timeprogress",progress, viewList.get(viewPager.getCurrentItem()).prevTime, viewList.get(viewPager.getCurrentItem()).nxtTime);
		//} catch (Exception e) { }
		final long timeprogress=progress+10;
        //if current Time is out of domain[prev,nxt]
        if(viewList.size()>0 && viewPager.getCurrentItem()<viewList.size()) {
            PageHolder holder = viewList.get(viewPager.getCurrentItem());
            if (timeprogress >= holder.nxtTime || timeprogress < holder.prevTime) {
                int currentIntervalEndIdx = holder.subs_timeNodeTree.lookUpKey(timeprogress, false);
                //CMN.Log("currentIntervalEndIdx", currentIntervalEndIdx);
                //if(timeprogress==subs_timeNodeTree.getKeyAt(currentIntervalEndIdx))
                //    currentIntervalEndIdx+=1;
				boolean sjzw = currentIntervalEndIdx==0;
                if (currentIntervalEndIdx >= 0) {
                    holder.prevTime = sjzw?0:holder.subs_timeNodeTree.data.get(currentIntervalEndIdx - 1);
                    holder.nxtTime = currentIntervalEndIdx < holder.subs_timeNodeTree.size() ? holder.subs_timeNodeTree.getKeyAt(currentIntervalEndIdx) : mMediaPlayerCompat.getTime();
                    if (holder.prevTime < holder.nxtTime) {
                        ColoredHighLightSpan[] spans = holder.baseSpan.getSpans(holder.lastHighLightStart, holder.lastHighLightEnd, ColoredHighLightSpan.class);
                        for (ColoredHighLightSpan sI : spans) {
                            holder.baseSpan.removeSpan(sI);
                        }

                        holder.lastHighLightStart = sjzw?0:holder.subs_timeNodeTree.value.get(currentIntervalEndIdx - 1);
                        holder.lastHighLightEnd = sjzw?0:currentIntervalEndIdx < holder.subs_timeNodeTree.size() ? holder.subs_timeNodeTree.value.get(currentIntervalEndIdx) : holder.tv.getText().length();

                        Layout layout = holder.tv.getLayout();
                        if(layout==null)return;
                        int line = sjzw?0:layout.getLineForOffset(holder.lastHighLightStart);

                        //if current subscript line is out of scrollview's scope

                        //CMN.Log(holder.tv.requestFocus(), System.currentTimeMillis() - SelectableTextView.lastTouchTime,holder.tv.draggingHandle==null , !holder.tv.isDragging);
                        if(menupopup==null || !menupopup.isShowing())
                        if(System.currentTimeMillis() - SelectableTextView.lastTouchTime >800)//delay scroll
                        if(holder.tv.draggingHandle==null && !holder.tv.isDragging) {
                            int highLimit=holder.tv.sv.getScrollY(),height=holder.tv.sv.getHeight(), lowLimit=highLimit+height;
                            int y;
                            int compensation=holder.tv.getLineHeight()/5;
                            if ((y = layout.getLineTop(line)) < highLimit){//顶过高
                                holder.tv.sv.smoothScrollTo(0, y);
                            } else if((layout.getLineBottom(line))+compensation >= lowLimit) {//上底过低, 重置
                                holder.tv.sv.smoothScrollTo(0, y);
                            } else if((y = layout.getLineBottom(layout.getLineForOffset(holder.lastHighLightEnd)))+compensation > lowLimit) {//下底过低, 回弹
                                holder.tv.sv.smoothScrollTo(0, y-height+compensation);
                            }
                        }

						if(!sjzw){
							//if(opt.getDrawHighlightOnTextView())
							//	holder.baseSpan.setSpan(new ColoredHighLightSpan(highLightBG, 9f, 1),
							//			holder.lastHighLightStart, holder.lastHighLightEnd, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
							//else
								holder.tv.inflateHighLightPoolPool(holder.lastHighLightStart, holder.lastHighLightEnd, highLightBG);
						}
                    }
                }
            }
        }
    }

    public static Bitmap blurByGauss(Bitmap srcBitmap, int radius) {
        Bitmap bitmap = srcBitmap.copy(srcBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int[] vmin = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int temp = 256 * divsum;
        int[] dv = new int[temp];
        for (i = 0; i < temp; i++) {
            dv[i] = (i / divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                sir = stack[i + radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1 - Math.abs(i);
                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return bitmap;
    }

    void ExLearnringMode() {
        if(opt.isInLearningMode() && textMain!=null){
        	boolean bNoLyricsFound=viewList.size()==0;
            if(bNoLyricsFound) { if(textMain.getVisibility()!=View.GONE) textMain.setVisibility(View.GONE); }
            else if(textMain.getVisibility()!=View.VISIBLE) textMain.setVisibility(View.VISIBLE);

			if(opt.getVoiceOnly() || bOnlyVoice){
                IMPageCover.getLayoutParams().width=-1;
                IMPageCover.getLayoutParams().height=-1;
                IMPageCover.setLayoutParams(IMPageCover.getLayoutParams());
                IMPageCover.setVisibility(View.VISIBLE);
                IMPageCover.setTag(R.id.home, false);

                Priority priority = Priority.HIGH;
                RequestOptions options = new RequestOptions()
                        .signature(new ObjectKey(mMediaPlayerCompat.currentFile.lastModified()))//+"|"+item.size
                        .format(DecodeFormat.PREFER_ARGB_8888)//DecodeFormat.PREFER_ARGB_8888
                        .priority(priority)
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        //.onlyRetrieveFromCache(true)
                        .fitCenter()
                        .override(bOnlyVoice?Target.SIZE_ORIGINAL:360, Target.SIZE_ORIGINAL);
				RequestBuilder<Bitmap> IcanOpen = Glide.with(getApplicationContext()).asBitmap();
				(bOnlyVoice?
						IcanOpen.load(new AudioCover(mMediaPlayerCompat.currentFile.getAbsolutePath())):
						IcanOpen.load(mMediaPlayerCompat.currentFile.getAbsolutePath()))
                        .apply(options)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .listener(new RequestListener<Bitmap>(){
                            @Override public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) { return false; }
                            @Override public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                ImageView medium_thumbnail = ((ImageViewTarget<?>) target).getView();
								if(bOnlyVoice){
									medium_thumbnail.setImageBitmap(resource);
									medium_thumbnail.setScaleType(ImageView.ScaleType.FIT_CENTER);
									Palette.from(resource).generate(palette -> {
										if(palette==null)return;
										int targetColor;
										if ((targetColor=palette.getLightMutedColor(Color.BLACK)) != Color.BLACK) { }
										else if ((targetColor=palette.getLightVibrantColor(Color.BLACK)) != Color.BLACK) { }
										else if ((targetColor=palette.getDarkVibrantColor(Color.BLACK)) != Color.BLACK) { }
										else if ((targetColor=palette.getDarkMutedColor(Color.BLACK)) != Color.BLACK) { }
										medium_thumbnail.setBackgroundColor(targetColor);
									});
								}
                                else{
									medium_thumbnail.setImageBitmap(blurByGauss(resource, 5));
									medium_thumbnail.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                }
                                return true;
                            }
                        })
                        .into(IMPageCover)
                ;

                if(opt.isFullScreen() && opt.isFullscreenHideNavigationbar())
                    getWindowManager().getDefaultDisplay().getRealMetrics(dm);
                else
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                int emptyheight= bNoLyricsFound?-1:(int) (175*dm.density);
                int targetheight=bNoLyricsFound?0:dm.heightPixels-emptyheight;

                //if(viewList.size() > 0){
                    boolean fit = textMain.getLayoutParams().height==targetheight;
                    if(!fit){
                        textMain.getLayoutParams().height=targetheight;
                        textMain.setLayoutParams(textMain.getLayoutParams());
                        video_surface_frame.getLayoutParams().height=emptyheight;
                        video_surface_frame.setLayoutParams(video_surface_frame.getLayoutParams());
                    }
                //}
            }
        }
        else if(IMPageCover.getVisibility()==View.VISIBLE){
            IMPageCover.setVisibility(View.GONE);
        }
    }

    /** Refresh size of videoview and it's parent view*/
    public void refreshSVLayout(int forceType){
        //CMN.Log("----refreshSVLayout");
        animation_start_stamp=Long.MAX_VALUE;
        anim=null;
        if(getVideoView()==null)
            return;
        if(getVideoView().getScaleX()!=1){
            getVideoView().setScaleX(1);
            getVideoView().setScaleY(1);
        }
        FrameLayout.LayoutParams targetLayoutParams = (FrameLayout.LayoutParams) getVideoView().getLayoutParams();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) video_surface_frame.getLayoutParams();
        targetLayoutParams.gravity=Gravity.START|Gravity.TOP;
        targetLayoutParams.height=-1;
        targetLayoutParams.width=-1;

        if(opt.isFullScreen() && opt.isFullscreenHideNavigationbar())
            getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        else
            getWindowManager().getDefaultDisplay().getMetrics(dm);

        if(opt.getPanoramaMode()){
            getVideoView().setLayoutParams(getVideoView().getLayoutParams());
            mMediaPlayerCompat.onPanoramaResize(dm.widthPixels, dm.heightPixels);
            return;
        }

        boolean isActuallyInLearningMode = opt.isInLearningMode() && viewList.size() > 0;
        int w = dm.widthPixels;
        int h = dm.heightPixels;
        //int w = getWindow().getDecorView().getWidth();
        //int h = getWindow().getDecorView().getHeight();
        if(!opt.isFullScreen())
            h-=CMN.getStatusBarHeight(this);
        w-=DockerMarginR+DockerMarginL;
        h-=DockerMarginT+DockerMarginB;
        if(isActuallyInLearningMode)
            h=h/2;

        float newW = w;
        float newH = h;
        params.width = w;
        params.height = h;
        pendingTransX = 0;
        pendingTransY = 0;
        pendingWidth = 0;
        pendingHeight = 0;
        pendingScale = 1;
        pendingMatchType = -1;

        int type=forceType>=0?forceType:opt.getMatchtType();
        if(type==Match_Auto && preferedMatchType!=-1) type=preferedMatchType;
        switch(type){
            case Match_Auto:
                pendingMatchType=3;
            case Match_Width:
                //CMN.Log("Match_Width");
                OUT: {
                    targetLayoutParams.width = (int) (0.5+w*1.0*mVideoWidth/mVideoVisibleWidth);
                    newH = 1.f*w*mVideoVisibleHeight/mVideoVisibleWidth;
                    float bottomPad = (mVideoVisibleHeight - mVideoHeight) * newH * 1.f/mVideoVisibleHeight;
                    newH-=bottomPad;
                    targetLayoutParams.height = (int) newH;
                    if(newH<=h){
                        if(!isActuallyInLearningMode) {
                            pendingTransY = -(newH - h + bottomPad) / 2; //targetLayoutParams.gravity = Gravity.CENTER;
                        }else
                            params.height = (int) (newH+bottomPad);
                    }else {
                        if(pendingMatchType==3) break OUT;//吾乃天上之星，大地之汉。
                        pendingTransY = -(newH - h + bottomPad) / 2;
                    }
                    pendingMatchType=Match_Width;
                    break;
                }
            case Match_Height:
				//CMN.Log("Match_Height");
                newH = h; pendingTransY = 0;
                //bottomPad = 1.f*(mVideoVisibleHeight - mVideoHeight)*dm.density  * newH /mVideoVisibleHeight;
                //targetLayoutParams.height = (int) (h - bottomPad);
                targetLayoutParams.height = (int) (0.5+h*1.0*mVideoHeight/mVideoVisibleHeight);
                newW = 1.f * newH * mVideoVisibleWidth / mVideoVisibleHeight;
                float rightPad = (mVideoVisibleWidth - mVideoWidth) * newW * 1.f / mVideoVisibleWidth;
                newW-=rightPad;
                targetLayoutParams.width = (int) newW;
                if(newW<w){
                    pendingTransX = -(newW - w) / 2;//targetLayoutParams.gravity=Gravity.TOP|Gravity.CENTER;
                }else {
                    pendingTransX = -(newW - w) / 2;
                }
                pendingMatchType=Match_Height;
            break;
            case Match_None:
            break;
        }
        if(opt.getMatchtType()==Match_Auto){
            wiget5ld.findDrawableByLayerId(R.id.wiget1).setAlpha(pendingMatchType==Match_Width?255:0);
            wiget5ld.findDrawableByLayerId(R.id.wiget2).setAlpha(pendingMatchType==Match_Height?255:0);
        }

        getVideoView().setLayoutParams(targetLayoutParams);
        getVideoView().setTranslationX(pendingTransX);
        getVideoView().setTranslationY(pendingTransY);
        vTranslate.set(pendingTransX, pendingTransY);
        vTranslateOrg = new PointF(vTranslate.x, vTranslate.y);
        pendingWidth=targetLayoutParams.width;
        pendingHeight=targetLayoutParams.height;
        //sWidth=targetLayoutParams.width;
        //sHeight=targetLayoutParams.height;
        //scale=1;
        sWidth=mVideoWidth;
        sHeight=mVideoHeight;
        pendingScale=scale=targetLayoutParams.width*1.0f/mVideoWidth;
        maxScale=pendingScale*10.5f;

        video_surface_frame.setLayoutParams(params);

        if(textMain!=null) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) textMain.getLayoutParams();
            lp.height = dm.heightPixels - (opt.isFullScreen()?0:CMN.getStatusBarHeight(this)) - DockerMarginT - DockerMarginB - params.height;
            lp.gravity = Gravity.BOTTOM;
            //lp.topMargin = (int) newH;

            //lp.bottomMargin = bottombar.getHeight();
            textMain.setLayoutParams(lp);
            textMain.setVisibility(View.VISIBLE);
        }
        IMPageCover.setTag(null);
    }

    /** Callback receiving <b>onVideoSizeChanged</b> Event from various players.
     *  @param width: actual video width decoded by players
     *  @param height: actual video height decoded by players
     *  @param visibleWidth: visible video width required by libvlc renderer
     *  @param visibleHeight: visible video height required by libvlc renderer */
    public void onNewVideoViewLayout(int width, int height, int visibleWidth, int visibleHeight) {
        //CMN.Log("onNewVideoViewLayout? ",mVideoWidth,   mVideoHeight,   mVideoVisibleWidth,   mVideoVisibleHeight, width * height, mVideoWidthReq);
        //if(getVideoView()==null) return;
		if(width==-1 && height==-1){
			visibleWidth=width=dm.widthPixels;
			visibleHeight=height=dm.heightPixels;
		}
		if(IMPageCover.getVisibility()==View.VISIBLE)
			IMPageCover.setVisibility(View.GONE);
        if (width * height == 0) return;
        if(mVideoWidthReq || mVideoWidth != width || mVideoHeight != height || mVideoVisibleWidth != visibleWidth || mVideoVisibleHeight != visibleHeight){
            mVideoWidth = width;
            mVideoHeight = height;
            mVideoVisibleWidth = visibleWidth;
            mVideoVisibleHeight = visibleHeight;
            refreshSVLayout(-1);
            mVideoWidthReq=false;
        }
    }


    // 提示用户 去设置界面 手动开启权限
    private void AppSetttingShowDialogReq() {
        //动态申请不成功，转为手动开启权限
        d = new AlertDialog.Builder(this)
                .setTitle(R.string.stgerr_cannot)
                .setMessage(R.string.stg_mannual)
                .setPositiveButton(R.string.stg_grantnow, (dialog, which) -> {
                    // 跳转到应用设置界面
                    Intent intent = new Intent();

                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);

                    startActivityForResult(intent, 123);
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> finish()).setCancelable(false).show();

    }

    //权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //CMN.Log(permissions,"\r\n",grantResults);
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        AppSetttingShowDialogReq();
                    } else
                        finish();
                } else {
                    showX(R.string.stg_succ, Toast.LENGTH_SHORT);
                    pre_further_loading(null);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent duco) {
        super.onActivityResult(requestCode, resultCode, duco);
        switch (requestCode) {
            case 123:{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    int i = checkSelfPermission(permissions[0]);
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        AppSetttingShowDialogReq();
                    } else {
                        if (d != null && d.isShowing()) {
                            d.dismiss();
                            d = null;
                        }
                        showX(R.string.stg_succ, Toast.LENGTH_SHORT);
                        pre_further_loading(null);
                    }
                }
                break;
            }
            case 700:if(resultCode==RESULT_OK){
                Uri treeUri = duco.getData();
                if(treeUri!=null) {
                    int GRANTFLAGS = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                    grantUriPermission(getPackageName(), treeUri, GRANTFLAGS);
                    getContentResolver().takePersistableUriPermission(treeUri, GRANTFLAGS);
                }
                break;
            }
        }
    }

    // https://www.cnblogs.com/RabbitLx/p/5886528.html
    public void changeAppBrightness(int brightness) {
        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //Reflection.unseal(base);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMediaPlayerCompat!=null && mMediaPlayerCompat.currentFile!=null){
			outState.putString("runmed",mMediaPlayerCompat.currentFile.getAbsolutePath());
			outState.putLong("time",mMediaPlayerCompat.getTime());
		}
    }


    private void AskPermissionSnack(View snv) {
        if(snv==null) snv=root;
        snackbar = Snackbar.make(snv, permission_asker!=0?permission_asker:R.string.stg_require,Snackbar.LENGTH_SHORT);
        if(FU.bKindButComplexSdcardAvailable)
        snackbar.setAction(R.string.grant, view -> {
            //getContext().startActivity(new Intent(Intent.ACTION_MAIN).setClass(getContext(), StorageActivity.class));
            if(view.getTag()==null)
                startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), 700);
            else
                finish();
        });
        snackbar.show();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void AskExternalPermissionSnack(View snv, int id) {
        if(snv==null) snv=root;
        snackbar = Snackbar.make(snv, id,Snackbar.LENGTH_SHORT);
        if(FU.bKindButComplexSdcardAvailable)
        snackbar.setAction("赋予", view -> {
            if(view.getTag()==null)
                showDialogTipUserRequestPermission();
            else
                finish();
        });
        snackbar.show();
    }

    private void sn(int id, View snv) {
        if(snv==null) snv=root;
        snackbar = Snackbar.make(snv, id,Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void sn(String val, View snv) {
        if(snv==null) snv=root;
        snackbar = Snackbar.make(snv, val,Snackbar.LENGTH_LONG);
        snackbar.show();
    }

}
