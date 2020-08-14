//
// Created by plx on 18/8/23.
//

#ifndef FFMPEGPROJECT_LXYUVENCODEH264_H
#define FFMPEGPROJECT_LXYUVENCODEH264_H

#include "base_include.h"
#include "LXMediaArgument.h"

/**
 * yuv编码h264
 */
class LXYUVEncodeH264 {
public:
    LXYUVEncodeH264(LXMediaArguments* arg);
public:
    int initVideoEncoder();

    static void* startEncode(void * obj);

    int startSendOneFrame(uint8_t *buf);

    void userEnd();

    void release();

    int encodeEnd();

    void customFilter(const LXYUVEncodeH264 *h264_encoder, const uint8_t *picture_buf,
                       int in_y_size,
                       int format);
    ~LXYUVEncodeH264() {
    }
private:
    int flushEncoder(AVFormatContext *fmt_ctx, unsigned int stream_index);

private:
    LXMediaArguments *arguments;
    int is_end = 0;
    int is_release = 0;
    threadsafe_queue<uint8_t *> frame_queue;
    AVFormatContext *pFormatCtx;
    AVOutputFormat *fmt;
    AVStream *video_st;
    AVCodecContext *pCodecCtx;
    AVCodec *pCodec;
    AVPacket pkt;
    AVFrame *pFrame;
    int picture_size;
    int out_y_size;
    int framecnt = 0;
    int frame_count = 0;


};

#endif //FFMPEGPROJECT_LXYUVENCODEH264_H
