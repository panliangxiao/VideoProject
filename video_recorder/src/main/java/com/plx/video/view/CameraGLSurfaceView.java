package com.plx.video.view;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.util.Log;

import com.plx.video.utils.Logger;

/**
 * Created by plx on 18/9/7.
 */

public class CameraGLSurfaceView extends GLSurfaceView implements
        Renderer, SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = CameraGLSurfaceView.class.getSimpleName();
    private Context mContext;
    private SurfaceTexture mSurface;
    private int mTextureID = -1;
    private CameraDrawer mCameraDrawer;

    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mTextureID = createTextureID();
        mSurface = new SurfaceTexture(mTextureID);
        mSurface.setOnFrameAvailableListener(this);
        mCameraDrawer = new CameraDrawer(mTextureID);
        CameraHolder.getInstance().doOpenCamera(null);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        if(!CameraHolder.getInstance().isPreviewing()){
            CameraHolder.getInstance().doStartPreview(mSurface, 1.33f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Logger.i(TAG, "onDrawFrame...");
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        mSurface.updateTexImage();
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        mCameraDrawer.drawSelf(mtx);
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraHolder.getInstance().doStopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private int createTextureID() {
        int[] texture = new int[1];

        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public SurfaceTexture getSurfaceTexture() {
        return mSurface;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Logger.i(TAG, "onFrameAvailable...");
        this.requestRender();
    }
}
