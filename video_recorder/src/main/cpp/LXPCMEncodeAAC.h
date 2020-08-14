//
// Created by plx on 18/8/22.
//

#ifndef FFMPEGPROJECT_LXPCMENCODEAAC_H
#define FFMPEGPROJECT_LXPCMENCODEAAC_H

#include "base_include.h"
#include "LXMediaArgument.h"

/**
 * pcm转码aac
 */
class LXPCMEncodeAAC{
public:
    LXPCMEncodeAAC(LXMediaArguments *arguments);
    ~LXPCMEncodeAAC(){};
public:
    int initAudioEncoder();

    static void* startEncode(void* obj);

    void userEnd();

    void release();

    int sendOneFrame(uint8_t* buf);

    int encodeEnd();

private:
    int flushEncoder(AVFormatContext *fmt_ctx, unsigned int stream_index);

private:
    threadsafe_queue<uint8_t *> frame_queue;
    AVFormatContext *pFormatCtx;
    AVOutputFormat *fmt;
    AVStream *audio_st;
    AVCodecContext *pCodecCtx;
    AVCodec *pCodec;

    AVFrame *pFrame;
    AVPacket pkt;

    int got_frame = 0;
    int ret = 0;
    int size = 0;

    int i;
    int is_end=LX_FALSE;
    int is_release=LX_FALSE;
    LXMediaArguments *arguments;
};


#endif //FFMPEGPROJECT_LXPCMENCODEAAC_H
