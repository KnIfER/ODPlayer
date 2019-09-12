package com.knziha.panorama;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES10;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.view.Surface;

import com.knziha.ODPlayer.CMN;
import com.knziha.ODPlayer.R;

import static com.knziha.panorama.ShaderUtils.checkGlError;

/**
 * Created by 海米 on 2018/10/26.
 */

public class GLRenderer {
    private int aPositionHandle;
    private int mProgramHandle;

    private final float[] projectionMatrix= new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] modelViewMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];



    private final float[] mTransformMatrix = new float[16];
    private int uMatrixHandle;
    private int uSTMatrixHandle;

    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;

    private int screenWidth,screenHeight;

    private int[] textures;
    private int textureId;

    private Sphere sphere;

    public interface IOnSurfaceReadyCallback {
        void onSurfaceReady(Surface surface);
    }
    public IOnSurfaceReadyCallback mOnSurfaceReadyListener;

    private SurfaceTexture surfaceTexture;
    private float ratio;
    private long mTid;
    private boolean mIsInit;
    private Surface mSurface;

    final private EGLUtils eglUtils;

    public GLRenderer() {
        sphere = new Sphere(20,100,200);
        eglUtils = new EGLUtils();
    }

    public void onSurfaceCreated(Surface surface, Context context){
        if(surface!=null) eglUtils.initEGL(surface);
        setupInGL(context);
    }

    protected int createTextureId() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        checkGlError("Texture generate");

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        checkGlError("Texture bind");

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES10.GL_TEXTURE_WRAP_S, GLES10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES10.GL_TEXTURE_WRAP_T, GLES10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return textureId = textures[0];
    }


    public SurfaceTexture getSurfaceTexture() {
        return surfaceTexture;
    }
    public void Destroy(){
        release();
        eglUtils.release();
        mOnSurfaceReadyListener=null;
    }
    public void release(){
        if(mProgramHandle!=0)GLES20.glDeleteProgram(mProgramHandle);
        if(textures!=null)GLES20.glDeleteTextures(1,textures,0);
        if(surfaceTexture != null){
            surfaceTexture.release();
            surfaceTexture = null;
        }
        if(mSurface != null){
            mSurface.release();
            mSurface = null;
        }
    }
    public void onSurfaceChanged(int width, int height) {
        if(screenWidth!=width || screenHeight!=height){
            if(surfaceTexture != null){
                surfaceTexture.setDefaultBufferSize(width,height);
            }
            ratio=(float)width/height;
            Matrix.perspectiveM(projectionMatrix, 0, DefaultFieldOfView-zoomIn, ratio,  1, 50);
            Matrix.setLookAtM(viewMatrix, 0,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 0.0f,-1.0f,
                    0.0f, 1.0f, 0.0f);
            screenWidth=width; screenHeight=height;
        }
    }
	public float DefaultFieldOfView=120f;
    public float xAngle=0f;
    public float yAngle=90f;
    public float zAngle;
    public float zoomIn=0f;


    public final void setupInGL(Context context){
        long tid = Thread.currentThread().getId();
        if (tid != mTid || surfaceTexture==null) {
            mTid = tid;
            mIsInit = false;
        }
        mIsInit=false;
        if (!mIsInit){
            initInGL(context);
            mIsInit = true;
        }
    }

    public void initInGL(Context context) {
        CMN.Log("initInGL");
        build_mProgram(context);
        create_mTexture();
    }

    boolean program_built=false;
    private void build_mProgram(Context context) {
        if(!program_built){
            String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vertext_shader);
            String fragmentShader= ShaderUtils.readRawTextFile(context, R.raw.fragment_sharder);

            mProgramHandle=ShaderUtils.createProgram(vertexShader,fragmentShader);
            aPositionHandle= GLES20.glGetAttribLocation(mProgramHandle,"aPosition");
            uMatrixHandle=GLES20.glGetUniformLocation(mProgramHandle,"uMatrix");
            uSTMatrixHandle=GLES20.glGetUniformLocation(mProgramHandle,"uSTMatrix");
            uTextureSamplerHandle=GLES20.glGetUniformLocation(mProgramHandle,"sTexture");
            aTextureCoordHandle=GLES20.glGetAttribLocation(mProgramHandle,"aTexCoord");
            //program_built=true;
        }
    }

    private void create_mTexture() {
        int glTexture = createTextureId();
        if(glTexture==0) {
			//throw new RuntimeException("Error creating gl_texture");
		}
        surfaceTexture = new SurfaceTexture(glTexture);
        mSurface = new Surface(surfaceTexture);
        if (mOnSurfaceReadyListener != null)
            mOnSurfaceReadyListener.onSurfaceReady(mSurface);
    }

    public void onDrawFrame(){
        if(surfaceTexture == null){
            return;
        }
        //setupInGL(null);

        GLES20.glUseProgram(mProgramHandle);
        GLES20.glViewport(0,0,screenWidth,screenHeight);

        try {
            surfaceTexture.updateTexImage();
            surfaceTexture.getTransformMatrix(mTransformMatrix);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(0,0,1.0f,0);

        Matrix.setIdentityM(modelMatrix,0);
        Matrix.rotateM(modelMatrix, 0, -xAngle, 1, 0, 0);
        Matrix.rotateM(modelMatrix, 0, -yAngle, 0, 1, 0);
        Matrix.rotateM(modelMatrix, 0, -zAngle, 0, 0, 1);


        Matrix.perspectiveM(projectionMatrix, 0, Math.max(15,Math.min(150, DefaultFieldOfView-zoomIn)), ratio,  1, 50);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f,-1.0f,
                0.0f, 1.0f, 0.0f);
        //Matrix.translateM(viewMatrix, 0,0f,0f,zoomIn);

        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);


        GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,mMVPMatrix,0);
        GLES20.glUniformMatrix4fv(uSTMatrixHandle,1,false, mTransformMatrix,0);

        sphere.uploadVerticesBuffer(aPositionHandle);
        sphere.uploadTexCoordinateBuffer(aTextureCoordHandle);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
        GLES20.glUniform1i(uTextureSamplerHandle,0);

        sphere.draw();

        eglUtils.swap();
    }
}
