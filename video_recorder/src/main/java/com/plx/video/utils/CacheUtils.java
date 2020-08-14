package com.plx.video.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CacheUtils {

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

	public static String saveBitmap(Context context, Bitmap bitmap){
		return saveBitmap(context, bitmap, "wx_friends_share");
	}

	public static String saveBitmap(Context context, Bitmap bitmap, String fileName){
		if(bitmap == null){
			return null;
		}
		File imageFile = new File(getFileDir(context, "wuba/detailCache"), fileName + ".jpg");
		if(imageFile.exists()){
			imageFile.delete();
		}
		try {
			imageFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return imageFile.getAbsolutePath();
	}

}
