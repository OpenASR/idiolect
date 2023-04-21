package org.openasr.idiolect.presentation

import java.awt.Dimension
import javax.sound.sampled.TargetDataLine
import javax.swing.JProgressBar
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.roundToInt

class VuMeter(private var dataLine: TargetDataLine?) : JProgressBar(), Runnable {  //    JPanel() {
    private var running = false

    init {
        maximum = 100
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
                val bytesRead = read(buffer, 0, bufferSize)
                if (bytesRead == -1) {
                    break
                }

//                value = calculateRmsLevel(buffer, bytesRead)
                value = calculateMaxLevel(buffer, bytesRead)
            }
        }
    }

    fun start() {
        Thread(this).start()
    }

    fun stop() {
        running = false
        dataLine?.stop()
        dataLine?.close()
    }

    fun setDataLine(dataLine: TargetDataLine?) {
        this.dataLine = dataLine
    }

    /** Calculates the maximum level of the input samples and returns it as a percentage of the maximum possible level */
    private fun calculateMaxLevel(buffer: ByteArray, bytesRead: Int): Int {
        var maxLevel = 0.0
        for (i in 0 until bytesRead step 2) {
            val sample = ((buffer[i + 1].toInt() shl 8) or buffer[i].toInt()).toDouble() / 32768
            maxLevel = max(maxLevel, sample)
        }

        return (maxLevel * 100).toInt()
    }

    /**
     * Calculates the RMS level of the input samples, converts it to decibels (dB), and then returns it as a percentage of the maximum possible level.
     * This method takes into account the RMS level, which gives a better indication of the perceived loudness of the audio input.
     */
    private fun calculateRmsLevel(buffer: ByteArray, bytesRead: Int): Int {
        var sum = 0.0f
        for (i in 0 until bytesRead step 2) {
            val sample = ((buffer[i + 1].toInt() and 0xFF) shl 8 or (buffer[i].toInt() and 0xFF)).toFloat() / Short.MAX_VALUE
            sum += sample * sample
        }

        val rms = Math.sqrt(sum.toDouble() / (bytesRead / 2).toDouble()).toFloat()
        val db = if (rms > 0) 20.0f * log10(rms) else -80.0f
        return ((db + 80.0f) / 80.0f * 100.0f).roundToInt().coerceIn(0, 100)
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
