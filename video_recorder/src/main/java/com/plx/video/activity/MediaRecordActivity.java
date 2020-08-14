package com.plx.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.plx.R;
import com.plx.video.model.MediaObject;
import com.plx.video.model.MediaRecorderConfig;
import com.plx.video.recorder.MediaRecorderBase;
import com.plx.video.recorder.MediaRecorderNative;
import com.plx.video.utils.CameraUtils;
import com.plx.video.utils.FileUtils;
import com.plx.video.view.ProgressView;

import java.io.File;

/**
 * Created by plx on 18/9/6.
 */

public class MediaRecordActivity extends Activity implements MediaRecorderBase.OnErrorListener, View.OnClickListener
    , MediaRecorderBase.OnEncodeListener, MediaRecorderBase.OnPreparedListener{

    private int RECORD_TIME_MIN = (int) (1.5f * 1000);
    /**
     * 录制最长时间
     */
    private int RECORD_TIME_MAX = 6 * 1000;
    /**
     * 刷新进度条
     */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /**
     * 延迟拍摄停止
     */
    private static final int HANDLE_STOP_RECORD = 1;

    /**
     * 下一步
     */
    private ImageView mTitleNext;
    /**
     * 前后摄像头切换
     */
    private CheckBox mCameraSwitch;
    /**
     * 回删按钮、延时按钮、滤镜按钮
     */
    private CheckedTextView mRecordDelete;
    /**
     * 闪光灯
     */
    private CheckBox mRecordLed;
    /**
     * 拍摄按钮
     */
    private TextView mRecordController;

    /**
     * 底部条
     */
    private RelativeLayout mBottomLayout;
    /**
     * 摄像头数据显示画布
     */
    private SurfaceView mSurfaceView;
    /**
     * 录制进度
     */
    private ProgressView mProgressView;

    /**
     * SDK视频录制对象
     */
    private MediaRecorderBase mMediaRecorder;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;

    /**
     * 是否是点击状态
     */
    private volatile boolean mPressedStatus;
    /**
     * 是否已经释放
     */
    private volatile boolean mReleased;
    /**
     * 视屏地址
     */
    public final static String VIDEO_URI = "video_uri";
    /**
     * 本次视频保存的文件夹地址
     */
    public final static String OUTPUT_DIRECTORY = "output_directory";
    /**
     * 视屏截图地址
     */
    public final static String VIDEO_SCREENSHOT = "video_screenshot";
    /**
     * 录制完成后需要跳转的activity
     */
    public final static String OVER_ACTIVITY_NAME = "over_activity_name";
    /**
     * 最大录制时间的key
     */
    public final static String MEDIA_RECORDER_MAX_TIME_KEY = "media_recorder_max_time_key";
    /**
     * 最小录制时间的key
     */
    public final static String MEDIA_RECORDER_MIN_TIME_KEY = "media_recorder_min_time_key";
    /**
     * 录制配置key
     */
    public final static String MEDIA_RECORDER_CONFIG_KEY = "media_recorder_config_key";

    private boolean GO_HOME;
    private boolean startState;
    private boolean NEED_FULL_SCREEN = false;
    private RelativeLayout title_layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        setContentView(R.layout.activity_media_recorder);
        CameraUtils.setVideoCachePath(CameraUtils.getFileDir(this, "/lx_ffmpeg"));
        initData();
        initViews();
    }

    private void initViews(){
        // ~~~ 绑定控件
        mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
        title_layout = (RelativeLayout) findViewById(R.id.title_layout);
        mCameraSwitch = (CheckBox) findViewById(R.id.record_camera_switcher);
        mTitleNext = (ImageView) findViewById(R.id.title_next);
        mProgressView = (ProgressView) findViewById(R.id.record_progress);
        mRecordDelete = (CheckedTextView) findViewById(R.id.record_delete);
        mRecordController = (TextView) findViewById(R.id.record_controller);
        mRecordLed = (CheckBox) findViewById(R.id.record_camera_led);
        mRecordController.setOnTouchListener(mOnVideoControllerTouchListener);
        mTitleNext.setOnClickListener(this);
        mProgressView.setMaxDuration(RECORD_TIME_MAX);
        mProgressView.setMinTime(RECORD_TIME_MIN);
    }

    private void initData() {
//        Intent intent = getIntent();
//        MediaRecorderConfig mediaRecorderConfig = intent.getParcelableExtra(MEDIA_RECORDER_CONFIG_KEY);
//        if (mediaRecorderConfig == null) {
//            return;
//        }
        NEED_FULL_SCREEN = true;
        RECORD_TIME_MAX = 20000;
        RECORD_TIME_MIN = 1500;
        MediaRecorderBase.MAX_FRAME_RATE = 20;
        MediaRecorderBase.NEED_FULL_SCREEN = NEED_FULL_SCREEN;
        MediaRecorderBase.MIN_FRAME_RATE = 8;
        MediaRecorderBase.SMALL_VIDEO_HEIGHT = 480;
        MediaRecorderBase.SMALL_VIDEO_WIDTH = 480;
        MediaRecorderBase.mVideoBitrate = 580000;
        MediaRecorderBase.CAPTURE_THUMBNAILS_TIME = 1;
//        GO_HOME = mediaRecorderConfig.isGO_HOME();
    }

    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnVideoControllerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 检测是否手动对焦
                    // 判断是否已经超时
                    if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                        return true;
                    }

                    // 取消回删
                    if (cancelDelete())
                        return true;
                    if (!startState) {
                        startState = true;
                        startRecord();
                    } else {
                        mMediaObject.buildMediaPart(mMediaRecorder.mCameraId);
                        mProgressView.setData(mMediaObject);
                        setStartUI();
                        mMediaRecorder.setRecordState(true);
                    }

                    break;

                case MotionEvent.ACTION_UP:

                    mMediaRecorder.setRecordState(false);
                    if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                        mTitleNext.performClick();
                    } else {
                        mMediaRecorder.setStopDate();
                        setStopUI();
                    }


                    // 暂停
/*                    if (mPressedStatus) {

                        // 检测是否已经完成
                        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            mTitleNext.performClick();
                        }
                    }*/
                    break;
            }
            return true;
        }

    };

    /**
     * 初始化画布
     */
    private void initSurfaceView() {
//        if (NEED_FULL_SCREEN) {
            mBottomLayout.setBackgroundColor(0);
            title_layout.setBackgroundColor(getResources().getColor(R.color.full_title_color));
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSurfaceView
                    .getLayoutParams();
            lp.setMargins(0,0,0,0);
            mSurfaceView.setLayoutParams(lp);
            mProgressView.setBackgroundColor(getResources().getColor(R.color.full_progress_color));
//        } else {
//            final int w = DeviceUtils.getScreenWidth(this);
//            ((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin = (int) (w / (MediaRecorderBase.SMALL_VIDEO_HEIGHT / (MediaRecorderBase.SMALL_VIDEO_WIDTH * 1.0f)));
//            int width = w;
//            int height = (int) (w * ((MediaRecorderBase.mSupportedPreviewWidth * 1.0f) / MediaRecorderBase.SMALL_VIDEO_HEIGHT));
//            //
//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSurfaceView
//                    .getLayoutParams();
//            lp.width = width;
//            lp.height = height;
//            mSurfaceView.setLayoutParams(lp);
//        }
    }

    /**
     * 初始化拍摄SDK
     */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative();

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        mMediaRecorder.setOnPreparedListener(this);

        File f = new File(CameraUtils.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key,
                CameraUtils.getVideoCachePath() + key);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }


    /**
     * 开始录制
     */
    private void startRecord() {
        if (mMediaRecorder != null) {

            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }

            mProgressView.setData(mMediaObject);
        }

        setStartUI();
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }
        setStopUI();
    }

    /**
     * 取消回删
     */
    private boolean cancelDelete() {
        if (mMediaObject != null) {
            MediaObject.MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.remove) {
                part.remove = false;
//                mRecordDelete.setChecked(false);

                if (mProgressView != null)
                    mProgressView.invalidate();

                return true;
            }
        }
        return false;
    }

    private void setStartUI(){

    }

    private void setStopUI() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mRecordLed.setChecked(false);
            mMediaRecorder.prepare();
            mProgressView.setData(mMediaObject);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.title_next) {// 停止录制
            stopRecord();
            /*finish();
            overridePendingTransition(R.anim.push_bottom_in,
					R.anim.push_bottom_out);*/
        }
    }

    @Override
    public void onEncodeStart() {

    }

    @Override
    public void onEncodeProgress(int progress) {

    }

    @Override
    public void onEncodeComplete() {

    }

    @Override
    public void onEncodeError() {

    }

    @Override
    public void onVideoError(int what, int extra) {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void onPrepared() {
        initSurfaceView();
    }
}
