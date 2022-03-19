package me._4o4.blinkenlightsconverter.thread

import me._4o4.blinkenlightsconverter.models.FramePage
import me._4o4.blinkenlightsconverter.utils.VideoUtils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.opencv.videoio.Videoio
import org.tinylog.kotlin.Logger
import java.io.File
import kotlin.math.round

class ConversionThread(
    private val frames: List<Double>,
    private val threadID: Int,
    private val video: File
) : Runnable {
    override fun run() {
        Logger.info("Starting thread with id $threadID; ${frames.size} frames")

        ThreadConstants.frameTable[threadID] = mutableListOf()

        val vid = VideoUtils.getVideo(video)

        for(frame in frames){
            vid.set(Videoio.CAP_PROP_FRAME_COUNT, frame)

            val mat = Mat()

            if(!vid.read(mat)){
                Logger.warn("Skipping frame $frame; returned false")
                continue
            }

            val imageX = mat.cols().toDouble()
            val imageY = mat.rows().toDouble()

            // convert frame
            var borderX = 0.0
            var borderY = 0.0

            val imageRatio = imageX / imageY

            if(imageRatio > ThreadConstants.animationRatio){
                // Borders will be on the top and on the bottom
                borderY = (((ThreadConstants.animationSize.height / ThreadConstants.animationSize.width) * imageX) - imageY) / 2
            }else{
                // Borders will be on the right-hand and on the left-hand site
                borderX = (((ThreadConstants.animationSize.width / ThreadConstants.animationSize.height) * imageY) - imageX) / 2
            }

            // add borders
            Core.copyMakeBorder(mat, mat, borderY.toInt(), borderY.toInt(), borderX.toInt(), borderX.toInt(), Core.BORDER_CONSTANT, Scalar(0.0))

            // resize image
            Imgproc.resize(mat, mat, ThreadConstants.animationSize)

            // convert to grayscale
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)

            // convert to FramePage -> easier to save
            val framePage = FramePage()
            for(y in 0 .. (ThreadConstants.animationSize.height - 1).toInt()){

                val stringBuilder = StringBuilder()
                for(x in 0 .. (ThreadConstants.animationSize.width - 1).toInt()){
                    stringBuilder.append(calculateGraySteps(mat[y, x][0]).toString(16)) // convert to hex
                }

                framePage.addRow(stringBuilder.toString())
            }

            ThreadConstants.frameTable[threadID]!!.add(framePage)
        }

        vid.release()
        Logger.info("Thread $threadID finished execution")
    }

    private fun calculateGraySteps(input: Double): Int = round(input * ThreadConstants.depth).toInt()
}