package com.plx.video.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by plx on 18/8/2.
 */

public class ExportConfig implements Serializable{
    /**
     * 帧率
     */
    private final int FRAME_RATE;

    /**
     * 默认码率
     */
    public final static int DEFAULT_VIDEO_BITRATE = 800;

    /**
     * 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
     */
    private final int captureThumbnailsTime;

    private final boolean GO_HOME;
    /**
     * 码率配置
     */
    private final BitrateConfig bitrateConfig;

    private final String videoAddress;

    private final float scale;

    public String distVideoDic;

    public String getOutputVideoDictionary() {
        return outputVideoDictionary;
    }

    public void setOutputVideoDictionary(String outputVideoDictionary) {
        this.outputVideoDictionary = outputVideoDictionary;
    }

    private String outputVideoDictionary;

    private ExportConfig(Builder builder) {
        this.captureThumbnailsTime = builder.captureThumbnailsTime;
        this.FRAME_RATE = builder.FRAME_RATE;
        this.bitrateConfig = builder.bitrateConfig;
        this.videoAddress = builder.videoPath;
        this.scale = builder.scale;
        this.GO_HOME = builder.GO_HOME;
        this.outputVideoDictionary = builder.outputDictionary;

    }

    public boolean isGO_HOME() {
        return GO_HOME;
    }

    public int getCaptureThumbnailsTime() {
        return captureThumbnailsTime;
    }

    public int getFrameRate() {
        return FRAME_RATE;
    }


    public BitrateConfig getCompressConfig() {
        return bitrateConfig;
    }

    public String getVideoPath() {
        return videoAddress;
    }

    public float getScale() {
        return scale;
    }


    public static class Builder {
        /**
         * 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
         */
        private int captureThumbnailsTime = 1;


        private boolean GO_HOME = false;

        private BitrateConfig bitrateConfig;
        private int FRAME_RATE;

        private String videoPath;
        private float scale;

        private String outputDictionary;

        public ExportConfig build() {
            return new ExportConfig(this);
        }

        /**
         * @param captureThumbnailsTime 会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
         * @return
         */
        public Builder captureThumbnailsTime(int captureThumbnailsTime) {
            this.captureThumbnailsTime = captureThumbnailsTime;
            return this;
        }

        /**
         * @param bitrateConfig 压缩配置设置
         * {@link VBRModeConfig }{@link }{@link }
         * @return
         */
        public Builder doH264Compress(BitrateConfig bitrateConfig) {
            this.bitrateConfig = bitrateConfig;
            return this;
        }


        public Builder goHome(boolean GO_HOME) {
            this.GO_HOME = GO_HOME;
            return this;
        }

        public Builder setFramerate(int MAX_FRAME_RATE) {
            this.FRAME_RATE = MAX_FRAME_RATE;
            return this;
        }

        public Builder setVideoPath(String videoPath) {
            this.videoPath = videoPath;
            return this;
        }

        public Builder setOutputDictionory(String outputDictionary){
            this.outputDictionary = outputDictionary;
            return this;
        }

        /**
         * @param scale 大于1，否者无效
         * @return
         */
        public Builder setScale(float scale) {
            if (scale <= 1) {
                this.scale = 1;
            } else {
                this.scale = scale;
            }
            return this;
        }
    }

}

