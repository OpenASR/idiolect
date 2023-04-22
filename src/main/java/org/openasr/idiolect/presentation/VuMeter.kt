package org.openasr.idiolect.presentation

import com.intellij.openapi.Disposable
import org.openasr.idiolect.utils.AudioUtils
import java.awt.Dimension
import java.io.InputStream
import javax.sound.sampled.TargetDataLine
import javax.swing.JProgressBar
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

open class VuMeter(private var dataLine: TargetDataLine?, private val mode: Int = MAX_MODE) : JProgressBar(), Runnable, Disposable {
    private var running = false
    private var stream: InputStream? = null

    companion object {
        val MAX_MODE = 0
        val RMS_MODE = 1
    }

    init {
        maximum = Short.MAX_VALUE.toInt() // 32767
        preferredSize = Dimension(150, 10)
        isStringPainted = false
    }

    override fun run() {
        if (dataLine == null) {
            value = 0
            return
        }

        dataLine?.apply {
            val bufferSize = bufferSize / 5
            val buffer = ByteArray(bufferSize)
            start()

            running = true
            while (running) {
                val bytesRead = stream?.read(buffer, 0, bufferSize) ?: read(buffer, 0, bufferSize)
                if (bytesRead == -1) {
                    break
                }

                value = if (mode == MAX_MODE) calculateMaxLevel(buffer, bytesRead)
                    else calculateRmsLevel(buffer, bytesRead)
            }
        }
    }

    fun start() {
        if (!running) {
            Thread(this).start()
        }
    }

    fun stop() {
        running = false
    }

    fun setDataLine(dataLine: TargetDataLine?) {
        this.dataLine = dataLine
    }

    fun setStream(stream: InputStream) {
        this.stream = stream
    }

    override fun dispose() {
        stop()
    }

    /** Calculates the maximum level of the input samples */
    private fun calculateMaxLevel(buffer: ByteArray, bytesRead: Int): Int {
        var maxLevel = 0

        AudioUtils.readLittleEndianShorts(buffer, bytesRead) { sample ->
            val s = abs(sample.toInt())
            maxLevel = max(maxLevel, s)
        }

        return maxLevel
    }

    /**
     * Calculates the RMS level of the input samples, converts it to decibels (dB).
     * This method takes into account the RMS level, which gives a better indication of the perceived loudness of the audio input.
     * @return 0 to Short.MAX_VALUE (32767)
     */
    private fun calculateRmsLevel(buffer: ByteArray, bytesRead: Int): Int {
        var sum = 0.0

        AudioUtils.readLittleEndianShorts(buffer, bytesRead) { sample ->
            val s = sample.toFloat() / Short.MAX_VALUE
            sum += s * s
        }

        val rms = Math.sqrt(sum / (bytesRead / 2))
        val db = 20.0 * log10(rms)  // should be < 0
        // noisy hum of laptop: -53 to -49
        // person quietly humming: -42
        // talking: -28
        // loud talking: -16
        val dBquietNoise = 50
        val scaled = ((db + dBquietNoise) / dBquietNoise * Short.MAX_VALUE.toDouble()).toInt().coerceIn(0, Short.MAX_VALUE.toInt())
        return scaled
    }
}



//    override fun paintComponent(g: Graphics) {
//        super.paintComponent(g)
//
//        // calculate the height of the green bar based on the current level
//        val greenHeight = (height * level).toInt()
//
//        // draw the red background bar
//        g.color = JBColor.RED
//        g.fillRect(0, 0, width, height)
//
//        // draw the green level bar on top of the red background bar
//        g.color = JBColor.GREEN
//        g.fillRect(0, height - greenHeight, width, greenHeight)
//    }
//
