package com.plx.video.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.plx.video.jniInterface.FFmpegNativeBridge;

import java.io.File;

/**
 * Created by plx on 18/9/21.
 */

public class CameraUtils {
    /** 应用包名 */
    private static String mPackageName;
    /** 应用版本名称 */
    private static String mAppVersionName;
    /** 应用版本号 */
    private static int mAppVersionCode;
    /** 视频缓存路径 */
    private static String mVideoCachePath;

    /** 执行FFMPEG命令保存路径 */
    public final static String FFMPEG_LOG_FILENAME_TEMP = "ffmpeg.log";

    /**
     *
     * @param debug debug模式
     * @param logPath 命令日志存储地址
     */
    public static void initialize(boolean debug,String logPath) {

        if(debug&& TextUtils.isEmpty(logPath)){
            logPath=mVideoCachePath+"/"+FFMPEG_LOG_FILENAME_TEMP;
        }else if(!debug){
            logPath=null;
        }
        FFmpegNativeBridge.nativeInit(debug,logPath);

    }



    /** 获取视频缓存文件夹 */
    public static String getVideoCachePath() {
        return mVideoCachePath;
    }

    /** 设置视频缓存路径 */
    public static void setVideoCachePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        mVideoCachePath = path;

    }

    public static String getFileDir(Context context, String dir){
        String state;
        try {
            state = Environment.getExternalStorageState();// 解决部分机型出异常的bug
        } catch (Exception rex) {
            state= Environment.MEDIA_REMOVED;
        }

        File directory;
        if (Environment.MEDIA_MOUNTED.equals(state)) {// 有Sdcard
            directory = context.getExternalCacheDir(); // 有Sdcard，就使用Sdcard
        } else {
            directory = context.getFilesDir(); // 没有Sdcard卡的，就使用内部磁盘
        }

        File storageDirectory = new File(directory, dir);
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs();
        }

        return storageDirectory.getAbsolutePath();
    }
}
