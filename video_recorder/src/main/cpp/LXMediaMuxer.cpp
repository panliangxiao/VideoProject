//
// Created by plx on 18/8/9.
//

#include "LxMediaMuxer.h"
#ifdef __cplusplus
extern "C" {
#endif
#include "ffmpeg_cmd_run.h"
#ifdef __cplusplus
}
#endif

int LXMediaMuxer::startMuxer(const char *video, const char *audio, const char *outFile) {
    size_t in_filename_v_size = strlen(video);
    char *new_in_filename_v = (char *)malloc(in_filename_v_size+1);
    strcpy((new_in_filename_v), video);

    size_t in_filename_a_size = strlen(audio);
    char *new_in_filename_a = (char *)malloc(in_filename_a_size+1);
    strcpy((new_in_filename_a), audio);

    size_t out_filename_size = strlen(outFile);
    char *new_out_filename = (char *)malloc(out_filename_size+1);
    strcpy((new_out_filename), outFile);


    LOGI(JNI_DEBUG,"视音编码成功,开始合成")
    char *cmd[10];
    cmd[0]="ffmpeg";
    cmd[1]="-i";
    cmd[2]=new_in_filename_v;
    cmd[3]="-i";
    cmd[4]=new_in_filename_a;
    cmd[5]="-c:v";
    cmd[6]="copy";
    cmd[7]="-c:a";
    cmd[8]="copy";
    cmd[9]=new_out_filename;
    return ffmpeg_command_run(10, cmd);
}