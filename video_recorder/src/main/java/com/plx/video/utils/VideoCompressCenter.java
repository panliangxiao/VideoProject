package com.plx.video.utils;

import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.plx.video.jniInterface.FFmpegNativeBridge;
import com.plx.video.model.BitrateConfig;
import com.plx.video.model.ExportConfig;

import java.io.File;

/**
 * Created by plx on 18/8/1.
 */

public class VideoCompressCenter {

    private final String mNeedCompressVideo;
    private BitrateConfig compressConfig;
    private ExportConfig exportConfig;

    public VideoCompressCenter(ExportConfig exportConfig){
        this.exportConfig = exportConfig;
        this.compressConfig = exportConfig.getCompressConfig();
//        if (exportConfig.getFrameRate() > 0) {
//            setTranscodingFrameRate(exportConfig.getFrameRate());
//        }
        mNeedCompressVideo = exportConfig.getVideoPath();
    }

    public Boolean doCompress(boolean mergeFlag) {
            String vbr = " -vbr 4 ";
//            if (compressConfig != null && compressConfig.getMode() == BaseMediaBitrateConfig.MODE.CBR) {
//                vbr = "";
//            }
            String scaleWH = getScaleWH(mNeedCompressVideo, exportConfig.getScale());;
            if(!TextUtils.isEmpty(scaleWH)){
                scaleWH="-s "+scaleWH;
            }else {
                scaleWH="";
            }
        String key = String.valueOf(System.currentTimeMillis());

        String cmd_transcoding = String.format("ffmpeg -threads 16 -i %s -c:v libx264 %s %s %s -c:a libfdk_aac %s %s %s %s",
                mNeedCompressVideo,
                getBitrateModeCommand(compressConfig, "", false),
                getBitrateCrfSize(compressConfig, "-crf 28", false),
                getBitrateVelocity(compressConfig, "-preset:v ultrafast", false),
                vbr,
                "",
                scaleWH,
                exportConfig.getOutputVideoDictionary() + File.separator + System.currentTimeMillis() + ".mp4"
        );


        return FFmpegNativeBridge.runCommand(cmd_transcoding) == 0;



    }

    private String getScaleWH(final String videoPath, float scale) {
        final MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
        String s = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        String videoW = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String videoH = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        int srcW = Integer.valueOf(videoW);
        int srcH = Integer.valueOf(videoH);
        int newsrcW = (int) (srcW / scale);
        int newsrcH = (int) (srcH / scale);
        if (newsrcH % 2 != 0) {
            newsrcH += 1;
        }
        if (newsrcW % 2 != 0) {
            newsrcW += 1;
        }
        if (s.equals("90") || s.equals("270")) {
            return String.format("%dx%d", newsrcH,newsrcW);

        } else if (s.equals("0") || s.equals("180") || s.equals("360")) {
            return String.format("%dx%d", newsrcW, newsrcH);
        }else {
            return "";
        }
    }


    protected String getBitrateModeCommand(BitrateConfig config, String defualtCmd, boolean needSymbol) {
        String add = "";
        if (TextUtils.isEmpty(defualtCmd)) {
            defualtCmd = "";
        }
        if (config != null) {
            if (config.getMode() == BitrateConfig.MODE.VBR) {
                if (needSymbol) {
                    add = String.format(" -x264opts \"bitrate=%d:vbv-maxrate=%d\" ", config.getBitrate(), config.getMaxBitrate());
                } else {
                    add = String.format(" -x264opts bitrate=%d:vbv-maxrate=%d ", config.getBitrate(), config.getMaxBitrate());
                }
                return add;
            } else if (config.getMode() == BitrateConfig.MODE.CBR) {
                if (needSymbol) {
                    add = String.format(" -x264opts \"bitrate=%d:vbv-bufsize=%d:nal_hrd=cbr\" ", config.getBitrate(), config.getBufSize());
                } else {
                    add = String.format(" -x264opts bitrate=%d:vbv-bufsize=%d:nal_hrd=cbr ", config.getBitrate(), config.getBufSize());

                }
                return add;

            }
        }
        return defualtCmd;
    }

    protected String getBitrateCrfSize(BitrateConfig config, String defualtCmd, boolean nendSymbol) {
        if (TextUtils.isEmpty(defualtCmd)) {
            defualtCmd = "";
        }
        String add = "";
        if (config != null && config.getMode() == BitrateConfig.MODE.AUTO_VBR && config.getCrfSize() > 0) {
            if (nendSymbol) {
                add = String.format("-crf \"%d\" ", config.getCrfSize());
            } else {
                add = String.format("-crf %d ", config.getCrfSize());
            }
        } else {
            return defualtCmd;
        }
        return add;
    }

    protected String getBitrateVelocity(BitrateConfig config, String defualtCmd, boolean nendSymbol) {
        if (TextUtils.isEmpty(defualtCmd)) {
            defualtCmd = "";
        }
        String add = "";
        if (config != null && !TextUtils.isEmpty(config.getVelocity())) {
            if (nendSymbol) {
                add = String.format("-preset \"%s\" ", config.getVelocity());
            } else {
                add = String.format("-preset %s ", config.getVelocity());
            }
        } else {
            return defualtCmd;
        }
        return add;
    }
}
