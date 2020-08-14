package com.plx.video.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;

/**
 * Created by plx on 18/9/6.
 */

public class AudioRecorder extends Thread {

    /**
     * 支持采样率设置
     */
    public static final int AUDIO_SAMPLE_RATE_8000Hz = 8000;
    public static final int AUDIO_SAMPLE_RATE_16000Hz = 16000;
    public static final int AUDIO_SAMPLE_RATE_22050Hz = 22050;
    public static final int AUDIO_SAMPLE_RATE_44100Hz = 44100;

    public static final int AUDIO_RECORD_ERROR_UNKNOWN = 0;
    /**
     * 采样率设置不支持
     */
    public static final int AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT = 1;
    /**
     * 最小缓存获取失败
     */
    public static final int AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT = 2;
    /**
     * 创建AudioRecord失败
     */
    public static final int AUDIO_RECORD_ERROR_CREATE_FAILED = 3;

    private IMediaRecorder sMediaRecorder;

    private AudioRecord sAudioRecord = null;
    /** 采样率 */
    private int sSampleRate = 44100;

    public AudioRecorder(IMediaRecorder mediaRecorder){
        sMediaRecorder = mediaRecorder;
    }

    /** 设置采样率 */
    public void setSampleRate(int sampleRate) {
        this.sSampleRate = sampleRate;
    }

    @Override
    public void run() {
        if (sSampleRate != AUDIO_SAMPLE_RATE_8000Hz && sSampleRate != AUDIO_SAMPLE_RATE_16000Hz
                && sSampleRate != AUDIO_SAMPLE_RATE_22050Hz && sSampleRate != AUDIO_SAMPLE_RATE_44100Hz) {
            sMediaRecorder.onAudioError(AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT, "sampleRate not support.");
            return;
        }

        final int mMinBufferSize = AudioRecord.getMinBufferSize(sSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        if (AudioRecord.ERROR_BAD_VALUE == mMinBufferSize) {
            sMediaRecorder.onAudioError(AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT, "parameters are not supported by the hardware.");
            return;
        }

        sAudioRecord = new AudioRecord(android.media.MediaRecorder.AudioSource.MIC, sSampleRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mMinBufferSize);
        if (null == sAudioRecord) {
            sMediaRecorder.onAudioError(AUDIO_RECORD_ERROR_CREATE_FAILED, "new AudioRecord failed.");
            return;
        }
        try {
            sAudioRecord.startRecording();
        } catch (IllegalStateException e) {
            sMediaRecorder.onAudioError(AUDIO_RECORD_ERROR_UNKNOWN, "startRecording failed.");
            return;
        }

        byte[] sampleBuffer = new byte[2048];

        try {
            while (!Thread.currentThread().isInterrupted()) {

                int result = sAudioRecord.read(sampleBuffer, 0, 2048);
                if (result > 0) {
                    sMediaRecorder.receiveAudioByte(sampleBuffer, result);
                }
            }
        } catch (Exception e) {
            sMediaRecorder.onAudioError(AUDIO_RECORD_ERROR_UNKNOWN, e.getMessage());
        }

        sAudioRecord.release();
        sAudioRecord = null;
    }
}
