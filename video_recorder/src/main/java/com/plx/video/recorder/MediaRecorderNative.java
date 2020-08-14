package com.plx.video.recorder;

import android.hardware.Camera;

import com.plx.video.jniInterface.FFmpegNativeBridge;
import com.plx.video.model.MediaObject;

/**
 * Created by plx on 18/9/7.
 */

public class MediaRecorderNative extends MediaRecorderBase {

    /**
     * 视频后缀
     */
    private static final String VIDEO_SUFFIX = ".ts";

    @Override
    public MediaObject.MediaPart startRecord() {
        int vCustomFormat;
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            vCustomFormat = FFmpegNativeBridge.ROTATE_90_CROP_LT;
        } else {
            vCustomFormat = FFmpegNativeBridge.ROTATE_270_CROP_LT_MIRROR_LR;
        }

        FFmpegNativeBridge.nativePrepare( mMediaObject.getOutputDirectory(), mMediaObject.getBaseName(),vCustomFormat, mSupportedPreviewWidth, SMALL_VIDEO_HEIGHT, SMALL_VIDEO_WIDTH, SMALL_VIDEO_HEIGHT, mFrameRate, mVideoBitrate);

        MediaObject.MediaPart result = null;

        if (mMediaObject != null) {

            result = mMediaObject.buildMediaPart(mCameraId, VIDEO_SUFFIX);
            String cmd = String.format("filename = \"%s\"; ", result.mediaPath);
            //如果需要定制非480x480的视频，可以启用以下代码，其他vf参数参考ffmpeg的文档：

            if (mAudioRecorder == null && result != null) {
                mAudioRecorder = new AudioRecorder(this);
                mAudioRecorder.start();
            }
            mRecording = true;

        }
        return result;
    }

    @Override
    public void receiveAudioByte(byte[] sampleBuffer, int len) {
        if (mRecording && len > 0) {
            FFmpegNativeBridge.encodeFrame2AAC(sampleBuffer);
        }
    }

    /**
     * 停止录制
     */
    @Override
    public void stopRecord() {

        super.stopRecord();
        if (mOnEncodeListener != null) {
            mOnEncodeListener.onEncodeStart();
        }
        FFmpegNativeBridge.recordEnd();
    }


    /**
     * 数据回调
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mRecording) {
            FFmpegNativeBridge.encodeFrame2H264(data);
            mPreviewFrameCallCount++;
        }
        super.onPreviewFrame(data, camera);
    }

    /**
     * 预览成功，设置视频输入输出参数
     */
    @Override
    protected void onStartPreviewSuccess() {
//        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//            UtilityAdapter.RenderInputSettings(mSupportedPreviewWidth, SMALL_VIDEO_WIDTH, 0, UtilityAdapter.FLIPTYPE_NORMAL);
//        } else {
//            UtilityAdapter.RenderInputSettings(mSupportedPreviewWidth, SMALL_VIDEO_WIDTH, 180, UtilityAdapter.FLIPTYPE_HORIZONTAL);
//        }
//        UtilityAdapter.RenderOutputSettings(SMALL_VIDEO_WIDTH, SMALL_VIDEO_HEIGHT, mFrameRate, UtilityAdapter.OUTPUTFORMAT_YUV | UtilityAdapter.OUTPUTFORMAT_MASK_MP4/*| UtilityAdapter.OUTPUTFORMAT_MASK_HARDWARE_ACC*/);
    }

//    @Override
    public void allRecordEnd() {

//        final boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempTranscodingVideoPath(), mMediaObject.getOutputVideoThumbPath(),  String.valueOf(CAPTURE_THUMBNAILS_TIME));
//
//        if(mOnEncodeListener!=null){
//            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if(captureFlag){
//                        mOnEncodeListener.onEncodeComplete();
//                    }else {
//                        mOnEncodeListener.onEncodeError();
//                    }
//                }
//            },0);
//
//        }

    }

}
