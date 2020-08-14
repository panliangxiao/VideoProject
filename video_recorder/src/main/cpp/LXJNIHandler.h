//
// Created by plx on 18/8/9.
//

#ifndef FFMPEGPROJECT_JNI_HANDLER_H
#define FFMPEGPROJECT_JNI_HANDLER_H

#include "LXMediaArgument.h"

class LXJNIHandler{
    ~LXJNIHandler(){

    }
public:
    void setVideoState(int videoState);
    void setAudioState(int audioState);
    int tryEncodeOver(LXMediaArguments* arguments);
    void endNotify(LXMediaArguments* arguments);

private:
    int startMuxer(LXMediaArguments *arguments);
private:
    int videoState;
    int audioState;
};

#endif //FFMPEGPROJECT_JNI_HANDLER_H
