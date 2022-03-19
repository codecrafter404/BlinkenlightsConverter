package me._4o4.blinkenlightsconverter.thread

import me._4o4.blinkenlightsconverter.models.FramePage
import org.opencv.core.Size

class ThreadConstants {

    // make static
    companion object {
        // thread ID | Calculated threads
        val frameTable = mutableMapOf<Int, MutableList<FramePage>>()
        var millisPerFrame = 0.0
        var animationSize = Size()
        var animationRatio = 0.0
        var depth = 0.0
    }
}