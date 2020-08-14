//
// Created by plx on 18/8/9.
//

#include "base_include.h"
#include "LXJNIHandler.h"
#include "LxMediaMuxer.h"

void LXJNIHandler::setVideoState(int videoState){
    LXJNIHandler::videoState = videoState;
}

void LXJNIHandler::setAudioState(int audioState) {
    LXJNIHandler::audioState = audioState;
}


int LXJNIHandler::tryEncodeOver(LXMediaArguments *arguments) {
    if(videoState == END_STATE && audioState == END_STATE){
        startMuxer(arguments);
        return END_STATE;
    }
    return 0;
}

/**
 * 开始视频合成
 * @param arguments
 * @return
 */
int LXJNIHandler::startMuxer(LXMediaArguments *arguments) {
    LXMediaMuxer *muxer = new LXMediaMuxer();
    muxer->startMuxer(arguments->video_path, arguments->audio_path, arguments->media_path);
    delete (muxer);
    endNotify(arguments);
    return 0;
}

/**
 * 通知java层
 * @param arguments
 */
void LXJNIHandler::endNotify(LXMediaArguments *arguments) {
    try {
        int status;

        JNIEnv *env;
        status = arguments->javaVM->AttachCurrentThread(&env, NULL);
        if (status < 0) {
            LOGE(JNI_DEBUG,"callback_handler: failed to attach "
                    "current thread");
            return;
        }

        jmethodID pID = env->GetStaticMethodID(arguments->java_class, "notifyState", "(IF)V");

        if (pID == NULL) {
            LOGE(JNI_DEBUG,"callback_handler: failed to get method ID");
            arguments->javaVM->DetachCurrentThread();
            return;
        }

        env->CallStaticVoidMethod(arguments->java_class, pID, END_STATE, 0);
        env->DeleteGlobalRef(arguments->java_class);
        LOGI(JNI_DEBUG,"啦啦啦---succeed");
        arguments->javaVM->DetachCurrentThread();

    }
    catch (exception e) {
        LOGI(JNI_DEBUG,"反射回调失败");
    }

    delete (arguments);
    delete(this);
}