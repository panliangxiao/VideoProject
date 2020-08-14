package com.plx.video.jniInterface;

import java.util.ArrayList;

/**
 * Created by plx on 18/8/1.
 */

public class FFmpegNativeBridge {

    static {

        System.loadLibrary("avutil");
        System.loadLibrary("fdk-aac");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("swresample");
        System.loadLibrary("avfilter");
        System.loadLibrary("ffmpeg-lib");
    }

    /**
     * 结束录制并且转码保存完成
     */
    public static final int ALL_RECORD_END =1;


    public final static int ROTATE_0_CROP_LF=0;
    /**
     * 旋转90度剪裁左上
     */
    public final static int ROTATE_90_CROP_LT =1;
    /**
     * 暂时没处理
     */
    public final static int ROTATE_180=2;
    /**
     * 旋转270(-90)裁剪左上，左右镜像
     */
    public final static int ROTATE_270_CROP_LT_MIRROR_LR=3;

    public static native int runCmd(String[] cmd);


    /**
     * 命令形式执行
     * @param cmd
     */
    public static int runCommand(String cmd){
        String regulation="[ \\t]+";
        final String[] split = cmd.split(regulation);

        return runCmd(split);
    }

    /**
     *
     * @param mediaBasePath 视频存放目录
     * @param mediaName 视频名称
     * @param filter 旋转镜像剪切处理
     * @param in_width 输入视频宽度
     * @param in_height 输入视频高度
     * @param out_height 输出视频高度
     * @param out_width 输出视频宽度
     * @param frameRate 视频帧率
     * @param bit_rate 视频比特率
     * @return
     */
    public static native int nativePrepare(String mediaBasePath, String mediaName, int filter, int in_width, int in_height, int out_width, int  out_height, int frameRate, long bit_rate);


    /**
     *
     * @return 返回ffmpeg的编译信息
     */
    public static native String getConfig();

    /**
     * 编码一帧视频，暂时只能编码yv12视频
     * @param data
     * @return
     */
    public static native int encodeFrame2H264(byte[] data);

    /**
     * 编码一帧音频,暂时只能编码pcm音频
     * @param data
     * @return
     */
    public static native int encodeFrame2AAC(byte[] data);

    /**
     *  录制结束
     * @return
     */
    public static native int recordEnd();

    public static native void nativeRelease();

    public static native void nativeInit(boolean debug,String logUrl);

    private static ArrayList<FFmpegStateListener> listeners=new ArrayList();

    /**
     *注册录制回调
     * @param listener
     */
    public static void registFFmpegStateListener(FFmpegStateListener listener){

        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    public static void unRegistFFmpegStateListener(FFmpegStateListener listener){
        if(listeners.contains(listener)){
            listeners.remove(listener);
        }
    }
    public interface FFmpegStateListener {
        void allRecordEnd();
    }

}
