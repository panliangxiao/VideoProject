//package com.plx.video.view;
//
//import android.content.Context;
//import android.graphics.SurfaceTexture;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//import android.util.AttributeSet;
//
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
//import static android.webkit.ConsoleMessage.MessageLevel.LOG;
//
///**
// * Created by plx on 18/9/7.
// */
//
//public class CameraGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
//    private Context mContext;
//    private SurfaceTexture mSurface;
//    private int mTextureID = -1;
//    private DirectDrawer mDirectDrawer;
//
//    public CameraGLSurfaceView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        mContext = context;
//        // 设置OpenGl ES的版本为2.0
//        setEGLContextClientVersion(2);
//        // 设置与当前GLSurfaceView绑定的Renderer
//        setRenderer(this);
//        // 设置渲染的模式
//        setRenderMode(RENDERMODE_WHEN_DIRTY);
//    }
//
//    @Override
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        // TODO Auto-generated method stub
////        LOG.logI("onSurfaceCreated...");
//        mTextureID = GlUtil.createTextureID();
//        mSurface = new SurfaceTexture(mTextureID);
//        mSurface.setOnFrameAvailableListener(this);
//        mDirectDrawer = new DirectDrawer(mTextureID);
//        CameraCapture.get().openBackCamera();
//
//    }
//
//    @Override
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//        // TODO Auto-generated method stub
////        LOG.logI("onSurfaceChanged...");
//        // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(w,h)指定了视口的大小
//        GLES20.glViewport(0, 0, width, height);
//        if (!CameraCapture.get().isPreviewing()) {
//            CameraCapture.get().doStartPreview(mSurface);
//        }
//
//
//    }
//
//    @Override
//    public void onDrawFrame(GL10 gl) {
//        // TODO Auto-generated method stub
////        LOG.logI("onDrawFrame...");
//        // 设置白色为清屏
//        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
//        // 清除屏幕和深度缓存
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//        // 更新纹理
//        mSurface.updateTexImage();
//
//        mDirectDrawer.draw();
//
//    }
//
//    @Override
//    public void onPause() {
//        // TODO Auto-generated method stub
//        super.onPause();
//        CameraCapture.get().doStopCamera();
//    }
//
//
//    @Override
//    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        // TODO Auto-generated method stub
////        LOG.logI("onFrameAvailable...");
//        this.requestRender();
//    }
//
//}
