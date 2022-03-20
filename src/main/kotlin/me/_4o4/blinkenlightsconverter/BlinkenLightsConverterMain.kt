package me._4o4.blinkenlightsconverter

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import me._4o4.blinkenlightsconverter.models.FramePage
import me._4o4.blinkenlightsconverter.thread.ConversionThread
import me._4o4.blinkenlightsconverter.thread.ThreadConstants
import me._4o4.blinkenlightsconverter.utils.FrameUtils
import me._4o4.blinkenlightsconverter.utils.VideoUtils
import nu.pattern.OpenCV
import org.tinylog.kotlin.Logger
import java.io.File
import java.io.StringWriter
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.pow

class BlinkenLightsConverterMain : CliktCommand() {

    private val width: Int by option(help = "The width of the Animation", names = arrayOf("-w", "--width")).int().default(30).check("Value must be grater than one") { it > 1}
    private val height: Int by option(help = "The height of the Animation", names = arrayOf("-h", "--height")).int().default(12).check("Value must be grater than one") { it > 1}
    private val depth: Int by option(help = "The bytes used for depth information", names = arrayOf("-d", "--depth")).int().default(4).check("Value must between 1 and 4") { it in 1..4 }
    private val video: File by option(help = "The video file to convert from", names = arrayOf("-v", "--video")).file().required().check("File must exist") {it.exists()}
    private val output: File by option(help = "The output file", names = arrayOf("-o", "--output")).file().required().check("Must be an .bml file") { it.name.endsWith(".bml")}
    private val threads: Int by option(help = "The thread that will be used to convert asynchronous", names = arrayOf("-t", "--threads")).int().default(Runtime.getRuntime().availableProcessors()).check("Value must be grater than zero") {it > 0}

    override fun run() {
        Logger.info("Using $threads threads to convert the video")
        Logger.info("Using ${2.0.pow(depth)} different gray steps")
        Logger.info("Converting to animation of size ${width}x$height")

        Logger.info("Initialize OpenCV")
        OpenCV.loadLocally()


        val vid = VideoUtils.getVideo(video)

        // setup constants for the threads
        ThreadConstants.millisPerFrame = VideoUtils.getVideoMillisPerFrame(vid)

        ThreadConstants.animationSize.width = width.toDouble()
        ThreadConstants.animationSize.height = height.toDouble()
        ThreadConstants.animationRatio = width.toDouble() / height.toDouble()

        ThreadConstants.depth = 2.0.pow(depth) / 256


        // schedule threads
        val frames = FrameUtils.generateFrames(
            VideoUtils.getVideoFramesCount(vid),
            50 / VideoUtils.getVideoMillisPerFrame(vid) // skip x frames per 50 milliseconds to get the 20 fps mark
        )

        val executorService = Executors.newCachedThreadPool()
        for((id, threadList) in FrameUtils.splitFramesToThreads(frames, threads).withIndex()){
            executorService.execute(ConversionThread(threadList, id, video))
        }
        // wait for completion
        executorService.shutdown()
        while(!executorService.awaitTermination(1, TimeUnit.SECONDS)) { Logger.info("Executing tasks...")}

        val converted = mutableListOf<FramePage>()
        ThreadConstants.frameTable.forEach{ it.value.forEach { x -> converted.add(x) }}
        Logger.info("Converted ${converted.size}/${frames.size} frames")

        Logger.info("Generating xml...")

        // template
        val templateMap = mutableMapOf<String, Any>()
        templateMap["width"] = width
        templateMap["height"] = height
        templateMap["depth"] = depth
        templateMap["duration"] = converted.size * 50
        templateMap["frames"] = converted

        val conf = Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
        conf.defaultEncoding = "UTF-8"
        conf.locale = Locale.GERMAN
        conf.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        conf.setClassForTemplateLoading(BlinkenLightsConverterMain::class.java, "/")

        val template = conf.getTemplate("template.ftl")
        val writer = StringWriter()
        template.process(templateMap, writer)
        Logger.info("Writing output to ${output.absolutePath}")

        output.writeText(writer.toString())

        Logger.info("Theoretical animation size: ${width.toDouble() * height.toDouble() * converted.size.toDouble() * depth.toDouble() / 8.0 / 1024.0} Kb")
    }
}