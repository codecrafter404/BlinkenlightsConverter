package me._4o4.blinkenlightsconverter.utils

import org.opencv.videoio.VideoCapture
import org.opencv.videoio.Videoio
import java.io.File

class VideoUtils {
    companion object {
        fun getVideo(file: File): VideoCapture = VideoCapture(file.absolutePath)
        fun getVideoFramesCount(vid: VideoCapture) = vid.get(Videoio.CAP_PROP_FRAME_COUNT)
        fun getVideoFramesPerSecond(vid: VideoCapture) = vid.get(Videoio.CAP_PROP_FPS)
        fun getVideoLengthMillis(vid: VideoCapture) = getVideoFramesCount(vid) / getVideoFramesPerSecond(vid) * 1000
        fun getVideoMillisPerFrame(vid: VideoCapture) = getVideoLengthMillis(vid) / getVideoFramesCount(vid)
    }
}