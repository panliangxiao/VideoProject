package com.plx.video.recorder;

import android.hardware.Camera;

import com.plx.video.jniInterface.FFmpegNativeBridge;
import com.plx.video.model.MediaObject;

/**
 * Created by plx on 18/9/6.
 */

public class MediaRecorder implements IMediaRecorder{

    /**
     * 摄像头类型（前置/后置），默认后置
     */
    protected int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    /**
     * 拍摄存储对象
     */
    protected MediaObject mMediaObject;
    /**
     * 声音录制
     */
    protected AudioRecorder mAudioRecorder;

    /**
     * 是否正在录制
     */
    protected volatile boolean mRecording;

    public MediaRecorder() {
//        FFmpegNativeBridge.registFFmpegStateListener(this);
    }

    /**
     * 视频后缀
     */
    private static final String VIDEO_SUFFIX = ".ts";

    @Override
    public MediaObject.MediaPart startRecord() {
        int vCustomFormat;
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            vCustomFormat=FFmpegNativeBridge.ROTATE_90_CROP_LT;
        } else {
            vCustomFormat=FFmpegNativeBridge.ROTATE_270_CROP_LT_MIRROR_LR;
        }
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
    public void stopRecord() {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void receiveAudioByte(byte[] sampleBuffer, int len) {

    }
}
