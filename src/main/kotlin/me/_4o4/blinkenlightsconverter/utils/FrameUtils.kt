package me._4o4.blinkenlightsconverter.utils

class FrameUtils {
    companion object{
        fun generateFrames(end: Double, step: Double): List<Double>{
            val ret = mutableListOf<Double>()
            var x = 0
            while(x <= end){
                val calc = x * step
                if(calc > end){
                    break
                }
                ret.add(calc)
                x++
            }
            return ret
        }

        fun splitFramesToThreads(frames: List<Double>, threads: Int): List<List<Double>>{
            val ret = mutableListOf<MutableList<Double>>()
            for(thread in 1 .. threads){
                ret.add(mutableListOf())
            }
            var currentThread = 1
            frames.forEach {
                ret[currentThread - 1].add(it)
                if(currentThread + 1 > threads){
                    currentThread = 1
                }else {
                    currentThread++
                }
            }
            return ret
        }
    }
}