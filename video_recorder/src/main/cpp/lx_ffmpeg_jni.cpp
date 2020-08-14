//
// Created by plx on 18/8/23.
//

#include "lx_ffmpeg_jni.h"
#include <string>
#include "LXYUVEncodeH264.h"
#include "LXPCMEncodeAAC.h"
#include "LXJNIHandler.h"

#include <assert.h>

LXPCMEncodeAAC *aacEncoder;
LXYUVEncodeH264 *h264Encoder;


#define JNIREG_CLASS "com/plx/video/jniInterface/FFmpegNativeBridge"//指定要注册的类

/**
* 方法对应表
*/
static JNINativeMethod gMethods[] = {
        {"getConfig", "()Ljava/lang/String;", (void*)getConfig},
};

/*
* 为某一个类注册本地方法
*/
static int registerNativeMethods(JNIEnv* env
        , const char* className
        , JNINativeMethod* gMethods, int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

/*
* 为所有类注册本地方法
*/
static int registerNatives(JNIEnv* env) {
    return registerNativeMethods(env, JNIREG_CLASS, gMethods,
                                 sizeof(gMethods) / sizeof(gMethods[0]));
}

/*
* System.loadLibrary("lib")时调用
* 如果成功返回JNI版本, 失败返回-1
*/
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = NULL;
    jint result = -1;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    if (!registerNatives(env)) {//注册
        return -1;
    }
    //成功
    result = JNI_VERSION_1_4;
    return result;
}

jstring getConfig(JNIEnv *env, jclass type){
    char info[10000] = {0};
    sprintf(info, "%s\n", avcodec_configuration());
    return env->NewStringUTF(info);
}


JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_encodeFrame2H264(JNIEnv *env, jclass type,
                                                                    jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO
    int i = h264Encoder->startSendOneFrame((uint8_t *)data);

    env->ReleaseByteArrayElements(data_, data, 0);

    return i;
}

JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_encodeFrame2AAC(JNIEnv *env, jclass type,
                                                                   jbyteArray data_) {
    jbyte *data = env->GetByteArrayElements(data_, NULL);

    // TODO
    int i = aacEncoder->sendOneFrame((uint8_t *) data);
    env->ReleaseByteArrayElements(data_, data, 0);

    return i;
}

JNIEXPORT void JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_nativeRelease(JNIEnv *env, jclass type) {

    // TODO
    if (aacEncoder != NULL) {
        try {
            aacEncoder->release();

        } catch (exception e) {

        }
        aacEncoder = NULL;

    }
    if (h264Encoder != NULL) {
        try {
            h264Encoder->release();

        } catch (exception e) {

        }
        h264Encoder = NULL;

    }
}

JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_nativePrepare(JNIEnv *env, jclass type,
                                                                  jstring mediaBasePath_,
                                                                  jstring mediaName_, jint filter,
                                                                  jint in_width, jint in_height,
                                                                  jint out_width, jint out_height,
                                                                  jint frameRate, jlong bit_rate) {

    // TODO

    jclass global_class = (jclass) env->NewGlobalRef(type);
    LXMediaArguments *arguments = (LXMediaArguments *) malloc(sizeof(LXMediaArguments));
    const char *media_base_path = env->GetStringUTFChars(mediaBasePath_, 0);
    const char *media_name = env->GetStringUTFChars(mediaName_, 0);
    LXJNIHandler *jni_handler = new LXJNIHandler();
    jni_handler->setAudioState(START_STATE);
    jni_handler->setVideoState(START_STATE);
    arguments->media_base_path = media_base_path;
    arguments->media_name = media_name;

    size_t v_path_size = strlen(media_base_path) + strlen(media_name) + strlen(VIDEO_FORMAT) + 1;
    arguments->video_path = (char *) malloc(v_path_size + 1);

    size_t a_path_size = strlen(media_base_path) + strlen(media_name) + strlen(AUDIO_FORMAT) + 1;
    arguments->audio_path = (char *) malloc(a_path_size + 1);

    size_t m_path_size = strlen(media_base_path) + strlen(media_name) + strlen(MEDIA_FORMAT) + 1;
    arguments->media_path = (char *) malloc(m_path_size + 1);

    strcpy(arguments->video_path, media_base_path);
    strcat(arguments->video_path, "/");
    strcat(arguments->video_path, media_name);
    strcat(arguments->video_path, VIDEO_FORMAT);

    strcpy(arguments->audio_path, media_base_path);
    strcat(arguments->audio_path, "/");
    strcat(arguments->audio_path, media_name);
    strcat(arguments->audio_path, AUDIO_FORMAT);

    strcpy(arguments->media_path, media_base_path);
    strcat(arguments->media_path, "/");
    strcat(arguments->media_path, media_name);
    strcat(arguments->media_path, MEDIA_FORMAT);

    arguments->video_bit_rate = bit_rate;
    arguments->frame_rate = frameRate;
    arguments->audio_bit_rate = 40000;
    arguments->audio_sample_rate = 44100;
    arguments->in_width = in_width;
    arguments->in_height = in_height;
    arguments->out_height = out_height;
    arguments->out_width = out_width;
    arguments->v_custom_format = filter;
    arguments->handler = jni_handler;
    arguments->env = env;
    arguments->java_class = global_class;
    arguments->env->GetJavaVM(&arguments->javaVM);
    h264Encoder = new LXYUVEncodeH264(arguments);
    aacEncoder = new LXPCMEncodeAAC(arguments);
    int v_code = h264Encoder->initVideoEncoder();
    int a_code = aacEncoder->initAudioEncoder();

    env->ReleaseStringUTFChars(mediaBasePath_, media_base_path);
    env->ReleaseStringUTFChars(mediaName_, media_name);

    if (v_code == 0 && a_code == 0) {
        return 0;
    } else {
        return -1;
    }
}

JNIEXPORT jint JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_recordEnd(JNIEnv *env, jclass type) {

    // TODO
    if (h264Encoder != NULL) {
        h264Encoder->userEnd();
        h264Encoder = NULL;
    }

    if (aacEncoder != NULL) {
        aacEncoder->userEnd();
        aacEncoder = NULL;
    }

    return 0;

}

JNIEXPORT void JNICALL
Java_com_plx_video_jniInterface_FFmpegNativeBridge_nativeInit(JNIEnv *env, jclass type,
                                                              jboolean debug, jstring logUrl_) {
    const char *logUrl = env->GetStringUTFChars(logUrl_, 0);

    // TODO

    env->ReleaseStringUTFChars(logUrl_, logUrl);
}