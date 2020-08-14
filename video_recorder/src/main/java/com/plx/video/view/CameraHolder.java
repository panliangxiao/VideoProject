package com.plx.video.view;

import java.io.IOException;
import java.util.List;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

import com.plx.video.utils.CamParaUtil;
import com.plx.video.utils.FileUtil;
import com.plx.video.utils.ImageUtil;
import com.plx.video.utils.Logger;

public class CameraHolder {
	private static final String TAG = CameraHolder.class.getSimpleName();
	private Camera mCamera;
	private Camera.Parameters mParams;
	private boolean isPreviewing = false;
	private float mPreviwRate = -1f;
	private static CameraHolder mCameraInterface;

	public interface CamOpenOverCallback{
		void cameraHasOpened();
	}

	private CameraHolder(){

	}
	public static synchronized CameraHolder getInstance(){
		if(mCameraInterface == null){
			mCameraInterface = new CameraHolder();
		}
		return mCameraInterface;
	}
	/**
	 * 打开Camera
	 * @param callback
	 */
	public void doOpenCamera(CamOpenOverCallback callback){
		Logger.i(TAG, "Camera open....");
		if(mCamera == null){
			mCamera = Camera.open();
			Logger.i(TAG, "Camera open over....");
			if(callback != null){
				callback.cameraHasOpened();
			}
		}else{
			Logger.i(TAG, "Camera open �쳣!!!");
			doStopCamera();
		}
			
	
	}
	/**
	 * @param holder
	 * @param previewRate
	 */
	public void doStartPreview(SurfaceHolder holder, float previewRate){
		Logger.i(TAG, "doStartPreview...");
		if(isPreviewing){
			mCamera.stopPreview();
			return;
		}
		if(mCamera != null){
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			initCamera(previewRate);
		}


	}
	/**
	 * 预览摄像头数据
	 * @param surface
	 * @param previewRate
	 */
	public void doStartPreview(SurfaceTexture surface, float previewRate){
		Logger.i(TAG, "doStartPreview...");
		if(isPreviewing){
			mCamera.stopPreview();
			return;
		}
		if(mCamera != null){
			try {
				mCamera.setPreviewTexture(surface);
			} catch (IOException e) {
				e.printStackTrace();
			}
			initCamera(previewRate);
		}
		
	}

	/**
	 * 结束预览Camera
	 */
	public void doStopCamera(){
		if(null != mCamera)
		{
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview(); 
			isPreviewing = false; 
			mPreviwRate = -1f;
			mCamera.release();
			mCamera = null;     
		}
	}
	/**
	 * 拍照
	 */
	public void doTakePicture(){
		if(isPreviewing && (mCamera != null)){
			mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
		}
	}
	public boolean isPreviewing(){
		return isPreviewing;
	}



	private void initCamera(float previewRate){
		if(mCamera != null){

			mParams = mCamera.getParameters();
			mParams.setPictureFormat(PixelFormat.JPEG);
//			CamParaUtil.getInstance().printSupportPictureSize(mParams);
//			CamParaUtil.getInstance().printSupportPreviewSize(mParams);
			Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
					mParams.getSupportedPictureSizes(),previewRate, 800);
			mParams.setPictureSize(pictureSize.width, pictureSize.height);
			Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
					mParams.getSupportedPreviewSizes(), previewRate, 800);
			mParams.setPreviewSize(previewSize.width, previewSize.height);

			mCamera.setDisplayOrientation(90);

//			CamParaUtil.getInstance().printSupportFocusMode(mParams);
			List<String> focusModes = mParams.getSupportedFocusModes();
			if(focusModes.contains("continuous-video")){
				mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			}
			mCamera.setParameters(mParams);	
			mCamera.startPreview();//����Ԥ��

			mCamera.setPreviewCallback(new Camera.PreviewCallback() {
				@Override
				public void onPreviewFrame(byte[] data, Camera camera) {
					Logger.i(TAG, "摄像头回调！");
				}
			});

			isPreviewing = true;
			mPreviwRate = previewRate;

			mParams = mCamera.getParameters();
			Logger.i(TAG, "PreviewSize--With = " + mParams.getPreviewSize().width
					+ "Height = " + mParams.getPreviewSize().height);
			Logger.i(TAG, "PictureSize--With = " + mParams.getPictureSize().width
					+ "Height = " + mParams.getPictureSize().height);
		}
	}



	ShutterCallback mShutterCallback = new ShutterCallback()
	//���Ű��µĻص������������ǿ����������Ʋ��š����ꡱ��֮��Ĳ�����Ĭ�ϵľ������ꡣ
	{
		public void onShutter() {
			Logger.i(TAG, "myShutterCallback:onShutter...");
		}
	};
	PictureCallback mRawCallback = new PictureCallback() {

		public void onPictureTaken(byte[] data, Camera camera) {
			Logger.i(TAG, "myRawCallback:onPictureTaken...");

		}
	};
	PictureCallback mJpegPictureCallback = new PictureCallback() 
	//��jpegͼ�����ݵĻص�,����Ҫ��һ���ص�
	{
		public void onPictureTaken(byte[] data, Camera camera) {
			Logger.i(TAG, "myJpegCallback:onPictureTaken...");
			Bitmap b = null;
			if(null != data){
				b = BitmapFactory.decodeByteArray(data, 0, data.length);
				mCamera.stopPreview();
				isPreviewing = false;
			}
			if(null != b) {
				Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
				FileUtil.saveBitmap(rotaBitmap);
			}
			mCamera.startPreview();
			isPreviewing = true;
		}
	};


}
