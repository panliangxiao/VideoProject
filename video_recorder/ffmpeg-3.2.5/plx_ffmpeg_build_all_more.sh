

chmod a+x plx_ffmpeg_*.sh

cd libx264
chmod a+x x264_build_all.sh
./x264_build_all.sh

cd ..
cd fdk-aac-0.1.5
chmod a+x fdk_aac_build_all.sh
./fdk_aac_build_all.sh

cd ..

# Build arm v6
./plx_ffmpeg_arm_build_more.sh

# Build arm  v7a
./plx_ffmpeg_arm_v7a_build_more.sh

# Build arm64 v8a
./plx_ffmpeg_arm64_v8a_build_more.sh

# Build x86
./plx_ffmpeg_x86_build_more.sh

# Build x86_64
./plx_ffmpeg_x86_64_build_more.sh
