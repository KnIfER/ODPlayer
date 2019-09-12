package com.knziha.ODPlayer;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.knziha.filepicker.view.CMNF;

@SuppressWarnings("unused")
public class VICMainAppOptions
{
    public static boolean isTransparentDialog;
	public static int BGB = 0xc0333333;
	public static int BGB2 = 0xc0333333;
    //SharedPreferences reader;
    SharedPreferences defaultReader;
	public static String locale;

	public VICMainAppOptions(Context a_){
        //reader2 = a_.getSharedPreferences("SizeChangablePrefs",Activity.MODE_PRIVATE);
        defaultReader = PreferenceManager.getDefaultSharedPreferences(a_);
    }

	public String getLocale() {
		return locale!=null?locale:(locale=defaultReader.getString("locale",""));
	}

	public int getBackgroundLightColor() {
		return BGB=defaultReader.getInt("back_light",0xc0333333);
	}

	public int getBackgroundLightColor2() {
		return BGB2=defaultReader.getInt("back_light2",0xc0333333);
	}

    public Editor defaultputter() {
        return defaultReader.edit();
    }
    public void putRate(float val) {
        defaultReader.edit().putFloat("Rate",val).commit();
    }
    public float getRate() {
        return defaultReader.getFloat("Rate",1);
    }
    public void putVolume(float val) {
        defaultReader.edit().putFloat("Vol",val).commit();
    }
    public float getVolume() {
        return defaultReader.getFloat("Vol",1);
    }
    public int EqbarSpacing=-1;
    public int EqbarSize=-1;
    public int getEqBarSpacing() {
        return EqbarSpacing>=0?EqbarSpacing:(EqbarSpacing=defaultReader.getInt("EqGap",(int) (10 * dm.density)));
    }

    public int getEqBarSize() {
        return EqbarSize>=0?EqbarSize:(EqbarSize=Math.max(defaultReader.getInt("EqSize",(int) (30 * dm.density)), (int) (5 * dm.density)));
    }

    public int get_preset_api_idx() {
        return defaultReader.getInt("psetapi",0);
    }

    public int get_preset_vlc_idx() {
        return defaultReader.getInt("psetvlc",0);
    }

    //////////First Boolean Flag//////////
    // ||||||||    ||||||||	    |FVDOCKED|PBOB|Ficb|FBOB|ForceSearch|showFSroll|showBD|showBA	|ToD|ToR|ToL|InPeruseTM|InPeruse|SlideTURNP|BOB|icb
    //                              0        1     0    1      0             0         0     0            0 1 0          0   1         1   0   0
    //||||||||    ||||||||CBE
    //
    private static Long FirstFlag=null;
    public long getFirstFlag() {
        if(FirstFlag==null) {
            return CMNF.FirstFlag=FirstFlag=defaultReader.getLong("MFF",0);//76+32768+65536
        }
        return FirstFlag;
    }
    private void putFirstFlag(long val,boolean CommitOrApply) {
        Editor editor = defaultReader.edit().putLong("MFF", FirstFlag=val);
        if(CommitOrApply) editor.commit();
        else editor.apply();
    }

    public void putFlags(boolean CommitOrApply) {
        Editor editor = defaultReader.edit().putLong("MFF", FirstFlag).putLong("MSF", SecondFlag);
        if(CommitOrApply) editor.commit();
        else editor.apply();
    }

    /**bCommitOrApply<br>&nbsp;true: Commit<br>&nbsp;false : apply*/
    public void putFirstFlag(boolean bCommitOrApply) {
        putFirstFlag(FirstFlag, bCommitOrApply);
    }
    public Long FirstFlag() {
        return FirstFlag;
    }
    private static void updateFFAt(int o, boolean val) {
        FirstFlag &= (~o);
        if(val) FirstFlag |= o;
        //defaultReader.edit().putInt("MFF",FirstFlag).commit();
    }
    private void updateFFAt(long o, boolean val) {
        FirstFlag &= (~o);
        if(val) FirstFlag |= o;
        //defaultReader.edit().putInt("MFF",FirstFlag).commit();
    }

    public boolean get_USE_SURFACE_VIEW() {
        return (FirstFlag & 1) == 1;
    }
    public boolean get_USE_SURFACE_VIEW(Long FirstFlag) {
        return (FirstFlag & 1) == 1;
    }
    public boolean set_USE_SURFACE_VIEW(boolean val) {
        updateFFAt(1,val);
        return val;
    }



    public boolean get_ENABLE_SUBTITLES() {//false
        return (FirstFlag & 2) != 2;
    }
    public boolean set_ENABLE_SUBTITLES(boolean val) {
        updateFFAt(2,!val);
        return val;
    }


    public int getHardWareDecaodeType() {//true
        return 0;//(int) ((FirstFlag >> 2) & 7);
    }
    public int setHardWareDecaodeType(int val) {
        FirstFlag &= (~0x4);
        FirstFlag &= (~0x8);
        FirstFlag |= ((long)(val & 3)) << 2;
        return val;
    }


    public boolean keepBottomBar() {//false
        return (FirstFlag & 16) == 16;
    }
    public boolean setkeepBottomBar(boolean val) {
        updateFFAt(16,val);
        return val;
    }

    public boolean isEqualizerEnabled() {
        return (FirstFlag & 32) == 32;
    }
    public boolean setEqualizerEnabled(boolean val) {
        updateFFAt(32,val);
        return val;
    }
    public boolean isEqualizerShown() {//true
        return (FirstFlag & 64) != 64;
    }
    public boolean setEqualizerShown(boolean val) {
        updateFFAt(64,!val);
        return val;
    }
    public static boolean isDrawSimpleSelection() {//false
        return true;//(FirstFlag & 128) == 128;
    }
    public static boolean setDrawSimpleSelection(boolean val) {
        updateFFAt(128,val);
        return val;
    }

    public boolean isUsingDoubleClick() {//false
        return true;//(FirstFlag & 0x100) == 0x100;//256
    }
    public boolean setUsingDoubleClick(boolean val) {
        updateFFAt(0x100,val);
        return val;
    }

    public int getScreenPhaseCount() {//default to 3     2,3,4
        return 2;//((int) ((FirstFlag >> 9) & 3)+1)%3+2;
    }
    public int setScreenPhaseCount(int val) {
        val=val%3;
        FirstFlag &= (~0x200);
        FirstFlag &= (~0x400);
        FirstFlag |= (val & 3) << 9;
        return val;
    }
    //nn
    public int getMatchtType() {//default to 2 (match auto)  0,1,2,3
        return 2;//((int) ((FirstFlag >> 11) & 3)+2)%4;
    }
    public int setMatchtType(int val) {
        FirstFlag &= (~0x800);
        FirstFlag &= (~0x1000);
        FirstFlag |= ((val+2)%4 & 3) << 11;
        return val;
    }


    public boolean getInfoShowMore() {//false
        return (FirstFlag & 0x2000) == 0x2000;
    }
    public boolean setInfoShowMore(boolean val) {
        updateFFAt(0x2000,val);
        return val;
    }

    public boolean getInfoPinned() {//true
        return (FirstFlag & 0x4000) == 0x4000;
    }
    public boolean setInfoPinned(boolean val) {
        updateFFAt(0x4000,val);
        return val;
    }

    public boolean getAccurateSeek() {//true
        return (FirstFlag & 0x8000) == 0x8000;
    }
    public boolean getAccurateSeek(Long FirstFlag) {
        return (FirstFlag & 0x8000) == 0x8000;
    }
    public boolean setAccurateSeek(boolean val) {
        updateFFAt(0x8000,val);
        return val;
    }

    /**Also read by filepicker/slideshowactivity*/
    public boolean isFullScreen() {
        return (FirstFlag & 0x10000) == 0x10000;
    }
    public boolean setFullScreen(boolean val) {
        updateFFAt(0x10000,val);
        CMNF.FirstFlag=FirstFlag;
        return val;
    }
    public boolean getSingleMusic() {
        return (FirstFlag & 0x20000) != 0x20000;
    }
    public boolean setSingleMusic(boolean val) {
        updateFFAt(0x20000,!val);
        return val;
    }
    public boolean isMute() {
        return (FirstFlag & 0x40000) == 0x40000;
    }
    public boolean setMute(boolean val) {
        updateFFAt(0x40000,val);//
        return val;
    }

    public boolean isBackgroundPlayEnabled() {
        return (FirstFlag & 0x80000) == 0x80000;
    }
    public boolean setBackgroundPlayEnabled(boolean val) {
        updateFFAt(0x80000,val);//0x‭80000
        return val;
    }

    public boolean isFullscreenHideNavigationbar() {
        return true;//(FirstFlag & 0x100000) == 0x100000;
    }
    public boolean setFullscreenHideNavigationbar(boolean val) {
        updateFFAt(0x100000,val);
        return val;
    }

    public boolean isInLearningMode() {
        return (FirstFlag & 0x200000) == 0x200000;
    }
    public boolean setInLearningMode(boolean val) {
        updateFFAt(0x200000,val);
        return val;
    }

    public boolean rewindOnReplay() {
        return false;//(FirstFlag & 0x400000) == 0x400000;
    }
    public boolean setRewindOnReplay(boolean val) {
        updateFFAt(0x400000,val);
        return val;
    }

    public boolean rewindOnPause() {
        return false;//(FirstFlag & 0x800000) == 0x800000;
    }
    public boolean setRewindOnPause(boolean val) {
        updateFFAt(0x800000,val);
        return val;
    }

    ////
    public boolean isScrollSeekImmediate() {
        return true;//(FirstFlag & 0x1000000) != 0x1000000;
    }
    public boolean setcrollSeekImmediate(boolean val) {
        updateFFAt(0x1000000,!val);
        return val;
    }

    /** <b>DeletionScheme</b>: <br> dialog:<br> 1:popup<br> 2:popup<br> 3:delete edirectly*/
    public int getDeletionScheme() {
        return (int) ((FirstFlag >> 25) & 3);
    }
    public int setDeletionScheme(int val) {
        FirstFlag &= (~0x2000000l);
        FirstFlag &= (~0x4000000l);
        FirstFlag |= ((long)(val & 3)) << 25;
        return val;
    }


    public boolean isSeekBtnShown() {
        return (FirstFlag & 0x8000000) == 0x8000000;
    }
    public boolean setSeekBtnShown(boolean val) {
        updateFFAt(0x8000000,val);
        return val;
    }

    public boolean isSeekButtonsHideWithUI() {
        return (FirstFlag & 0x10000000) != 0x10000000;
    }
    public boolean setSeekButtonsHideWithUI(boolean val) {
        updateFFAt(0x10000000,!val);
        return val;
    }

    public boolean isSpeedBtnShown() {
        return (FirstFlag & 0x20000000) == 0x20000000;
    }
    public boolean setpeedkBtnShown(boolean val) {
        updateFFAt(0x20000000,val);
        return val;
    }

    public boolean isSpeedButtonsHideWithUI() {
        return (FirstFlag & 0x40000000) == 0x40000000;
    }
    public boolean setSpeedButtonsHideWithUI(boolean val) {
        updateFFAt(0x40000000,val);
        return val;
    }

    public boolean isABLoopBtnShown() {//from editor
        return (FirstFlag & 0x80000000) == 0x80000000;
    }
    public boolean setABLoopBtnShown(boolean val) {
        updateFFAt(0x80000000,val);
        return val;
    }
    /////////////////////End First 32-bit Flag////////////////////////////////////

    /////////////////////Start First Flag Long field///////////////////////////////////
    public boolean isABLoopButtonsHideWithUI() {
        return (FirstFlag & 0x100000000l) == 0x100000000l;
    }
    public boolean setABLoopButtonsHideWithUI(boolean val) {
        updateFFAt(0x100000000l,val);
        return val;
    }
    public boolean getVoiceOnly() {
        return (FirstFlag & 0x200000000l) == 0x200000000l;
    }
    public boolean getVoiceOnly(Long FirstFlag) {
        return (FirstFlag & 0x200000000l) == 0x200000000l;
    }
    public boolean setVoiceOnly(boolean val) {
        updateFFAt(0x200000000l,val);
        return val;
    }
    public int getPlayerType() {
        return (int) ((FirstFlag >> 34) & 3);
    }
    public int setPlayerType(int val) {
        FirstFlag &= (~0x400000000l);
        FirstFlag &= (~0x800000000l);
        FirstFlag |= ((long)(val & 3)) << 34;
        return val;
    }

    public boolean getRateEnabled() {
        return (FirstFlag & 0x1000000000l) == 0x1000000000l;
    }
    public boolean setRateEnabled(boolean val) {
        updateFFAt(0x1000000000l,val);
        return val;
    }

    public boolean getVolumeEnabled() {
        return (FirstFlag & 0x2000000000l) == 0x2000000000l;
    }
    public boolean setVolumeEnabled(boolean val) {
        updateFFAt(0x2000000000l,val);
        return val;
    }

    public boolean getPanoramaMode() {
        return (FirstFlag & 0x4000000000l) == 0x4000000000l;
    }
    public boolean getPanoramaMode(long FirstFlag) {
        return (FirstFlag & 0x4000000000l) == 0x4000000000l;
    }
    public boolean setPanoramaMode(boolean val) {
        updateFFAt(0x4000000000l,val);
        return val;
    }

    public boolean getVibrationEnabled() {
        return (FirstFlag & 0x20000000000l) != 0x20000000000l;
    }

    public boolean setVibrationEnabled(boolean val) {
        updateFFAt(0x20000000000l,!val);
        return val;
    }

    public boolean isEqualizerReallyEnabled() {
        return (FirstFlag & 0x40000000000L) != 0x40000000000L;
    }
    public boolean setEqualizerReallyEnabled(boolean val) {
        updateFFAt(0x40000000000L,!val);
        return val;
    }
    public boolean getTintEqBars() {
        return (FirstFlag & 0x80000000000l) == 0x80000000000l;
    }
    public boolean setTintEqBars(boolean val) {
        updateFFAt(0x80000000000l,val);
        return val;
    }
    public boolean getNotInOctopusMode() {//getUseAdvancedEqTw
        return (FirstFlag & 0x100000000000l) != 0x100000000000l;
    }
    public boolean setNotInOctopusMode(boolean val) {
        updateFFAt(0x100000000000l,!val);
        return val;
    }

    public boolean getAutoExpandPresets() {
        return (FirstFlag & 0x200000000000l) != 0x200000000000l;
    }
    public boolean setAutoExpandPresets(boolean val) {
        updateFFAt(0x200000000000l,!val);
        return val;
    }

	public boolean getRespectAudioManager() {
		return (FirstFlag & 0x400000000000l) != 0x400000000000l;
	}
	public boolean setRespectAudioManager(boolean val) {
		updateFFAt(0x400000000000l,!val);
		return val;
	}

    public boolean getPinTweakerDialog() {
        return (FirstFlag & 0x800000000000l) != 0x800000000000l;
    }
    public boolean setPinTweakerDialog(boolean val) {
        updateFFAt(0x800000000000l,!val);
        return val;
    }

    public boolean isBookmarkManagerShown() {
        return (FirstFlag & 0x1000000000000l) == 0x1000000000000l;
    }

    public boolean setBookmarkManagerShown(boolean val) {
        updateFFAt(0x1000000000000l,val);
        return val;
    }
    public boolean isBookmarkManagerHideWithUI() {
        return (FirstFlag & 0x2000000000000l) != 0x2000000000000l;
    }

    public boolean setBookmarkManagerHideWithUI(boolean val) {
        updateFFAt(0x2000000000000l,!val);
        return val;
    }
    public boolean isBringbackBottombarFromHidden() {
        return (FirstFlag & 0x4000000000000l) == 0x4000000000000l;
    }

    public boolean setBringbackBottombarFromHidden(boolean val) {
        updateFFAt(0x4000000000000l,val);
        return val;
    }

    public boolean isFetchTransiantFocus() {
        return true;//(FirstFlag & 0x8000000000000l) == 0x4000000000000l;
    }

    public boolean setFetchTransientFocus(boolean val) {
        updateFFAt(0x8000000000000l,val);
        return val;
    }

	//0x8000000000000000l
    /////////////////////End First Flag////////////////////////////////////
    /////////////////////Start Second Flag////////////////////////////////////
    private static Long SecondFlag=null;
    public long getSecondFlag() {
        if(SecondFlag==null) {
            return SecondFlag=defaultReader.getLong("MSF",0);
        }
        return SecondFlag;
    }
    private void putSecondFlag(long val) {
        defaultReader.edit().putLong("MSF",SecondFlag=val).apply();
    }
    public void putSecondFlag() {
        putSecondFlag(SecondFlag);
    }
    public Long SecondFlag() {
        return SecondFlag;
    }
    private static void updateSFAt(int o, boolean val) {
        SecondFlag &= (~o);
        if(val) SecondFlag |= o;
        //defaultReader.edit().putInt("MFF",FirstFlag).commit();
    }
    private void updateSFAt(long o, boolean val) {
        SecondFlag &= (~o);
        if(val) SecondFlag |= o;
        //defaultReader.edit().putInt("MFF",FirstFlag).commit();
    }

    public boolean getLoopMusic() {
        return (SecondFlag & 0x1) == 0x1;
    }

    public boolean setLoopMusic(boolean val) {
        updateSFAt(0x1,val);
        return val;
    }
    public boolean getShaffle() {
        return (SecondFlag & 0x2) == 0x2;
    }

    public boolean setShaffle(boolean val) {
        updateSFAt(0x2,val);
        return val;
    }

    public boolean isDoubleTapDissmissUI() {
        return true;//(SecondFlag & 0x4) == 0x4;
    }

    public boolean setDoubleTapDissmissUI(boolean val) {
        updateSFAt(0x4,val);
        return val;
    }

    /** <b>DoubleTap Action</b>: <br> 0:pause<br> 1:zoom<br> 2:nothing*/
    public int getDoubleTapAction() {
        return 1;//(int) ((FirstFlag >> 3) & 3);
    }
    public int setDoubleTapAction(int val) {
        FirstFlag &= (~0x8);
        FirstFlag &= (~0x10);
        FirstFlag |= ((long)(val & 3)) << 3;
        return val;
    }

    /** <b>Rebound Scheme</b>: <br> 0:always rebound to matched state.
     * <br> 1:only rebound when videoview is small<br>
     *     2:only rebound when you cannot see videoview*/
    public int getRebounceScheme() {
        return 0;//(int) ((FirstFlag >> 5) & 3);
    }
    public int setRebounceScheme(int val) {
        FirstFlag &= (~0x20);
        FirstFlag &= (~0x40);
        FirstFlag |= ((long)(val & 3)) << 5;
        return val;
    }

    /** If the <b>rebound scheme</b> respects <b>MatchType</b>,
     * then if it determines that a rebound will occur,
     *      the rebound will <b>only</b> happen when the dimension of videoview along the axis specified by
     *      <b>MatchType</b> is smaller than expected.<br/>This allows for more flexible positioning of videoview.*/
    public boolean getRebounceRespectMatchType() {
        return true;//(SecondFlag & 0x80) == 0x80;
    }

    public boolean setRebounceRespectMatchType(boolean val) {
        updateSFAt(0x80,val);
        return val;
    }

    public boolean getUseLruDiskCache() {
        return true;//(SecondFlag & 0x100) == 0x100;
    }

    public boolean setUseLruDiskCache(boolean val) {
        updateSFAt(0x100,val);
        return val;
    }

    public boolean getSaveProjectToDB() {
        return true;//(SecondFlag & 0x200) == 0x200;
    }

    public boolean setSaveProjectToDB(boolean val) {
        updateSFAt(0x200,val);
        return val;
    }
    public boolean getUseOpensles() {
        return false;//(SecondFlag & 0x400) == 0x400;
    }

    public boolean setUseOpensles(boolean val) {
        updateSFAt(0x400,val);
        return val;
    }

    public boolean getDisableAndroidDisplay() {
        return false;//(SecondFlag & 0x800) == 0x800;
    }

    public boolean setDisableAndroidDisplay(boolean val) {
        updateSFAt(0x800,val);
        return val;
    }
    public boolean getUseCustomCrashCatcher() {
        return true;//(SecondFlag & 0x1000) == 0x1000;
    }

    public boolean setUseCustomCrashCatcher(boolean val) {
        updateSFAt(0x1000,val);
        return val;
    }

    public boolean getSilentExitBypassingSystem() {
        return (SecondFlag & 0x2000) != 0x2000;
    }

    public boolean setSilentExitBypassingSystem(boolean val) {
        updateSFAt(0x2000,!val);
        return val;
    }
    public boolean getLogToFile() {
        return (SecondFlag & 0x4000) != 0x4000;
    }

    public boolean setLogToFile(boolean val) {
        updateSFAt(0x4000,!val);
        return val;
    }
    public boolean getAutoNoPauseIfAudioOnly() {
        return true;//(SecondFlag & 0x8000) == 0x8000;
    }

    public boolean setAutoNoPauseIfAudioOnly(boolean val) {
        updateSFAt(0x8000,val);
        return val;
    }

    public boolean getDrawHighlightOnTextView() {
        return (SecondFlag & 0x10000) != 0x10000;
    }

    public boolean setDrawHighlightOnTextView(boolean val) {
        updateSFAt(0x10000,!val);
        return val;
    }

	public static boolean getUseGradientBackground() {
		return (SecondFlag & 0x20000) == 0x20000;
	}
	public boolean getUseGradientBackground(long SecondFlag) {
		return (SecondFlag & 0x20000) == 0x20000;
	}
	public static boolean setUseGradientBackground(boolean val) {
		updateSFAt(0x20000,val);
		return val;
	}
    /////////////////////End Second Flag////////////////////////////////////
    /////////////////////Start Third Flag////////////////////////////////////
    private static Long ThirdFlag=null;
    public long getThirdFlag() {
        if(ThirdFlag==null) {
            return ThirdFlag=defaultReader.getLong("MTF",0);//76+32768+65536
        }
        return ThirdFlag;
    }
    private void putThirdFlag(long val) {
        defaultReader.edit().putLong("MTF",ThirdFlag=val).commit();
    }
    public void putThirdFlag() {
        putThirdFlag(ThirdFlag);
    }
    public Long ThirdFlag() {
        return ThirdFlag;
    }
    private void updateTFAt(int o, boolean val) {
        ThirdFlag &= (~o);
        if(val) ThirdFlag |= o;
        //defaultReader.edit().putInt("MFF",FirstFlag).commit();
    }
    private void updateTFAt(long o, boolean val) {
        ThirdFlag &= (~o);
        if(val) ThirdFlag |= o;
        //defaultReader.edit().putInt("MFF",FirstFlag).commit();
    }

    final static int STATE_SHOWASACTION=1;
    final static int STATE_ENABLED=1;
    public boolean getUIStates(int id, int type){
        boolean ret=false;
        switch(id){
            case R.id.toolbar_action1://添加书签
                ret= (ThirdFlag & 0x1) !=0;
            break;
            case R.id.toolbar_action3://阅读模式
                ret= (ThirdFlag & 0x2) !=0;
            break;
            case R.id.toolbar_action4://双击
                ret= (ThirdFlag & 0x4) !=0;
            break;
            case R.id.toolbar_action5://允许缩放::回弹-单指滑动-滑动
                ret= (ThirdFlag & 0x8) !=0;
            break;
            case R.id.toolbar_action6://阅读模式
                ret= (ThirdFlag & 0x10) !=0;
            break;
            case R.id.toolbar_action7://上下切换
                ret= (ThirdFlag & 0x20) !=0;
            break;
            case R.id.toolbar_action8://控制面板
                ret= (ThirdFlag & 0x40) !=0;
            break;
            case R.id.toolbar_action9://字幕
                ret= (ThirdFlag & 0x80) !=0;
            break;
            case R.id.toolbar_action10://音轨
                ret= (ThirdFlag & 0x100) !=0;
            break;
            case R.id.toolbar_action11://硬件加速
                ret= (ThirdFlag & 0x200) !=0;
            break;
            case R.id.toolbar_action12://播放列表
                ret= (ThirdFlag & 0x400) !=0;
            break;
            case R.id.toolbar_action13://横屏
                ret= (ThirdFlag & 0x800) !=0;
            break;
            case R.id.toolbar_action14://删除文件
                ret= (ThirdFlag & 0x1000) !=0;
            break;
            case R.id.toolbar_action15://移动文件
                ret= (ThirdFlag & 0x2000) !=0;
            break;
            case R.id.toolbar_action16://重命名
                ret= (ThirdFlag & 0x4000) !=0;
            break;
            case R.id.toolbar_action17://文件信息
                ret= (ThirdFlag & 0x8000) !=0;
            break;
            case R.id.toolbar_action18://切割视频::切割模式，参数
                ret= (ThirdFlag & 0x10000) !=0;
            break;
            //case R.id.toolbar_action19://
            //break;
            case R.id.toolbar_action20://提取音频
                ret= (ThirdFlag & 0x20000) !=0;
            break;
            //case R.id.toolbar_action21://
            //break;
            case R.id.toolbar_action22://制作动图
                ret= (ThirdFlag & 0x40000) !=0;
            break;
            case R.id.toolbar_action23://打开...
                ret= (ThirdFlag & 0x80000) !=0;
            break;
            case R.id.toolbar_action24://标记
                ret= (ThirdFlag & 0x80000) !=0;
            break;
            case R.id.toolbar_action25://排序
                ret= (ThirdFlag & 0x80000) !=0;
            break;
        }
        return ret;
    }




    private final StringBuilder pathTo = new StringBuilder();//"/sdcard/PLOD/bmDBs/");
    public String rootPath;
    protected int pathToL = -1;//pathTo.toString().length();
    public static boolean isLarge;
    public DisplayMetrics dm;
    public StringBuilder pathTo(@NonNull Context context) {
        CMN.Log("pathTo");
        if(pathToL==-1) {
            String i = "/PLOD/video/";
            pathTo.setLength(0);
            pathTo.append(rootPath!=null?rootPath:context.getExternalFilesDir("").getAbsolutePath()).append(i);
            if(rootPath!=null)pathToL = rootPath.length()+i.length();
        }else
            pathTo.setLength(pathToL);
        return pathTo;
    }
    public StringBuilder pathToInternal(@NonNull Context context) {
        return pathTo(context).append("INTERNAL/");
    }
    public String pathToMain(@NonNull Context context) {
        return pathTo(context).toString().substring(0,pathToL-6);
    }

}