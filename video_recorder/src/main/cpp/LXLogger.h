//
// Created by plx on 18/8/3.
//

#ifndef FFMPEGPROJECT_LX_LOGGER_H
#define FFMPEGPROJECT_LX_LOGGER_H

#include <android/log.h>

extern int JNI_DEBUG;

#define LOGE(debug, format, ...) if(debug){__android_log_print(ANDROID_LOG_ERROR, "lx_ffmpeg", format, ##__VA_ARGS__);}
#define LOGI(debug, format, ...) if(debug){__android_log_print(ANDROID_LOG_INFO, "lx_ffmpeg", format, ##__VA_ARGS__);}
#define LOGD(debug, format, ...) if(debug){__android_log_print(ANDROID_LOG_DEBUG, "lx_ffmpeg", format, ##__VA_ARGS__);}


#endif //FFMPEGPROJECT_LX_LOGGER_H
