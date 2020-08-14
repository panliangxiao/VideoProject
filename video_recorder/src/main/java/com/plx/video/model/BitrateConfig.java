package com.plx.video.model;

import java.io.Serializable;

/**
 * Created by plx on 18/8/2.
 */

public class BitrateConfig implements Serializable {

    /**
     * 码率模式{@link MODE}
     */
    protected int mode=-1;
    /**
     * 固定码率值
     */
    protected int bitrate=-1;
    /**
     * 最大码率值
     */
    protected int maxBitrate=-1;

    protected int bufSize=-1;
    /**
     * 码率等级0~51，越大
     */
    protected int crfSize=-1;
    /**
     * {@link Velocity}  转码速度控制
     */
    protected String velocity;

    protected BitrateConfig(){

    }

    public int getBitrate() {
        return bitrate;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public int getMode() {
        return mode;
    }

    public int getBufSize() {
        return bufSize;
    }

    public int getCrfSize() {
        return crfSize;
    }
    public String getVelocity() {
        return velocity;
    }

    /**
     *
     * @param velocity 转码速度控制,速度越快体积将变大，质量也稍差一点点 {@link Velocity}
     * @return
     */
    public BitrateConfig setVelocity(String velocity) {
        this.velocity=velocity;
        return this;
    }

    public static class MODE {
        /**
         * 默认模式
         */
        public final static int AUTO_VBR = 3;
        /**
         * 这个模式下可设置额定码率
         */
        public final static int VBR = 1;
        /**
         * 固定码率
         */
        public final static int CBR = 2;
    }

    public static class Velocity {
        public final static String ULTRAFAST="ultrafast";
        public final static String SUPERFAST="superfast";
        public final static String VERYFAST="veryfast";
        public final static String FASTER="faster";
        public final static String FAST="fast";
        public final static String MEDIUM="medium";
        public final static String SLOW="slow";
        public final static String SLOWER="slower";
        public final static String VERYSLOW="veryslow";
        public final static String PLACEBO="placebo";
    }
}
