#include <jni.h>
#include <string.h>
#include "ffmpeg_cmd_run.h"
#include "ffmpeg.h"


JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_runCmd(JNIEnv *env, jclass type,
                                                                    jobjectArray cmd) {

    // TODO
    int argc = (*env)->GetArrayLength(env, cmd);
    char *argv[argc];
    jstring jsArray[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jsArray[i] = (jstring) (*env)->GetObjectArrayElement(env, cmd, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env, jsArray[i], 0);
    }
    int ret = ffmpeg_command_run(argc,argv);
    for (i = 0; i < argc; ++i) {
        (*env)->ReleaseStringUTFChars(env, jsArray[i], argv[i]);
    }
    return ret;

}

int ffmpeg_command_run(int argc, char **argv){
    return ffmpeg_cmd_run(argc, argv);
}