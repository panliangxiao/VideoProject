package com.plx.video.model;

/**
 * Created by plx on 18/8/2.
 */

public class VBRModeConfig extends  BitrateConfig{

    public VBRModeConfig(int maxBitrate, int bitrate){
        if(maxBitrate<=0||bitrate<=0){
            throw new IllegalArgumentException("maxBitrate or bitrate value error!");
        }
        this.maxBitrate=maxBitrate;
        this.bitrate=bitrate;
        this.mode= MODE.VBR;

    }
}
