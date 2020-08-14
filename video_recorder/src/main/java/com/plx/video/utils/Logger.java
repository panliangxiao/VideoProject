package com.plx.video.utils;

import android.util.Log;

public class Logger {

	private static final String TAG = Logger.class.getSimpleName();

	public static boolean sIsLog = true;

	public static void d(String tag, String msg) {
		if (sIsLog) {
			Log.d(tag, msg);
		}
	}

	public static void d(String msg) {
		if (sIsLog) {
			Log.d(TAG, msg);
		}

	}

	/**
	 * Send a {@link #DEBUG} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static void d(String tag, String msg, Throwable tr) {
		if (sIsLog) {
			Log.d(tag, msg, tr);
		}
	}

	public static void i(String tag, String msg) {
		if (sIsLog) {
			Log.i(tag, msg);
		}
	}

	/**
	 * Send a {@link #INFO} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static void i(String tag, String msg, Throwable tr) {
		if (sIsLog) {
			Log.i(tag, msg, tr);
		}

	}

	/**
	 * Send an {@link #ERROR} log message.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static void e(String tag, String msg) {
		if (sIsLog) {
			Log.e(tag, msg);
		}
	}

	public static void e(String msg) {
		if (sIsLog) {
			Log.e(TAG, msg);
		}
	}

	/**
	 * Send a {@link #ERROR} log message and log the exception.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @param tr
	 *            An exception to log
	 */
	public static void e(String tag, String msg, Throwable tr) {
		if (sIsLog) {
			Log.e(tag, msg, tr);
		}
	}

	public static void e(String msg, Throwable tr) {
		if (sIsLog) {
			Log.e(TAG, msg, tr);
		}
	}
}
