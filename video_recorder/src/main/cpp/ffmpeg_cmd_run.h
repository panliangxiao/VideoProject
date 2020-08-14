//
// Created by plx on 18/8/1.
//

#ifndef FFMPEGTEST_FFMPEG_COMMAND_RUN_H
#define FFMPEGTEST_FFMPEG_COMMAND_RUN_H

#include <jni.h>

JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_runCmd(JNIEnv *env, jclass type,
                                                                    jobjectArray cmd);

int ffmpeg_command_run(int argc, char **argv);

#endif //FFMPEGTEST_FFMPEG_COMMAND_RUN_H
