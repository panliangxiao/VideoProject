package com.plx.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.plx.R;
import com.plx.video.activity.MediaRecordActivity;
import com.plx.video.jniInterface.FFmpegNativeBridge;
import com.plx.video.model.BitrateConfig;
import com.plx.video.model.ExportConfig;
import com.plx.video.model.VBRModeConfig;
import com.plx.video.utils.CacheUtils;
import com.plx.video.utils.VideoCompressCenter;
import com.plx.video.view.VoiceIndicatorView;

public class MainActivity extends Activity {

    private final int PERMISSION_REQUEST_CODE = 0x001;
    private final int CHOOSE_CODE = 0x000520;

    private String savePath;

    private Button mGoCamera;

    private static final String[] permissionManifest = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionCheck();
        savePath = CacheUtils.getFileDir(this, "video");

        findViewById(R.id.sample_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose(v);
            }
        });
        TextView textView = findViewById(R.id.sample_txt);
        textView.setText(FFmpegNativeBridge.getConfig());
        mGoCamera = findViewById(R.id.sample_btn2);
        mGoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MediaRecordActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public String stringFromJNI(){
        return "哈哈哈哈";
    }


    /**
     * 选择本地视频，为了方便我采取了系统的API，所以也许在一些定制机上会取不到视频地址，
     * 所以选择手机里视频的代码根据自己业务写为妙。
     *
     * @param v
     */
    public void choose(View v) {

        Intent it = new Intent(Intent.ACTION_PICK,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        it.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*");
        startActivityForResult(it, CHOOSE_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_CODE) {
            //
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                String[] projection = new String[]{MediaStore.Video.VideoColumns._ID, MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.MIME_TYPE, MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.DURATION};

                Cursor cursor = getContentResolver().query(uri, projection, null,
                        null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int _data_num = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                    int mime_type_num = cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE);

                    String _data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA));
                    String mime_type = cursor.getString(mime_type_num);
                    if (!TextUtils.isEmpty(mime_type) && mime_type.contains("video") && !TextUtils.isEmpty(_data)) {
                        BitrateConfig compressMode = null;
                        String maxBitrate = "800";
                        String bitrate = "600";

                        compressMode = new VBRModeConfig(Integer.valueOf(maxBitrate), Integer.valueOf(bitrate));


//                        String sRate = et_only_framerate.getText().toString();
//                        String scale = et_only_scale.getText().toString();
                        int iRate = 0;
                        float fScale=1;
//                        if (!TextUtils.isEmpty(sRate)) {
//                            iRate = Integer.valueOf(sRate);
//                        }
//                        if (!TextUtils.isEmpty(scale)) {
//                            fScale = Float.valueOf(scale);
//                        }
                        ExportConfig.Builder builder = new ExportConfig.Builder();
                        final ExportConfig config = builder
                                .setVideoPath(_data)
                                .captureThumbnailsTime(1)
                                .doH264Compress(compressMode)
                                .setFramerate(iRate)
                                .setScale(fScale)
                                .setOutputDictionory(savePath)
                                .build();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "压缩中。。。", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                new VideoCompressCenter(config).doCompress(true);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MainActivity.this, "压缩完成", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();
                    } else {
                        Toast.makeText(this, "选择的不是视频或者地址错误,也可能是这种方式定制神机取不到！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void permissionCheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean permissionState = true;
            for (String permission : permissionManifest) {
                if (ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissionState = false;
                }
            }
            if (!permissionState) {
                ActivityCompat.requestPermissions(this, permissionManifest, PERMISSION_REQUEST_CODE);
            } else {
//                setSupportCameraSize();
            }
        } else {
//            setSupportCameraSize();
        }
    }
}
