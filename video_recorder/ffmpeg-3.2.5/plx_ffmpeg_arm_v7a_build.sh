# Created by jianxi on 2017/6/4
# https://github.com/mabeijianxi
# mabeijianxi@gmail.com

#!/bin/bash
basepath=$(cd `dirname $0`; pwd)
export TMPDIR=$basepath/ffmpegtemp
# NDK的路径，根据自己的安装位置进行设置
NDK=/Users/plx/Library/Android/android-ndk-r13b
# 编译针对的平台，可以根据自己的需求进行设置
# 这里选择最低支持android-14, arm架构，生成的so库是放在
# libs/armeabi文件夹下的，若针对x86架构，要选择arch-x86
PLATFORM=$NDK/platforms/android-14/arch-arm
# 工具链的路径，根据编译的平台不同而不同
# arm-linux-androideabi-4.9与上面设置的PLATFORM对应，4.9为工具的版本号，
# 根据自己安装的NDK版本来确定，一般使用最新的版本
TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.9/prebuilt/darwin-x86_64

CPU=arm-v7a

echo $basepath

FDK_INCLUDE=$basepath/fdk-aac-0.1.5/android/arm/include

FDK_LIB=$basepath/fdk-aac-0.1.5/android/arm/lib

X264_INCLUDE=$basepath/libx264/android/arm/include

X264_LIB=$basepath/libx264/android/arm/lib

FF_EXTRA_CFLAGS="-DANDROID -fPIC -ffunction-sections -funwind-tables -fstack-protector -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -fomit-frame-pointer -fstrict-aliasing -funswitch-loops -finline-limit=300 "
FF_CFLAGS="-O3 -Wall -pipe \
-ffast-math \
-fstrict-aliasing -Werror=strict-aliasing \
-Wno-psabi -Wa,--noexecstack \
-DANDROID  "
PREFIX=./android/$CPU

rm "./compat/strtod.o"

build_one(){
./configure \
--prefix=$PREFIX \
--enable-cross-compile \
--disable-runtime-cpudetect \
--disable-asm \
--arch=arm \
--target-os=android \
--cc=$TOOLCHAIN/bin/arm-linux-androideabi-gcc \
--cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
--disable-stripping \
--nm=$TOOLCHAIN/bin/arm-linux-androideabi-nm \
--sysroot=$PLATFORM \
--extra-cflags="-I$X264_INCLUDE  -I$FDK_INCLUDE " \
--extra-ldflags="-L$FDK_LIB -L$X264_LIB" \
--enable-gpl \
--enable-shared \
--disable-static \
--enable-version3 \
--enable-pthreads \
--enable-small \
--disable-vda \
--disable-iconv \
--disable-encoders \
--enable-libx264 \
--enable-neon \
--enable-yasm \
--enable-libfdk_aac \
--enable-encoder=libx264 \
--enable-encoder=libfdk_aac \
--enable-encoder=mjpeg \
--enable-encoder=png \
--enable-nonfree \
--enable-muxers \
--enable-muxer=mov \
--enable-muxer=mp4 \
--enable-muxer=h264 \
--enable-muxer=avi \
--disable-decoders \
--enable-decoder=aac \
--enable-decoder=aac_latm \
--enable-decoder=h264 \
--enable-decoder=mpeg4 \
--enable-decoder=mjpeg \
--enable-decoder=png \
--disable-demuxers \
--enable-demuxer=image2 \
--enable-demuxer=h264 \
--enable-demuxer=aac \
--enable-demuxer=avi \
--enable-demuxer=mpc \
--enable-demuxer=mpegts \
--enable-demuxer=mov \
--disable-parsers \
--enable-parser=aac \
--enable-parser=ac3 \
--enable-parser=h264 \
--enable-protocols \
--enable-zlib \
--enable-avfilter \
--disable-outdevs \
--disable-ffprobe \
--disable-ffplay \
--disable-ffmpeg \
--disable-ffserver \
--disable-debug \
--disable-ffprobe \
--disable-ffplay \
--disable-ffmpeg \
--disable-postproc \
--disable-avdevice \
--disable-symver \
--disable-stripping \
--extra-cflags="$FF_EXTRA_CFLAGS  $FF_CFLAGS" \
--extra-ldflags="  "
}
build_one


make clean
make -j16
make install

cp $FDK_LIB/libfdk-aac.so $PREFIX/lib

