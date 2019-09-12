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

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.media.TimedText;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.DiscontinuityReason;
import com.google.android.exoplayer2.Player.VideoComponent;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.id3.ApicFrame;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.video.VideoListener;
import com.knziha.equalizer.EqualizerGroup;
import com.knziha.panorama.GLRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.Media.FileMediaDataSource;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

@SuppressWarnings("SpellCheckingInspection")
public class FilmCapsule implements org.videolan.libvlc.IVLCVout.OnNewVideoLayoutListener, EqualizerGroup.EqualizerListener {
    public VICMainActivity a;//TODO weakreference it, transfer FilmCapsule to a service.
    public VICMainAppOptions opt;
    private static final int SURFACE_TYPE_UNSPECIFIED = 0;
    private static final int SURFACE_TYPE_SURFACE_VIEW = 1;
    private static final int SURFACE_TYPE_TEXTURE_VIEW = 2;

    private FrameLayout contentFrame;
    public long currentfilesize;
    public long onStartSTime;
    private View mVideoView;
    SurfaceView mSurfaceView;
    TextureView mTextureView;

    private CompoundListener compoundListener;

    Object player;
    private Surface mSurface;
    private GLRenderer renderer;

	AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    org.videolan.libvlc.LibVLC mLibVLC;
    private Object mVLCLISTENER;
    private boolean mMediaPlayer_feiwu;

	public FilmCapsule(@NonNull FrameLayout _contentFrame, Activity context,VICMainAppOptions options) {
        contentFrame=_contentFrame;
        opt=options;
        initPanorama();
    }

    protected void PlayAt(String path) {
        if(path==null)return;
        attachViews();
        playMediaAtPath(path);//myFolder/coursera.mp4
        if(opt.getPanoramaMode() && opt.getPlayerType()==LV_player)
            a.IMPageCover.postDelayed(()->{playMediaAtPath(path);},100);//xxx
        a.mPlay.setImageResource(R.drawable.ic_pause_black_24dp);
        a.ispauseExpected=false;
    }

    public ArrayList<CachedFile> mPlaylist;
    public CachedFile currentFile;
    public void playMediaAtPath(String path) {
        boolean isReLoading=true;
        currentfilesize=0;
        if (currentFile != null) {
            if (path == null) {
                path = currentFile.getAbsolutePath();
                isReLoading=false;
            }else if (currentFile.getAbsolutePath().equals(path)) {
                isReLoading=false;
            }/*else if(mPlaylist!=null){
                mPlaylist.clear();
                mPlaylist=null;
            }*/
        }
        if(path==null) {
            a.show(R.string.path_not_exist, path);
            return;
        }
        if(!isReLoading){
            //if (a.currenT != null) {
            //    a.currenT.setText("00:00");
            //    a.totalT.setText("00:00");
            //}
        }else session_id_stamp=0;

		if(opt.getRespectAudioManager()) requestAudioFocus();
        currentFile = new CachedFile(path);
        String Alias = null;
        if(player instanceof MediaPlayer){
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            if(mMediaPlayer_feiwu){
                mMediaPlayer.setVolume(0,0);
                a.stopRudeTL();
                createPlayer(Native_player);
                attachViews();
                mMediaPlayer = (MediaPlayer) player;
                mMediaPlayer_feiwu=false;
            }
            AssetFileDescriptor asset_file=null;
            if (path.startsWith("/ASSET/")) {
                Integer id = CMN.AssetMap.get(path);
                if (id != null) {
                    Alias = a.getResources().getStringArray(R.array.stellarium)[id];
                    try {
                        asset_file=a.getAssets().openFd(currentFile.getAbsolutePath().substring(7));
                        a.isPlayingAsset = true;
                    } catch (IOException ignored) { }
                }
            }

            try {
                if(asset_file!=null) {
                    mMediaPlayer.setDataSource(asset_file.getFileDescriptor(),asset_file.getStartOffset(), asset_file.getLength());
                    asset_file.close();
                    CMN.Log("playing asset!!",asset_file.getFileDescriptor(),asset_file.getStartOffset(), asset_file.getLength());
                }else
                    mMediaPlayer.setDataSource(currentFile.getAbsolutePath());
                mMediaPlayer_feiwu=true;
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                if(!mMediaPlayer_feiwu){
                    compoundListener.onError(mMediaPlayer, 0, 0);
                }
            }
            //a.mSeekBar.setMax(mMediaPlayer.getDuration());
        }
        else if(player instanceof SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = ((SimpleExoPlayer)player);
            if(false){
                createPlayer(GuGe_player);
                attachViews();
                mMediaPlayer = (SimpleExoPlayer) player;
            }
            //mMediaPlayer.stop(false);
            Uri file_asset=null;
            MediaSource mediaSource=null;
            if (path.startsWith("/ASSET/")) {
                Integer id = CMN.AssetMap.get(path);
                if (id != null) {
                    Alias = a.getResources().getStringArray(R.array.stellarium)[id];
                    try {
                        file_asset=Uri.parse("asset:///"+currentFile.getAbsolutePath().substring(7));
                        mediaSource = new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory(a,"spx")).createMediaSource(file_asset);
                        a.isPlayingAsset = true;
                    } catch (Exception ignored) {
                }
                }
            }
            if(mediaSource==null){
                file_asset = Uri.fromFile(currentFile);
                mediaSource = new ProgressiveMediaSource.Factory(new FileDataSourceFactory(null)).createMediaSource(file_asset);
            }

            mMediaPlayer.prepare(mediaSource, true, true);
            mMediaPlayer.setPlayWhenReady(!a.ispauseExpected);
            SeekParameters seekstyle = new SeekParameters(1500,1500);
            mMediaPlayer.setSeekParameters(seekstyle);
            a.setValidationExpected(true);
            a.mSeekBar.setMax((int) mMediaPlayer.getDuration());
        }
        else if(player instanceof IjkMediaPlayer){
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            if(mMediaPlayer.feiwu){
                createPlayer(BZhan_player);
                attachViews();
                mMediaPlayer = (IjkMediaPlayer) player;
            }
            AssetFileDescriptor asset_file=null;
            if (path.startsWith("/ASSET/")) {
                Integer id = CMN.AssetMap.get(path);
                if (id != null) {
                    Alias = a.getResources().getStringArray(R.array.stellarium)[id];
                    try {
                        asset_file=a.getAssets().openFd(currentFile.getAbsolutePath().substring(7));
                        a.isPlayingAsset = true;
                    } catch (IOException ignored) {
                    }
                }
            }

            if(asset_file!=null){
                try {
                    mMediaPlayer.setDataSource(asset_file.getFileDescriptor());
                } catch (IOException ignored) {return;}
            }else{
                IMediaDataSource dataSource = null;
                try {
                    dataSource = new FileMediaDataSource(currentFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(dataSource==null){
                    return;
                }
                mMediaPlayer.setDataSource(dataSource);
            }
            mMediaPlayer.feiwu=true;
            mMediaPlayer.prepareAsync();
            a.mSeekBar.setMax((int) mMediaPlayer.getDuration());
            a.setValidationExpected(true);
        }
        else if(player instanceof org.videolan.libvlc.MediaPlayer) {
            org.videolan.libvlc.Media m=null;//= new Media(mLibVLC, path);
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            if (path.startsWith("/ASSET/")) {
                Integer id = CMN.AssetMap.get(path);
                if (id != null) {
                    Alias = a.getResources().getStringArray(R.array.stellarium)[id];
                    try {
                        m = new org.videolan.libvlc.Media(mLibVLC, a.getAssets().openFd(currentFile.getAbsolutePath().substring(7)));
                        a.isPlayingAsset = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (m == null)
                if (currentFile.exists()) {
                    m = new org.videolan.libvlc.Media(mLibVLC, path);
                    a.isPlayingAsset = false;
                }

            if (m == null) {
                a.show(R.string.path_not_exist, path);
                return;
            }

            if (opt.getHardWareDecaodeType() >= 1) {
                m.setHWDecoderEnabled(true, true);
                if (opt.getHardWareDecaodeType() == 1) {
                    //m.addOption(":no-mediacodec-dr");
                    m.addOption(":no-omxil-dr");
                }
                CMN.Log("硬解");
            } else
                m.setHWDecoderEnabled(false, false);
            m.addOption(":no-sub-autodetect-file");//useless auto subtitle file detection.
            if(opt.getVoiceOnly()) {m.addOption(":no-video");}
            if(!opt.getAccurateSeek()) {m.addOption(":input-fast-seek");}
            mMediaPlayer.setMedia(m);
            m.release();

            if (opt.isMute())
                mMediaPlayer.setVolume(0);

            if (a.ispauseExpected)
                a.mPlay.performClick();
            else
                mMediaPlayer.play();
            a.setValidationExpected(true);
            a.mSeekBar.setMax((int) mMediaPlayer.getLength());
            a.mSeekBar.setProgress((int) onStartSTime);
            a.setValidationExpected(true);
            if(opt.getDisableAndroidDisplay()){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				Bitmap bm = BitmapFactory.decodeFile(path, options);
				mMediaPlayer.setScale(2);
				a.onNewVideoViewLayout(-1,-1,-1,-1);
				CMN.Log(path, options.outHeight, bm);
			}
        }

        //CMN.Log("声音设置",a.mVolume,opt.getVolumeEnabled(),opt.isMute());
        if(a.drawerFragment!=null) {
            if (opt.getRateEnabled() && a.drawerFragment.mRate != 1)
                setRate((float) a.drawerFragment.mRate);
        }
        if(opt.isMute())
            setVolume(0);
        else if (opt.getVolumeEnabled())
            setVolume((float) a.mVolume);

        //a.mSeekBar.setProgress(0);

        if(isReLoading) {
            a.OnLoadingNewMedia(path, Alias);
        }
    }

    private void initPanorama() {
        if(renderer==null){
            renderer = new GLRenderer();
            renderer.mOnSurfaceReadyListener = surface -> {
                mSurface = surface;
                renderer.getSurfaceTexture().setOnFrameAvailableListener(surfaceTexture1 -> {
                    refreshPanorama();
                });
                if(opt.get_USE_SURFACE_VIEW()){
                    if (player instanceof IjkMediaPlayer) {
                        IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
                        mMediaPlayer.setSurface(mSurface);
                    } else if (player instanceof MediaPlayer) {
                        MediaPlayer mMediaPlayer = (MediaPlayer) player;
                        mMediaPlayer.setSurface(mSurface);
                    } else if (player instanceof SimpleExoPlayer) {
                        SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
                        mMediaPlayer.setVideoSurface(mSurface);
                    } else if (player instanceof org.videolan.libvlc.MediaPlayer) {
                        org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
                        if(opt.getPanoramaMode()) {
                            mMediaPlayer.getVLCVout().detachViews();
                            mMediaPlayer.getVLCVout().setVideoSurface(renderer.getSurfaceTexture());
                            mMediaPlayer.getVLCVout().attachViews(FilmCapsule.this);
                        }
                    }
                }
            };
        }
    }

    //susu
    final SurfaceHolder.Callback surfaceviewcallback = new SurfaceHolder.Callback() {
        @Override public void surfaceCreated(SurfaceHolder holder) {
            //CMN.Log("surface_view: surface  Created");
            if (opt.get_USE_SURFACE_VIEW() && !opt.getVoiceOnly()) {
                if(opt.getPanoramaMode()){
                    renderer.onSurfaceCreated(holder.getSurface(), a.getApplicationContext());
                    return;
                }
                mSurface = holder.getSurface();
                if (player instanceof IjkMediaPlayer) {
                    IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
                    mMediaPlayer.setDisplay(((SurfaceView) mVideoView).getHolder());
                } else if (player instanceof MediaPlayer) {
                    MediaPlayer mMediaPlayer = (MediaPlayer) player;
                    mMediaPlayer.setDisplay(holder);
                }
            }
        }
        @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //CMN.Log("surface_view: surface  surfaceChanged");
            if(opt.getPanoramaMode()){
                renderer.onSurfaceChanged(width, height);
            }
        }
        @Override public void surfaceDestroyed(SurfaceHolder holder) {
            //CMN.Log("surface_view: surface  Destroyed");
            a.mVideoWidthReq = true;
            mSurface.release();
            mSurface=null;
            if(opt.getPanoramaMode()){
                renderer.release();
            }
            if (player instanceof org.videolan.libvlc.MediaPlayer) {
                org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
                if(opt.getPanoramaMode()) {
                    mMediaPlayer.getVLCVout().detachViews();//xxx
                }
            }
        }
    };

    //tete
    final TextureView.SurfaceTextureListener textureviewcallback = new TextureView.SurfaceTextureListener() {
        @Override public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //CMN.Log("textuew_view: textuew  Created");//视图创建
            if(!opt.get_USE_SURFACE_VIEW() && !opt.getVoiceOnly()) {
                if(opt.getPanoramaMode()){
                    renderer.onSurfaceCreated(new Surface(surface), a.getApplicationContext());
                    renderer.onSurfaceChanged(width,height);
                    surface = renderer.getSurfaceTexture();
                    mSurface = new Surface(surface);
                }else
                    mSurface = new Surface(surface);

                if (player instanceof IjkMediaPlayer) {
                    IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
                    mMediaPlayer.setSurface(mSurface);
                } else if (player instanceof MediaPlayer) {
                    MediaPlayer mMediaPlayer = (MediaPlayer) player;
                    mMediaPlayer.setSurface(mSurface);
                } else if (player instanceof SimpleExoPlayer) {
                    SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
                    if(opt.getPanoramaMode()) mMediaPlayer.setVideoSurface(mSurface);
                } else if (player instanceof org.videolan.libvlc.MediaPlayer) {
                    org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
                    if(opt.getPanoramaMode()) {
                        mMediaPlayer.getVLCVout().setVideoSurface(surface);
                        mMediaPlayer.getVLCVout().attachViews(FilmCapsule.this);
                    }
                }
            }
        }
        @Override public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            //CMN.Log("textuew_view: textuew  SizeChanged");
            if(opt.getPanoramaMode()){
                renderer.onSurfaceChanged(width,height);
            }
        }
        @Override public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            //CMN.Log("textuew_view: textuew  Destroyed");
            a.mVideoWidthReq=true;
            mSurface.release();
            mSurface=null;
            if(opt.getPanoramaMode()){
                renderer.release();
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
            //CMN.Log("textuew_view: textuew  Updated");
            if(player instanceof MediaPlayer) {
                MediaPlayer mMediaPlayer = (MediaPlayer) player;
                //a.onTimeChanged(mMediaPlayer.getCurrentPosition());
            }
        }
    };

    public void enssureVideoFrame(int surfaceType){
        surfaceType = opt.getVoiceOnly()?SURFACE_TYPE_UNSPECIFIED:
                opt.get_USE_SURFACE_VIEW()?SURFACE_TYPE_SURFACE_VIEW:SURFACE_TYPE_TEXTURE_VIEW;
        switch (surfaceType) {
            case SURFACE_TYPE_UNSPECIFIED:
                if(mSurfaceView!=null && mSurfaceView.getParent()!=null) contentFrame.removeView(mSurfaceView);
                if(mTextureView!=null && mTextureView.getParent()!=null) contentFrame.removeView(mTextureView);
                mVideoView=null;
            return;
            case SURFACE_TYPE_TEXTURE_VIEW:
                if(mTextureView==null) {
                    mTextureView = new TextureView(contentFrame.getContext());
                    mTextureView.setSurfaceTextureListener(textureviewcallback);
                }
                if(mSurfaceView!=null && mSurfaceView.getParent()!=null) contentFrame.removeView(mSurfaceView);
                mVideoView = mTextureView;
            break;
            default:
                if(mSurfaceView==null) {
                    mSurfaceView = new LindeqinSurfaceView(contentFrame.getContext());
                    SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
                    //mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
                    mSurfaceHolder.addCallback(surfaceviewcallback);
                }
                if(mTextureView!=null && mTextureView.getParent()!=null) contentFrame.removeView(mTextureView);
                mVideoView = mSurfaceView;
            break;
        }
        if(mVideoView.getParent()==null){
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-2, -2);
            mVideoView.setLayoutParams(params);
            contentFrame.addView(mVideoView, 0);
        }
    }

    //todo getPlayer setPlayer

    public void setRate(float rate){
        if(player instanceof SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = ((SimpleExoPlayer)player);
            PlaybackParameters playbackParameters = new PlaybackParameters(rate, 1.0F);
            mMediaPlayer.setPlaybackParameters(playbackParameters);
        }else if(player instanceof org.videolan.libvlc.MediaPlayer) {
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.setRate(rate);
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
            mMediaPlayer.setSpeed(rate);
        }else if(player instanceof MediaPlayer) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                MediaPlayer mMediaPlayer = (MediaPlayer) player;
                PlaybackParams playbackParameters = new PlaybackParams();
                playbackParameters.setPitch(1.0f);
                playbackParameters.setSpeed(rate);
                try {
                  mMediaPlayer.setPlaybackParams(playbackParameters);
                } catch (Exception ignored) {}
            }
        }
    }

    public View getVideoView() {
        return mVideoView;
    }

    public void onResume() {
        if(!a.systemIntialized) return;
		if(!a.ispauseExpected && opt.getRespectAudioManager()) requestAudioFocus();
        if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = ((MediaPlayer) player);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaPlayer.seekTo((int) a.lastMediaTime, MediaPlayer.SEEK_CLOSEST);
            }else{
                mMediaPlayer.seekTo((int) a.lastMediaTime);
            }
            if(!a.ispauseExpected) {
                mMediaPlayer.start();
                a.setRudeTL();
            }
        }
        else if(player instanceof SimpleExoPlayer) {
            SimpleExoPlayer mMediaPlayer = ((SimpleExoPlayer) player);
            mMediaPlayer.seekTo(a.lastMediaTime);
            if(!a.ispauseExpected) {
                //setPosition(a.lastMediaPostion);
                mMediaPlayer.setPlayWhenReady(true);
                a.setRudeTL();
            }
        }
        else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
            if(!a.ispauseExpected) {
                mMediaPlayer.start();
                a.setRudeTL();
            }
        }
        else if(player instanceof  org.videolan.libvlc.MediaPlayer) {
			//todo fix scrappy screen noise on surfaceview 花屏待解决
             org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
             if(!mMediaPlayer.getVLCVout().areViewsAttached()) attachViews();
             if(!a.ispauseExpected) {mMediaPlayer.play();}
            //mMediaPlayer.setPosition(a.lastMediaPostion);// no need
         }
        if(a.ispauseExpected && opt.getPanoramaMode()){
            //refreshPanorama();
        }
    }
    
    public void onPause() {
		if(opt.getRespectAudioManager()) releaseAudioFocus();
        if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = ((MediaPlayer) player);
            a.lastMediaTime = mMediaPlayer.getCurrentPosition();
            if(!a.ispauseExpected)
                mMediaPlayer.pause();
        }
        else if(player instanceof SimpleExoPlayer) {
            SimpleExoPlayer mMediaPlayer = ((SimpleExoPlayer) player);
            a.lastMediaTime = mMediaPlayer.getCurrentPosition();
			if(!a.ispauseExpected)
				mMediaPlayer.setPlayWhenReady(false);
			}
        else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
            a.lastMediaTime = mMediaPlayer.getCurrentPosition();
            if(!a.ispauseExpected)
            mMediaPlayer.pause();
        }
        else if(player instanceof  org.videolan.libvlc.MediaPlayer) {
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            //Log.e("PlayService","onPause!");
            //CMN.showTT("onPause!");
            //if(passFlag>=2) {//weird
            if (mMediaPlayer != null) {// && mMediaPlayer.getVLCVout().areViewsAttached()
                a.lastMediaPostion = mMediaPlayer.getPosition();
                mMediaPlayer.pause();
                //mMediaPlayer.getVLCVout().detachViews();
                a.needReAttach = true;
            } else {
                a.needReAttach = false;
            }
            //if(quickViewerMode)
            //    dumpSettings();
        }
    }

    // Internal methods.
    private void updateForCurrentTrackSelections(boolean isNewPlayer) {
        if(player instanceof SimpleExoPlayer){
                SimpleExoPlayer mMediaPlayer = ((SimpleExoPlayer)player);
                if (mMediaPlayer == null || mMediaPlayer.getCurrentTrackGroups().isEmpty()) {
                  return;
                }
                TrackSelectionArray selections = mMediaPlayer.getCurrentTrackSelections();
                for (int i = 0; i < selections.length; i++) {
                  if (mMediaPlayer.getRendererType(i) == C.TRACK_TYPE_VIDEO && selections.get(i) != null) {
                      // Video enabled so artwork must be hidden. If the shutter is closed, it will be opened in
                      // onRenderedFirstFrame().
                      return;
                  }
                }
        }

//      for (int i = 0; i < selections.length; i++) {
//        TrackSelection selection = selections.get(i);
//        if (selection != null) {
//          for (int j = 0; j < selection.length(); j++) {
//            Metadata metadata = selection.getFormat(j).metadata;
//            if (metadata != null && setArtworkFromMetadata(metadata)) {
//              return;
//            }
//          }
//        }
//      }
    }

    private Drawable RetrieveArkSpaceShipCoatFromMeta(Metadata metadata) {
        for (int i = 0; i < metadata.length(); i++) {
        Metadata.Entry metadataEntry = metadata.get(i);
        if (metadataEntry instanceof ApicFrame) {
            byte[] bitmapData = ((ApicFrame) metadataEntry).pictureData;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
            return new BitmapDrawable(mVideoView.getContext().getResources(), bitmap);
        }
        }
        return null;
    }

//        private boolean isDpadKey(int keyCode) {
            public boolean needRawTimeListener(){
                return PlayerType!=LV_player;
            }

    public void PreSwitch() {//revert player
        int PlayerType=-1;
        if(player instanceof MediaPlayer){
            PlayerType=Native_player;
        }else if(player instanceof ExoPlayer){
            PlayerType=GuGe_player;
        }else if(player instanceof IjkMediaPlayer){
            PlayerType=BZhan_player;
        }else if(player instanceof org.videolan.libvlc.MediaPlayer){
            PlayerType=LV_player;
        }
        if(PlayerType>=0 && PlayerType!=opt.getPlayerType()){
            refreshRenderViews();
            createPlayer(opt.getPlayerType());
            attachViews();
        }
    }

    int PlayerType= GuGe_player;
    public final static int Native_player=0;
    public final static int GuGe_player=1;
    public final static int LV_player =2;
    public final static int BZhan_player=3;
    public void createPlayer(int _PlayerType) {
        PlayerType=_PlayerType;
        if(compoundListener ==null)
            compoundListener = new CompoundListener();
        if(player!=null) {
            if(player instanceof MediaPlayer){
                MediaPlayer mMediaPlayer = (MediaPlayer) player;
                CompoundPolymerizer.Register(Native_player, mMediaPlayer, null);
                mMediaPlayer.stop();
                mMediaPlayer.setSurface(null);
                mMediaPlayer.setDisplay(null);
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }
            else if(player instanceof  SimpleExoPlayer){
            	exo_audiosessionid_cache=0;//have to clear it!
                SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
                VideoComponent oldVideoComponent = mMediaPlayer.getVideoComponent();
                if (oldVideoComponent != null) {
                    oldVideoComponent.removeVideoListener(compoundListener);
                    if (mVideoView instanceof TextureView) {
                        oldVideoComponent.clearVideoTextureView((TextureView) mVideoView);
                    } else if (mVideoView instanceof SurfaceView) {
                        oldVideoComponent.clearVideoSurfaceView((SurfaceView) mVideoView);
                    }
                }
                mMediaPlayer.removeListener(compoundListener);
                Player.TextComponent oldTextComponent = mMediaPlayer.getTextComponent();
                if (oldTextComponent != null) {
                    oldTextComponent.removeTextOutput(compoundListener);
                }
                mMediaPlayer.release();
            }
            else if(player instanceof IjkMediaPlayer){
                IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
                CompoundPolymerizer.Register(BZhan_player, mMediaPlayer, null);
                mMediaPlayer.stop();
                //mMediaPlayer.setSurface(null);
                mMediaPlayer.setDisplay(null);
                mMediaPlayer.resetListeners();
                mMediaPlayer.reset();
                mMediaPlayer.release();
            }
            else if(player instanceof  org.videolan.libvlc.MediaPlayer){
                org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        }
        if(mSurface!=null && !mSurface.isValid()){
            //releasing surfaceview!
            refreshRenderViews();
        }
        if(equalizer!=null){
            if(equalizer instanceof  Equalizer){
                Equalizer mEqualizer= (Equalizer) equalizer;
                try {
                    mEqualizer.setEnabled(false);
                    mEqualizer.release();
                    equalizer=null;
                } catch (Exception ignored) {}
            }
        }
        switch (PlayerType){
            case Native_player:{
                MediaPlayer mNMediaPlayer = new MediaPlayer();
                player = mNMediaPlayer;
                CompoundPolymerizer.Register(Native_player, mNMediaPlayer, compoundListener);
                break;
            }
            case BZhan_player:{
                IjkMediaPlayer.loadLibrariesOnce(null);
                IjkMediaPlayer mIjkMediaPlayer = new IjkMediaPlayer();
                IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT);//useless
                boolean getUsingMediaCodec=false;
                String pixelFormat = "";
                if (getUsingMediaCodec) {
                    mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
                    mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
                    mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
                } else {
                    mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
                }
                mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", opt.getUseOpensles()?1:0);
                if (TextUtils.isEmpty(pixelFormat)) {
                    mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
                } else {
                    mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat);
                }
                mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", -16);
                mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_idct", -16);
                mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
//                    mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
                mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
                if(opt.getVoiceOnly())mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "nodisp", 1);

                if(onStartSTime!=0) mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "seek-at-start", onStartSTime);
                onStartSTime=0;
                if(opt.getAccurateSeek()) mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);

                player = mIjkMediaPlayer;

                //mIjkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                //mIjkMediaPlayer.setScreenOnWhilePlaying(true);
                CompoundPolymerizer.Register(BZhan_player, mIjkMediaPlayer, compoundListener);
                break;
            }
            case GuGe_player:{
                DefaultTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
                trackSelector.setParameters(new DefaultTrackSelector.ParametersBuilder().build());

                boolean useExtensionRenderers = false;
                boolean preferExtensionRenderer = false;
                int extensionRendererMode =useExtensionRenderers? (preferExtensionRenderer
                        ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON) : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;

                RenderersFactory refac = new DefaultRenderersFactory(a.getApplicationContext())
                        .setExtensionRendererMode(extensionRendererMode);
                SimpleExoPlayer mMediaPlayer = ExoPlayerFactory.newSimpleInstance(a, refac , trackSelector, (DrmSessionManager) null);

                player = mMediaPlayer;
                //mMediaPlayer.addAnalyticsListener(new EventLogger(trackSelector));
                //updateForCurrentTrackSelections(true);
                Player.TextComponent newTextComponent = mMediaPlayer.getTextComponent();
                if (newTextComponent != null) {
                    newTextComponent.addTextOutput(compoundListener);
                }
                mMediaPlayer.addListener(compoundListener);
                break;
            }
            case LV_player:{
                ArrayList<String> options = new ArrayList<>();
				if(!opt.getDisableAndroidDisplay())
                	options.add("--vout=android-display");
                if(opt.getUseOpensles())
                	options.add("--aout=opensles");

                //options.add("--android-display-chroma");
                //options.add("RV16");

                //options.add("--audio-time-stretch"); // time stretching

                //options.add("--aout=android_audiotrack");
                options.add("-v");
                mLibVLC = new org.videolan.libvlc.LibVLC(a, options);
                org.videolan.libvlc.MediaPlayer  mVlanMediaPlayer = new org.videolan.libvlc.MediaPlayer(mLibVLC);
                if(mVLCLISTENER==null) {
                    org.videolan.libvlc.MediaPlayer.EventListener mPlayerListener = event -> {
                        if(player instanceof org.videolan.libvlc.MediaPlayer){
                            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
                            switch (event.type) {
                                case org.videolan.libvlc.MediaPlayer.Event.EncounteredError:
                                    a.OnError();
                                    break;
                                case org.videolan.libvlc.MediaPlayer.Event.TimeChanged:
                                    a.onTimeChanged(mMediaPlayer.getTime());
                                    break;
                                case org.videolan.libvlc.MediaPlayer.Event.EndReached:
                                    if (mMediaPlayer.getLength() > 0) {
                                        if (opt.get_USE_SURFACE_VIEW())
                                            a.IMPageCover.post(() -> {
                                                if (!a.isPlayingAsset) {
                                                    mMediaPlayer.stop();
                                                    mMediaPlayer.play();
                                                } else {
                                                    playMediaAtPath(null);
                                                }
                                            });
                                        else {
                                            if (!a.isPlayingAsset) {
                                                mMediaPlayer.stop();
                                                mMediaPlayer.play();
                                            } else {
                                                playMediaAtPath(null);
                                            }
                                        }
                                    }
                                    break;
                                case org.videolan.libvlc.MediaPlayer.Event.Playing:
                                    //CMN.Log("vlc_Playing",mIjkMediaPlayer.getVolume());
                                    a.totalT.setText(CMN.FormTime((int) mMediaPlayer.getLength(), 1));
                                    if (onStartSTime != 0) {
                                        mMediaPlayer.setTime(onStartSTime);
                                        onStartSTime = 0;
                                    }
                                    //if (IMPageCover.getVisibility() == View.VISIBLE) {
                                    //CMN.Log("removeImageCover progress" + progress);
                                    //removeImageCover();
                                    //}
                                    break;
                                case org.videolan.libvlc.MediaPlayer.Event.PositionChanged:
                                    //CMN.Log("vlc_PositionChanged" + mIjkMediaPlayer.getVolume());
                                    break;
                                case org.videolan.libvlc.MediaPlayer.Event.Vout:
                                    //CMN.Log("vlc_Vout",mIjkMediaPlayer.getVolume());
                                    //mIjkMediaPlayer.setSpuTrack(-1);
                                    a.OnPrepared(true);
                                    if (opt.isMute())
                                        mMediaPlayer.setVolume(0);
                                    else if (opt.getVolumeEnabled())
                                        mMediaPlayer.setVolume((int) (a.mVolume * 100));
                                    if (a.IMPageCover.getVisibility() == View.VISIBLE) {
                                        //CMN.Log("removeImageCover progress" + progress);
                                        //removeImageCover();
                                    }
                                    //CMN.show("posC "+mIjkMediaPlayer.getPosition()+":"+mIjkMediaPlayer.getTime());
                                    break;
                                case org.videolan.libvlc.MediaPlayer.Event.Buffering:
                                    //CMN.Log("vlc_Buffering",mIjkMediaPlayer.getVolume(),mIjkMediaPlayer.getPlayerState());
                                    //CMN.Log("Buffering");
                                    if (a.isPlayingAsset)
                                        if (mMediaPlayer.getPlayerState() == 6)
                                            playMediaAtPath(null);
                                    break;
                                default:
                                    break;
                            }
                        }
                    };
                    mVLCLISTENER=mPlayerListener;
                }
                mVlanMediaPlayer.setEventListener((org.videolan.libvlc.MediaPlayer.EventListener)mVLCLISTENER);
                //mIjkMediaPlayer.setAudioOutput("opensles");
                //mIjkMediaPlayer.setAudioOutput("android_audiotrack");
                mVlanMediaPlayer.setScale(0);
                mVlanMediaPlayer.setVideoScale(org.videolan.libvlc.MediaPlayer.ScaleType.SURFACE_FIT_SCREEN);
                mVlanMediaPlayer.setAspectRatio(null);
                player = mVlanMediaPlayer;
                break;
            }
        }
    }

    void refreshRenderViews() {
        if(mSurface!=null){
            mSurface.release();
        }
        contentFrame.removeView(getVideoView());
        if(mTextureView!=null){
            mTextureView=null;
        }
        if(mSurfaceView!=null){
            mSurfaceView=null;
        }
        mSurface=null;
        //enssureVideoFrame(0);
    }

    ControlDispatcher controlDispatcher = new com.google.android.exoplayer2.DefaultControlDispatcher();
    public void play() {
        if(player instanceof  MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            mMediaPlayer.start();
        }else if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.play();
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            mMediaPlayer.setPlayWhenReady(true);
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
            mMediaPlayer.start();
        }
    }

    boolean isPlaying() {
        if(player instanceof  org.videolan.libvlc.MediaPlayer) {
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            return mMediaPlayer.isPlaying();
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            return (mMediaPlayer.getPlaybackState() != Player.STATE_IDLE && mMediaPlayer.getPlaybackState() != Player.STATE_ENDED && mMediaPlayer.getPlayWhenReady());
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
            return mMediaPlayer.isPlaying();
        }else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void attachViews() {
        if(opt.getVoiceOnly())
            return;
        enssureVideoFrame(opt.get_USE_SURFACE_VIEW()?0:1);
        if(player instanceof MediaPlayer){
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            if(mSurface!=null && mSurface.isValid())
                mMediaPlayer.setSurface(mSurface);
            else if(!opt.getPanoramaMode()) {
                if (mVideoView instanceof TextureView) {
                    if (mTextureView.getSurfaceTexture() != null)
                        mMediaPlayer.setSurface(mSurface = new Surface(mTextureView.getSurfaceTexture()));
                } else if (mVideoView instanceof SurfaceView) {
                    SurfaceView mSurfaceView = ((SurfaceView) mVideoView);
                    mSurfaceView.getHolder().setFormat(PixelFormat.RGBX_8888);
                    mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }
            }
        }
        else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            VideoComponent newVideoComponent = mMediaPlayer.getVideoComponent();
            if(opt.getPanoramaMode()) {
                if(mSurface!=null && mSurface.isValid())
                    mMediaPlayer.setVideoSurface(mSurface);
            }else if (newVideoComponent != null && !opt.getPanoramaMode()) {
                if (mVideoView instanceof TextureView) {
                    newVideoComponent.setVideoTextureView((TextureView) mVideoView);
                } else if (mVideoView instanceof SurfaceView) {
                    newVideoComponent.setVideoSurfaceView((SurfaceView) mVideoView);
                }
                newVideoComponent.addVideoListener(compoundListener);
            }
            //Player.TextComponent newTextComponent = mMediaPlayer.getTextComponent();
            //if (newTextComponent != null) {
            //    newTextComponent.addTextOutput(compoundListener);
            //}
        }
        else if(player instanceof IjkMediaPlayer){
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            if(mSurface!=null && mSurface.isValid())
                mMediaPlayer.setSurface(mSurface);
            else if(!opt.getPanoramaMode()) {
                if (mVideoView instanceof TextureView) {
                    if(mTextureView.getSurfaceTexture() != null)
                        mMediaPlayer.setSurface(mSurface = new Surface(mTextureView.getSurfaceTexture()));
                }else if (mVideoView instanceof SurfaceView){
                    mMediaPlayer.setDisplay(((SurfaceView) mVideoView).getHolder());
                }
            }
        }
        else if(player instanceof org.videolan.libvlc.MediaPlayer) {
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            if(!opt.getPanoramaMode()) {
                if (mVideoView instanceof TextureView) {
                    mMediaPlayer.getVLCVout().setVideoView((TextureView) mVideoView);
                } else {
                    mMediaPlayer.getVLCVout().setVideoView((SurfaceView) mVideoView);
                }
                mMediaPlayer.getVLCVout().attachViews(this);
            }else if(mSurface!=null && mSurface.isValid()){
                mMediaPlayer.getVLCVout().setVideoSurface(renderer.getSurfaceTexture());
                mMediaPlayer.getVLCVout().attachViews(this);
            }
        }
    }

    /**
     * detach on the run time may want recreation of our player
     * @return whether recreation is wanted*/
    public boolean detachRuninngView() {
        if(player instanceof org.videolan.libvlc.MediaPlayer) {
            if(true) return true;
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            if(mMediaPlayer.getVLCVout().areViewsAttached())
                mMediaPlayer.getVLCVout().detachViews();
        }
        else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            VideoComponent newVideoComponent = mMediaPlayer.getVideoComponent();
            if (newVideoComponent != null) {
                newVideoComponent.setVideoSurfaceView(null);
            }
        }
        else if(player instanceof IjkMediaPlayer){
            if(true) return true;
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            mMediaPlayer.setSurface(null);
        }
        else if(player instanceof MediaPlayer){
            if(true) return true;
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            mMediaPlayer.setSurface(null);
        }
        if(mVideoView!=null){
            if(mVideoView instanceof TextureView &&
                    ((TextureView)mVideoView).getSurfaceTexture()!=null){
                ((TextureView)mVideoView).setSurfaceTextureListener(null);
                ((TextureView)mVideoView).getSurfaceTexture().release();
            }
            if(mVideoView instanceof SurfaceView&&
                    ((SurfaceView)mVideoView).getHolder()!=null&&
                    ((SurfaceView)mVideoView).getHolder().getSurface()!=null){
                ((SurfaceView)mVideoView).getHolder().removeCallback(surfaceviewcallback);
                ((SurfaceView)mVideoView).getHolder().getSurface().release();
            }
            contentFrame.removeView(mVideoView);
            mVideoView=null;
        }
        return false;
    }

    public void deleteVideoView(){
        if(mTextureView!=null){
            mTextureView.setSurfaceTextureListener(null);
            if(mTextureView.getSurfaceTexture()!=null)mTextureView.getSurfaceTexture().release();
        }
        if(mSurfaceView!=null){
            mSurfaceView.getHolder().removeCallback(surfaceviewcallback);
            if(mSurfaceView.getHolder().getSurface()!=null)mSurfaceView.getHolder().getSurface().release();
        }
        if(mVideoView!=null){
            try {
                contentFrame.removeView(mVideoView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mVideoView=null;
        }
        if(mSurface!=null){
            mSurface.release();
            mSurface=null;
        }
    }

    public void pause() {
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.pause();
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            mMediaPlayer.setPlayWhenReady(false);
        }else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            mMediaPlayer.pause();
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.stop();
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            mMediaPlayer.stop(true);
        }else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            mMediaPlayer.stop();
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
            mMediaPlayer.stop();
        }
    }

    public void requestAudioFocus() {
    	if(opt.isMute()) return;
		if(mOnAudioFocusChangeListener==null)
		mOnAudioFocusChangeListener= focusChange -> {
			switch (focusChange){
				case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				case AudioManager.AUDIOFOCUS_LOSS:
					pause();
					if(a!=null){
						a.mPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
						a.ispauseExpected = true;
					}
				break;
				case AudioManager.AUDIOFOCUS_GAIN:
					if(!isPlaying()){
						play();
						if(a!=null){
							a.mPlay.setImageResource(R.drawable.ic_pause_black_24dp);
							a.ispauseExpected = false;
						}
					}
				break;
			}
		};
		AudioManager am = (AudioManager)a.getSystemService(Context.AUDIO_SERVICE);
		if (am != null)
			am.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
					opt.isFetchTransiantFocus()?AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
							AudioManager.AUDIOFOCUS_GAIN
					);
	}

	public void releaseAudioFocus() {
		if(mOnAudioFocusChangeListener==null) return;
		AudioManager am = (AudioManager)a.getSystemService(Context.AUDIO_SERVICE);
		if (am != null)
			am.abandonAudioFocus(mOnAudioFocusChangeListener);
	}

    void switch_play_state() {
        if(player instanceof  org.videolan.libvlc.MediaPlayer) {
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            if (mMediaPlayer == null) return;
            //CMN.Log(mMediaPlayer.getMedia(),mMediaPlayer.getVLCVout().areViewsAttached(),mMediaPlayer.getPlayerState());
            if (!a.isTracking) {
                //暂停之
                if (mMediaPlayer.isPlaying()) {
                    //CMN.Log("play_botton_clicked, pausing... ");
					if(opt.getRespectAudioManager()) releaseAudioFocus();
                    mMediaPlayer.pause();
                    mMediaPlayer.setAudioOutput("opensles");
                    a.mPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    a.ispauseExpected = true;
                }
                //播放之
                else {//&&mMediaPlayer.getTime()!=mMediaPlayer.getLength()
                    //CMN.Log("play_botton_clicked, playing... ");
                    //if(mMediaPlayer.getTime()!=mMediaPlayer.getLength()){
                    //mMediaPlayer.setAudioDelay(0);
					if(opt.getRespectAudioManager()) requestAudioFocus();
                    if (a.isPlayingAsset && mMediaPlayer.getPlayerState() == 6) {
                        playMediaAtPath(null);
                    }
                    if (opt.rewindOnPause() && a.capturedLPOC != -1)
                        mMediaPlayer.setPosition(a.capturedLPOC);
                    else if (opt.rewindOnReplay())
                        mMediaPlayer.setPosition(mMediaPlayer.getPosition());
                    mMediaPlayer.play();
                    //mMediaPlayer.setAudioOutput("opensles");
                    a.mPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                    //mPlay.setBackgroundColor(Color.parseColor("#aa5655FD"));
                    a.ispauseExpected = false;
                    //}else{
                    //    mMediaPlayer.getVLCVout().detachRuninngView();
                    //    attachViews();
                    //    mMediaPlayer.play();
                    //}
                }
            }
            a.capturedLPOC = -1;
            //mMediaPlayer.getMedia().addOption(":no-audio");
            //CMN.Log(mMediaPlayer.setAudioTrack(-1));
        }
        else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            if (!a.isTracking) {
                //暂停之
                boolean isPlaying = (mMediaPlayer.getPlaybackState() != Player.STATE_IDLE && mMediaPlayer.getPlaybackState() != Player.STATE_ENDED && mMediaPlayer.getPlayWhenReady());
                if (isPlaying) {
                    //CMN.Log("play_botton_clicked, pausing... ");
					if(opt.getRespectAudioManager()) releaseAudioFocus();
                    mMediaPlayer.setPlayWhenReady(false);
                    a.mPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    a.ispauseExpected = true;
                }else {
					if(opt.getRespectAudioManager()) requestAudioFocus();
                    if (a.isPlayingAsset && mMediaPlayer.getPlaybackState() == Player.STATE_IDLE) {
                        playMediaAtPath(null);
                    }
                    //CMN.Log("play_botton_clicked, playing... ");
                    mMediaPlayer.setPlayWhenReady(true);
                    a.mPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                    a.ispauseExpected = false;
                }
            }
        }
        else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
            if (mMediaPlayer.isPlaying()) {
                //CMN.Log("play_botton_clicked, pausing... ");
				if(opt.getRespectAudioManager()) releaseAudioFocus();
                mMediaPlayer.pause();
                a.mPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                a.ispauseExpected = true;
            }else {
				if(opt.getRespectAudioManager()) requestAudioFocus();
                if (a.isPlayingAsset && !mMediaPlayer.isPlayable()) {
                    playMediaAtPath(null);
                }
                //CMN.Log("play_botton_clicked, playing... ");
                mMediaPlayer.start();
                a.mPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                a.ispauseExpected = false;
            }
        }
        else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            if (mMediaPlayer.isPlaying()) {
                //CMN.Log("play_botton_clicked, pausing... ");
				if(opt.getRespectAudioManager()) releaseAudioFocus();
                mMediaPlayer.pause();
                a.mPlay.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                a.ispauseExpected = true;
            }else {
                //CMN.Log("play_botton_clicked, playing... ");
				if(opt.getRespectAudioManager()) requestAudioFocus();
                mMediaPlayer.start();
                a.mPlay.setImageResource(R.drawable.ic_pause_black_24dp);
                a.ispauseExpected = false;
            }
        }
        if(a.ispauseExpected){
            a.stopRudeTL();
            a.tracksyncstarttime=System.currentTimeMillis();
        }else{
            if(needRawTimeListener())a.setRudeTL();
        }
    }

    public void setPosition(float position) {
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.setPosition(position);
            a.TimeExpection = (long) (position*mMediaPlayer.getLength());
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            mMediaPlayer.seekTo(a.TimeExpection=(long) (position*1.0/100 * mMediaPlayer.getDuration()));
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            mMediaPlayer.seekTo(a.TimeExpection=(long) (position*1.0/100 * mMediaPlayer.getDuration()));
        }else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaPlayer.seekTo((int) (a.TimeExpection=(long) (position*1.0/100 * mMediaPlayer.getDuration())), MediaPlayer.SEEK_CLOSEST);
            }else{
                mMediaPlayer.seekTo((int) (a.TimeExpection=(long) (position*1.0/100 * mMediaPlayer.getDuration())));
            }
        }
    }

    public void setTime(long timeMs) {
        a.TimeExpection = timeMs;
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.setTime(timeMs);
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            mMediaPlayer.seekTo(timeMs);
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            mMediaPlayer.seekTo(timeMs);
        }else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mMediaPlayer.seekTo((int) timeMs, MediaPlayer.SEEK_CLOSEST);
            }else{
                mMediaPlayer.seekTo((int) timeMs);
            }
        }
    }

    public long getTime() {
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            try {
                if(org.videolan.libvlc.LibVLC.sLoaded)
                    return mMediaPlayer.getTime();
            } catch (Exception e) {}
            return 0;
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            return mMediaPlayer.getCurrentPosition();
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            return mMediaPlayer.getCurrentPosition();
        }else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;//TODO IllegalStateException
            try {
                return mMediaPlayer.getCurrentPosition();
            } catch (Exception e) {}
        }
        return 0;
    }

    public float getPosition() {
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            return mMediaPlayer.getPosition();
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            return mMediaPlayer.getCurrentPosition()*1.f/mMediaPlayer.getDuration();
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            return mMediaPlayer.getCurrentPosition()*1.f/mMediaPlayer.getDuration();
        }else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            return mMediaPlayer.getCurrentPosition()*1.f/mMediaPlayer.getDuration();
        }
        return 0;
    }

    public long getLength() {
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            return mMediaPlayer.getLength();
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            return mMediaPlayer.getDuration();
        }else if(player instanceof IjkMediaPlayer) {
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            return mMediaPlayer.getDuration();
        }else if(player instanceof MediaPlayer) {
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            //CMN.Log("mMediaPlayer.getDuration()",mMediaPlayer.getDuration());
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public void onLaunch(String runmed) {
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            enssureVideoFrame(0);
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
//            if(opt.get_USE_SURFACE_VIEW()){
//                PlayAt(runmed);
//            }else{
//                //PlayAt(runmed);
//                //CMN.Log("posting initial play...");
//                mTextureView.postDelayed(TextureInitialRun,100);
//            }
            PlayAt(runmed);
        }else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            PlayAt(runmed);
        }else if(player instanceof  IjkMediaPlayer){
            PlayAt(runmed);
        }else if(player instanceof  MediaPlayer){
            PlayAt(runmed);
        }
    }

    public void setVolume(float expectedVolume) {
        if(opt.isMute())
            expectedVolume=0;
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.setVolume((int) (expectedVolume*100));
        }
        else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            mMediaPlayer.setVolume(expectedVolume);
        }
        else if(player instanceof  IjkMediaPlayer){
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            mMediaPlayer.setVolume(expectedVolume,expectedVolume);
        }
        else if(player instanceof  MediaPlayer){
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            mMediaPlayer.setVolume(expectedVolume,expectedVolume);
        }
    }

    public void onClose() {
    	if(opt.getRespectAudioManager()) releaseAudioFocus();
        if (currentFile != null)
            opt.defaultputter().putString("lastRem", currentFile.getAbsolutePath()).commit();
        if(player instanceof  org.videolan.libvlc.MediaPlayer) {
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            CompoundPolymerizer.Register(Native_player, mMediaPlayer, null);
            mMediaPlayer.setVolume(0);
            mMediaPlayer.pause();
            mMediaPlayer.stop();
            mMediaPlayer.detachViews();
            mMediaPlayer.release();
            if (mMediaPlayer.getMedia() != null)
                mMediaPlayer.getMedia().release();
            mLibVLC.release();
        }
        else if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            mMediaPlayer.stop(true);
            mMediaPlayer.setVideoSurface(null);
            VideoComponent oldVideoComponent = mMediaPlayer.getVideoComponent();
            if (oldVideoComponent != null) {
                oldVideoComponent.removeVideoListener(compoundListener);
                if (mVideoView instanceof TextureView) {
                    oldVideoComponent.clearVideoTextureView((TextureView) mVideoView);
                } else if (mVideoView instanceof SurfaceView) {
                    oldVideoComponent.clearVideoSurfaceView((SurfaceView) mVideoView);
                }
            }
            mMediaPlayer.removeListener(compoundListener);
            Player.TextComponent oldTextComponent = mMediaPlayer.getTextComponent();
            if (oldTextComponent != null) {
                oldTextComponent.removeTextOutput(compoundListener);
            }
            mMediaPlayer.release();
        }
        else if(player instanceof  IjkMediaPlayer){
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            CompoundPolymerizer.Register(BZhan_player, mMediaPlayer, null);
            mMediaPlayer.stop();
            mMediaPlayer.resetListeners();
            mMediaPlayer.reset();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        else if(player instanceof  MediaPlayer){
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }
        if(renderer!=null)
            renderer.Destroy();
        if(mTextureView!=null){
            mTextureView.setSurfaceTextureListener(null);
            if(mTextureView.getSurfaceTexture()!=null)mTextureView.getSurfaceTexture().release();
        }
        if(mSurfaceView!=null){
            mSurfaceView.getHolder().removeCallback(surfaceviewcallback);
            if(mSurfaceView.getHolder().getSurface()!=null)mSurfaceView.getHolder().getSurface().release();
        }
        contentFrame.removeView(getVideoView());
        contentFrame=null;
        mVideoView=null;
        a=null;
    }

    public @Nullable Bitmap getVideoBitmap() {
        Bitmap ret = null;
        if(opt.get_USE_SURFACE_VIEW()){
            //ret =  mSurfaceView.getHolder().getSurface().
            // return ((SurfaceViewmy)mSurfaceView).dumpView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                final Bitmap bitmap = Bitmap.createBitmap(mSurfaceView.getWidth(), mSurfaceView.getHeight(),
                        Bitmap.Config.ARGB_8888);
                final HandlerThread handlerThread = new HandlerThread("PixelCopier");
                handlerThread.start();
                PixelCopy.request(mSurfaceView, bitmap, copyResult -> {
                    if (copyResult == PixelCopy.SUCCESS) {
                    } else {
                        Toast toast = Toast.makeText(a, "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                        toast.show();
                    }
                    handlerThread.quitSafely();
                }, new Handler(handlerThread.getLooper()));
                return bitmap;
            }
        }else
            ret = mTextureView.getBitmap();
        return ret;
    }

    @Override
    public void onNewVideoLayout(org.videolan.libvlc.IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        if(!opt.getDisableAndroidDisplay())
    		a.onNewVideoViewLayout(width, height, visibleWidth, visibleHeight);
    }

    public void refreshPanorama() {
        try {
            renderer.onDrawFrame();//todo RuntimeException: glVertexAttribPointer maPosition: glError 1285
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getPanoramaZoomInStart() {
        return renderer.zoomIn;
    }

    public void ZoomPanorama(float deltaMove, float zoomInStart) {
        renderer.zoomIn = Math.max(90-150,Math.min(90-15, ((deltaMove)/opt.dm.density/5+ zoomInStart)));
    }

    public int getMaxZoom() {
        return 90-15;
    }

    public void setPanoramaZoomIn(float _zoomIn) {
        renderer.zoomIn=_zoomIn;
    }

    public void PanPannoramaXY(float dx, float dy) {
        renderer.yAngle += dx*0.1f;
        renderer.xAngle += dy*0.1f;
        if(renderer.yAngle > 360){
            renderer.yAngle%=360;
        }else if(renderer.yAngle < 0){
            renderer.yAngle = renderer.yAngle%360+360;
        }
        float trim = 89f;
        renderer.xAngle=Math.max(-trim,Math.min(trim,renderer.xAngle));
    }

    public void onPanoramaResize(int widthPixels, int heightPixels) {
        renderer.onSurfaceChanged(widthPixels, heightPixels);
    }

    private final static class CompoundPolymerizer {
        public static void Register(int playercode, Object player, CompoundListener compoundListener) {
        switch (playercode){
            case Native_player:{
                if(!(player instanceof MediaPlayer)) return;
                MediaPlayer p = (MediaPlayer) player;
                p.setOnPreparedListener(compoundListener);
                p.setOnVideoSizeChangedListener(compoundListener);
                p.setOnCompletionListener(compoundListener);
                p.setOnErrorListener(compoundListener);
                p.setOnInfoListener(compoundListener);
                p.setOnBufferingUpdateListener(compoundListener);
                p.setOnSeekCompleteListener(compoundListener);
                p.setOnTimedTextListener(compoundListener);
                break;
            }
            case BZhan_player:{
                if(!(player instanceof IjkMediaPlayer)) return;
                IjkMediaPlayer p = (IjkMediaPlayer) player;
                p.setOnPreparedListener(compoundListener);
                p.setOnVideoSizeChangedListener(compoundListener);
                p.setOnCompletionListener(compoundListener);
                p.setOnErrorListener(compoundListener);
                p.setOnInfoListener(compoundListener);
                p.setOnBufferingUpdateListener(compoundListener);
                p.setOnSeekCompleteListener(compoundListener);
                p.setOnTimedTextListener(compoundListener);
                break;
            }
        }
    }}

    private final class
    CompoundListener implements Player.EventListener, TextOutput,
            VideoListener, View.OnLayoutChangeListener, IMediaPlayer.OnPreparedListener,
            IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener,
            IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener,
            IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener,
            IMediaPlayer.OnTimedTextListener, MediaPlayer.OnPreparedListener,
            MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener,
            MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
            MediaPlayer.OnBufferingUpdateListener,
            MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnTimedTextListener{
        @Override
        public void onPlayerError(ExoPlaybackException error) {
            //falling back
            //a.OnError();
            CMN.Log("exoplayer falling back !");
            if(!opt.getPanoramaMode()){
                refreshRenderViews();
                createPlayer(Native_player);
                attachViews();
                playMediaAtPath(null);
            }
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
            a.onNewVideoViewLayout(width, height, width,height);
        }

        @Override
        public void onRenderedFirstFrame() {
        }

        @Override
        public void onTracksChanged(TrackGroupArray tracks, TrackSelectionArray selections) {
        updateForCurrentTrackSelections(/* isNewPlayer= */ false);
        }

        //![1] Player.EventListener implementation
        //TODO what is exo PlaybackPreparer ? how to handle error messages? what is text output?
        /** exo stuffs */
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            //CMN.Log("onPlayerStateChangedonPlayerStateChanged");
            SimpleExoPlayer mMediaPlayer = ((SimpleExoPlayer)player);
            switch(playbackState){
                case SimpleExoPlayer.STATE_READY:
                    if(onStartSTime!=0) {
                        mMediaPlayer.seekTo(onStartSTime);
                        onStartSTime=0;
                    }
                    if(opt.isEqualizerEnabled()){
                        //if(session_id_stamp!=mMediaPlayer.getAudioSessionId()){
                            //BatchAjustAmp(0,null);
                        //}
                    }
                    getAudioSessionId();
                    a.OnPrepared(false);
                    if(!a.isRudeTLEnabled)
                        a.setRudeTL();
                break;
                case SimpleExoPlayer.STATE_ENDED:
                    if(!a.isTracking){
                        mMediaPlayer.seekTo(0);
                        mMediaPlayer.setPlayWhenReady(true);
                    }
                break;
            }
        }

        @Override
        public void onPositionDiscontinuity(@DiscontinuityReason int reason) {
        }

        //![2] OnLayoutChangeListener implementation

        @Override
        public void onLayoutChange( View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            //applyTextureViewRotation((TextureView) view, textureViewRotation);
        }

        @Override
        public void onTimelineChanged( Timeline timeline, @Nullable Object manifest, @Player.TimelineChangeReason int reason) {
            SimpleExoPlayer mMediaPlayer = ((SimpleExoPlayer)player);
            //CMN.Log("onTimelineChangedonTimelineChanged",mMediaPlayer.getCurrentPosition(),mMediaPlayer.getDuration());
            //a.onTimeChanged(mMediaPlayer.getCurrentPosition());
        }

        //![3] TextOutput implementation
        @Override public void onCues(List<Cue> cues) { }

        //IJK stuffs
        @Override
        public void onPrepared(IMediaPlayer mp) {
            //CMN.Log("onPreparedonPrepared");
            a.OnPrepared(true);
            if(!a.isRudeTLEnabled)
                a.setRudeTL();
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
//            if(a.lastMediaTime!=0) {
//                mMediaPlayer.seekTo(a.lastMediaTime);
//                if(!a.ispauseExpected)
//                    mMediaPlayer.start();
//            }
            if(opt.isEqualizerEnabled()){
                //if(session_id_stamp!=mp.getAudioSessionId()){
                    //BatchAjustAmp(0,null);
                //}
            }
            if(!a.ispauseExpected && !mMediaPlayer.isPlaying())
               mMediaPlayer.start();

        }

        @Override
        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sar_num, int sar_den) {
            a.onNewVideoViewLayout(width, height, width,height);
        }

        @Override
        public void onCompletion(IMediaPlayer mp) {
            //CMN.Log("onCompletiononCompletion");
            if(!a.isTracking)
                mp.start();
        }

        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            a.OnError();
            return false;
        }

        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            //CMN.Log("onInfoonInfo",what);
            return false;
        }

        @Override
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            //CMN.Log("onBufferingUpdateonBufferingUpdate",percent);
        }

        @Override
        public void onSeekComplete(IMediaPlayer mp) {

        }

        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {
            CMN.Log("onTimedTextonTimedText",text);
        }



            //native stuffs
            @Override
            public void onPrepared(MediaPlayer mp) {
                //CMN.Log("onPreparedonPrepared");
                a.setValidationExpected(true);
                if(!a.ispauseExpected && !mp.isPlaying())
                    mp.start();
                if(onStartSTime!=0) {
                    mp.seekTo((int) onStartSTime);
                    onStartSTime=0;
                }
                if(opt.isEqualizerEnabled()){
                    //if(session_id_stamp!=mp.getAudioSessionId()){
                        //BatchAjustAmp(0,null);
                    //}
                }
                //if(opt.getPlayerType()==Native_player)
                a.OnPrepared(true);
                if(!a.isRudeTLEnabled)
                    a.setRudeTL();
            }

            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                a.onNewVideoViewLayout(width, height, width,height);
            }

            @Override
            public void onCompletion(MediaPlayer mp) {
                //CMN.Log("onCompletion !!!");
                if(!a.isTracking)
                    mp.start();
            }

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                CMN.Log("onError-n:"+what+","+extra+" =?"+currentFile.getAbsolutePath().equals(a.PreparedStamp));
                a.stopRudeTL();
                if(currentFile.getAbsolutePath().equals(a.PreparedStamp))
                    return true;
                a.OnError();
                return true;
            }

            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) { return false; }

            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) { }

            @Override
            public void onSeekComplete(MediaPlayer mp) { }

            @Override
            public void onTimedText(MediaPlayer mp, TimedText text) { }
        }

    //equa stuffs
    SparseArray<Integer> CachedBands;
    int session_id_stamp=0;
    Object equalizer;
    public void BatchAjustAmp(int delta, ArrayList<Integer> BandLevelsCache) {
        //CMN.Log("batch_adjusting...",delta,BandLevelsCache);
        if(player instanceof  MediaPlayer || player instanceof IjkMediaPlayer || player instanceof  SimpleExoPlayer){
            if(!(equalizer instanceof Equalizer))
                equalizer=null;
            try {
                CreateEqualizer();
            } catch (Exception e) {e.printStackTrace();}
            if(equalizer==null){
                //CMN.Log("batch_adjusting falling back");
                return;
            }
            Equalizer mEqualizer= (Equalizer) equalizer;
            if(BandLevelsCache!=null) {
                FillEqualizerFromCache(mEqualizer);
                int min = mEqualizer.getBandLevelRange()[0];
                int max = mEqualizer.getBandLevelRange()[1];
                int count = BandLevelsCache.size();//mEqualizer.getNumberOfBands();
                for (int i = 0; i < count; i++) {
                    short lvI = (short) BandLevelsCache.get(i).intValue();//mEqualizer.getAmp((short) i);
                    int modified = Math.max(min, Math.min(max, lvI + delta));
                    CachedBands.put(mEqualizer.getCenterFreq((short) i), modified);
                    //CMN.Log("modified" + modified, delta, lvI + delta);
                    mEqualizer.setBandLevel((short) i, (short) modified);
                }
            }
            if(opt.isEqualizerReallyEnabled())mEqualizer.setEnabled(true);
        }else
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            if(!(equalizer instanceof  org.videolan.libvlc.MediaPlayer.Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            org.videolan.libvlc.MediaPlayer.Equalizer mEqualizer= (org.videolan.libvlc.MediaPlayer.Equalizer) equalizer;
            int count  = org.videolan.libvlc.MediaPlayer.Equalizer.getBandCount();
            if(BandLevelsCache!=null) {
                int min =-2000;
                int max = 2000;
                //mEqualizer.setPreAmp(20);
                for (int i = 0; i < count; i++) {
                    int lvI = (int) (BandLevelsCache.get(i));
                    int modified = Math.max(min, Math.min(max, lvI + delta));
                    CachedBands.put((int) (org.videolan.libvlc.MediaPlayer.Equalizer.getBandFrequency(i) * 1000), modified);
                    boolean succ = mEqualizer.setAmp(i, modified * 1.0f / 100);
                }
            }
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.setEqualizer(mEqualizer);
        }
    }

    @Override
    public int setAmp(int id, int amp) {
        if(player instanceof  MediaPlayer || player instanceof  IjkMediaPlayer || player instanceof  SimpleExoPlayer){
            if(!(equalizer instanceof  Equalizer)){
                equalizer=null;
            }
            try {
                CreateEqualizer();
            } catch (Exception e) {e.printStackTrace();}
            if(equalizer==null){
                //if(old_equalizer!=null) equalizer=old_equalizer; else return;
                //CMN.Log("falling back");
                return -1;
            }
            Equalizer mEqualizer= (Equalizer) equalizer;
            CachedBands.put(mEqualizer.getCenterFreq((short) id), amp);
            mEqualizer.setBandLevel((short)id, (short)amp);
            if(opt.isEqualizerReallyEnabled())mEqualizer.setEnabled(true);
        }else
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            if(!(equalizer instanceof  org.videolan.libvlc.MediaPlayer.Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            org.videolan.libvlc.MediaPlayer.Equalizer mEqualizer= (org.videolan.libvlc.MediaPlayer.Equalizer) equalizer;
            CachedBands.put((int) (org.videolan.libvlc.MediaPlayer.Equalizer.getBandFrequency(id)*1000), amp);
            mEqualizer.setAmp(id, amp*1.0f/100);
            //CMN.Log("setting band"+(amp*1.0f/100));
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.setEqualizer(mEqualizer);
        }
        return 0;
    }

    @Override
    public int getAmp(int index) {
        if(player instanceof  MediaPlayer || player instanceof  IjkMediaPlayer || player instanceof  SimpleExoPlayer){
            if(!(equalizer instanceof  Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            Equalizer mEqualizer= (Equalizer) equalizer;
            int freq = mEqualizer.getCenterFreq((short) index);
            Integer cached = CachedBands.get(freq);
            if(cached!=null)
                return cached;
            else
                CachedBands.put(freq, cached=(int)mEqualizer.getBandLevel((short)index));
            return cached;
        }else
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            if(!(equalizer instanceof  org.videolan.libvlc.MediaPlayer.Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            org.videolan.libvlc.MediaPlayer.Equalizer mEqualizer= (org.videolan.libvlc.MediaPlayer.Equalizer) equalizer;
            int freq = (int) (1000*org.videolan.libvlc.MediaPlayer.Equalizer.getBandFrequency(index));
            Integer cached = CachedBands.get(freq);
            if(cached!=null)
                return cached;
            else
                cached=(int) (100*mEqualizer.getAmp(index));
            return cached;
        }
        return 0;
    }

    @Override
    public int[] getBandRange() {
        int[] ret = new int[]{-2000,2000};
        if(player instanceof  MediaPlayer || player instanceof IjkMediaPlayer || player instanceof  SimpleExoPlayer){
            if(!(equalizer instanceof  Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            Equalizer mEqualizer = (Equalizer) equalizer;
            ret[0]=mEqualizer.getBandLevelRange()[0];
            ret[1]=mEqualizer.getBandLevelRange()[1];
        }else
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            if(!(equalizer instanceof  org.videolan.libvlc.MediaPlayer.Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
        }
        CMN.Log("returned_range", ret[0], ret[1], player);
        return ret;
    }

    @Override
    public float getBandFrequency(int index) {
        if(player instanceof  MediaPlayer || player instanceof  IjkMediaPlayer || player instanceof  SimpleExoPlayer){
            if(!(equalizer instanceof  Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            Equalizer mEqualizer= (Equalizer) equalizer;
            return mEqualizer.getCenterFreq((short) index)/1000;
        }else
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            if(!(equalizer instanceof  org.videolan.libvlc.MediaPlayer.Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            return org.videolan.libvlc.MediaPlayer.Equalizer.getBandFrequency(index);
        }
        return 1000;
    }

    private void FillEqualizerFromCache(Equalizer mEqualizer) {
        //if(CachedBands==null)return;
        int count = mEqualizer.getNumberOfBands();
        for (short i = 0; i < count; i++) {
            int freq=mEqualizer.getCenterFreq(i);
            Integer Target = CachedBands.get(freq);
            if(Target==null) CachedBands.put(freq,Target=0);
            mEqualizer.setBandLevel(i, (short)Target.intValue());
            //CMN.Log("filled",i, (short)Target.intValue() );
        }
    }

    private void FillEqualizerFromCache(org.videolan.libvlc.MediaPlayer.Equalizer mEqualizer) {
        int count = org.videolan.libvlc.MediaPlayer.Equalizer.getBandCount();
        for (short i = 0; i < count; i++) {
            int freq= (int) (org.videolan.libvlc.MediaPlayer.Equalizer.getBandFrequency(i)* 1000);
            Integer Target = CachedBands.get(freq);
            if(Target==null) CachedBands.put(freq,Target=0);
            mEqualizer.setAmp(i, Target.intValue() * 1.0f / 100);
        }
    }

    public void FillEqualizerValuesToJSON(JSONArray jsonArray) {
        jsonArray.clear();
        if(equalizer instanceof  Equalizer){
            Equalizer mEqualizer = (Equalizer) equalizer;
            int count = mEqualizer.getNumberOfBands();
            for (short i = 0; i < count; i++) {
                int freq=mEqualizer.getCenterFreq(i);
                Integer Target = CachedBands.get(freq);
                if(Target==null) Target=0;
                jsonArray.add(Target);
            }
        }else if(equalizer instanceof org.videolan.libvlc.MediaPlayer.Equalizer){
            org.videolan.libvlc.MediaPlayer.Equalizer mEqualizer = (org.videolan.libvlc.MediaPlayer.Equalizer) equalizer;
            int count = org.videolan.libvlc.MediaPlayer.Equalizer.getBandCount();
            for (int i = 0; i < count; i++) {
                int freq= (int) (org.videolan.libvlc.MediaPlayer.Equalizer.getBandFrequency(i)*1000);
                Integer Target = CachedBands.get(freq);
                if(Target==null) Target=0;
                jsonArray.add(Target);
            }
        }
    }

    private void ClearCachedValuesForAPI(Equalizer mEqualizer) {
        int count = mEqualizer.getNumberOfBands();
        for (short i = 0; i < count; i++) {
            int freq= mEqualizer.getCenterFreq(i);
            CachedBands.remove(freq);
        }
    }

    private void ClearCachedValuesForVLC() {
        int count = org.videolan.libvlc.MediaPlayer.Equalizer.getBandCount();
        for (short i = 0; i < count; i++) {
            int freq= (int) (org.videolan.libvlc.MediaPlayer.Equalizer.getBandFrequency(i)* 1000);
            CachedBands.remove(freq);
        }
    }

    private void CreateEqualizer() {
        if(equalizer==null){
            //CMN.Log("recreating...");
            if(player instanceof  MediaPlayer){
                MediaPlayer mMediaPlayer= (MediaPlayer) player;
                equalizer = new Equalizer(0,mMediaPlayer.getAudioSessionId());
                CMN.Log("CreateEqualizer for mediaplayer ： asid= ",mMediaPlayer.getAudioSessionId());
                session_id_stamp=mMediaPlayer.getAudioSessionId();
                Equalizer mEqualizer= (Equalizer) equalizer;
                FillEqualizerFromCache(mEqualizer);
                if(opt.isEqualizerReallyEnabled())mEqualizer.setEnabled(true);
            }else if(player instanceof  SimpleExoPlayer){
                SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
                int session_id = getAudioSessionId();//mMediaPlayer.getAudioSessionId();
                equalizer = new Equalizer(0,session_id);
                session_id_stamp=session_id;
                Equalizer mEqualizer= (Equalizer) equalizer;
                FillEqualizerFromCache(mEqualizer);
                if(opt.isEqualizerReallyEnabled())mEqualizer.setEnabled(true);
            }else if(player instanceof IjkMediaPlayer) {
                IjkMediaPlayer mMediaPlayer = ((IjkMediaPlayer) player);
                int session_id = mMediaPlayer.getAudioSessionId();
                CMN.Log("session_id",session_id);
                equalizer = new Equalizer(0,session_id);
                session_id_stamp=session_id;
                Equalizer mEqualizer= (Equalizer) equalizer;
                FillEqualizerFromCache(mEqualizer);
                if(opt.isEqualizerReallyEnabled())mEqualizer.setEnabled(true);
            }else if(player instanceof  org.videolan.libvlc.MediaPlayer){
                equalizer = org.videolan.libvlc.MediaPlayer.Equalizer.create();
                org.videolan.libvlc.MediaPlayer.Equalizer mEqualizer= (org.videolan.libvlc.MediaPlayer.Equalizer) equalizer;
                FillEqualizerFromCache(mEqualizer);
                org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
                mMediaPlayer.setEqualizer(mEqualizer);
            }
        }
    }

    public void ShutDownEqualizer() {
        if(equalizer instanceof  Equalizer){
            ((Equalizer) equalizer).setEnabled(false);
        }else if(equalizer instanceof  org.videolan.libvlc.MediaPlayer.Equalizer && player instanceof org.videolan.libvlc.MediaPlayer){
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.setEqualizer(null);
        }
    }

    /** Here is the typical place creating Equalizer. But may fail.*/
    @Override public int getBandCount() {
        if(CachedBands==null)
            CachedBands= new SparseArray<>(5);
        int ret=0;
        if(player instanceof  MediaPlayer || player instanceof  IjkMediaPlayer || player instanceof  SimpleExoPlayer){
            if(!(equalizer instanceof  Equalizer)){
                equalizer=null;
                try {
                    CreateEqualizer();//may fail
                } catch (Exception ignored) {}
                if(equalizer==null)
                    return -1;
            }
            Equalizer mEqualizer= (Equalizer) equalizer;
            ret = mEqualizer.getNumberOfBands();
        }else
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            if(!(equalizer instanceof  org.videolan.libvlc.MediaPlayer.Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            ret = org.videolan.libvlc.MediaPlayer.Equalizer.getBandCount();
        }
        return ret;
    }

    int exo_audiosessionid_cache=0;
    public int getAudioSessionId() {
        if(player instanceof  SimpleExoPlayer){
            SimpleExoPlayer mMediaPlayer = (SimpleExoPlayer) player;
            int id = mMediaPlayer.getAudioSessionId();
            return id==0?exo_audiosessionid_cache:(exo_audiosessionid_cache=id);
        }else if(player instanceof  IjkMediaPlayer){
            IjkMediaPlayer mMediaPlayer = (IjkMediaPlayer) player;
            return mMediaPlayer.getAudioSessionId();
        }else if(player instanceof  MediaPlayer){
            MediaPlayer mMediaPlayer = (MediaPlayer) player;
            return mMediaPlayer.getAudioSessionId();
        }
        return -1;
    }

    public List<String> getPresetNames(){
        String[] names = null;
        if(player instanceof  MediaPlayer || player instanceof  IjkMediaPlayer || player instanceof  SimpleExoPlayer){
            if(!(equalizer instanceof  Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            Equalizer mEqualizer= (Equalizer) equalizer;
            names=new String[mEqualizer.getNumberOfPresets()];
            for (int i = 0; i < names.length; i++) {
                names[i] = mEqualizer.getPresetName((short) i);
            }
        }else
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            if(!(equalizer instanceof  org.videolan.libvlc.MediaPlayer.Equalizer)){
                equalizer=null;
                CreateEqualizer();
            }
            names=new String[org.videolan.libvlc.MediaPlayer.Equalizer.getPresetCount()];
            for (int i = 0; i < names.length; i++) {
                names[i] = org.videolan.libvlc.MediaPlayer.Equalizer.getPresetName(i);
            }
        }
        return Arrays.asList(names);
    }

    public int usePreset(int index) {
        CMN.Log("using preset", index);
        if(CachedBands==null)
            CachedBands= new SparseArray<Integer>(5);
        if(player instanceof  MediaPlayer || player instanceof  IjkMediaPlayer || player instanceof  SimpleExoPlayer){
            if(!(equalizer instanceof  Equalizer)){
            }
            equalizer=null;
            try {
                CreateEqualizer();
            } catch (Exception e) {e.printStackTrace();}
            if(equalizer==null){
                //if(old_equalizer!=null) equalizer=old_equalizer; else return;
                //CMN.Log("usePreset creat equlizer failed!");
                return -1;
            }
            Equalizer mEqualizer= (Equalizer) equalizer;
            if(index<0 || index>=mEqualizer.getNumberOfPresets())
                return -2;
            ClearCachedValuesForAPI(mEqualizer);
            mEqualizer.usePreset((short) index);
        }else
        if(player instanceof  org.videolan.libvlc.MediaPlayer){
            if(index<0 || index>=org.videolan.libvlc.MediaPlayer.Equalizer.getPresetCount())
                return -2;
            ClearCachedValuesForVLC();
            org.videolan.libvlc.MediaPlayer.Equalizer mEqualizer= org.videolan.libvlc.MediaPlayer.Equalizer.createFromPreset(index);
            equalizer=mEqualizer;
            org.videolan.libvlc.MediaPlayer mMediaPlayer = (org.videolan.libvlc.MediaPlayer) player;
            mMediaPlayer.setEqualizer(mEqualizer);
        }
        return 0;
    }

    public boolean isApiPlayer() {
        return player instanceof  MediaPlayer || player instanceof  IjkMediaPlayer || player instanceof  SimpleExoPlayer;
    }
}