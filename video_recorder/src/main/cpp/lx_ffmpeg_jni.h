//
// Created by plx on 18/8/23.
//

#ifndef FFMPEGPROJECT_LX_FFMPEG_JNI_H
#define FFMPEGPROJECT_LX_FFMPEG_JNI_H

#include <jni.h>
#include "base_include.h"

#define VIDEO_FORMAT ".h264"
#define MEDIA_FORMAT ".mp4"
#define AUDIO_FORMAT ".aac"

extern "C"{
jstring getConfig(JNIEnv *env, jclass type);

JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_encodeFrame2H264(JNIEnv *env, jclass type,
                                                                    jbyteArray data_);


JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_encodeFrame2AAC(JNIEnv *env, jclass type,
                                                                   jbyteArray data_);

JNIEXPORT void JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_nativeRelease(JNIEnv *env, jclass type);


JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_nativePrepare(JNIEnv *env, jclass type,
                                                                  jstring mediaBasePath_,
                                                                  jstring mediaName_, jint filter,
                                                                  jint in_width, jint in_height,
                                                                  jint out_width, jint out_height,
                                                                  jint frameRate, jlong bit_rate);

JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_recordEnd(JNIEnv *env, jclass type);

JNIEXPORT void JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_nativeInit(JNIEnv *env, jclass type,
                                                              jboolean debug, jstring logUrl_);

};
#endif //FFMPEGPROJECT_LX_FFMPEG_JNI_H
