package org.openasr.idiolect.presentation.toolwindow.audio

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.recognizer.CustomMicrophone
import org.openasr.idiolect.utils.AudioUtils
import java.awt.Dimension
import javax.swing.JProgressBar
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max

open class VuMeter(private val microphone: CustomMicrophone, private val mode: Int = MAX_MODE) : JProgressBar(), Runnable, Disposable {
    private val log = logger<VuMeter>()
    private var thread: Thread? = null

    companion object {
        const val MAX_MODE = 0
        const val RMS_MODE = 1
    }

    init {
        maximum = Short.MAX_VALUE.toInt() // 32767
        preferredSize = Dimension(150, 10)
        isStringPainted = false
    }

    override fun run() {
        val bufferSize = microphone.getLine()!!.bufferSize / 5
        val buffer = ByteArray(bufferSize)

//        log.debug("starting VuMeter thread, ${Thread.currentThread().id}")
        while (true) {
            try {
                val bytesRead = microphone.read(buffer, bufferSize)
                if (bytesRead <= 0) {
//                    log.debug("VuMeter read $bytesRead bytes, ${Thread.currentThread().id}")
                    break
                }

                value = if (mode == MAX_MODE) calculateMaxLevel(buffer, bytesRead)
                else calculateRmsLevel(buffer, bytesRead)
            } catch (ie: InterruptedException) {
//                log.debug("interrupted VuMeter thread, ${Thread.currentThread().id}")
                break
            }
        }
//        log.debug("exiting VuMeter thread, ${Thread.currentThread().id}")
    }

    fun start() {
        if (thread?.isAlive != true) {
            thread = Thread(this, "Idiolect VU Meter").apply { start() }
        }
    }

    fun stop() {
        thread?.interrupt()
        thread = null
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
