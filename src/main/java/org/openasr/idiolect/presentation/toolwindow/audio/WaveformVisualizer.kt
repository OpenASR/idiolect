package org.openasr.idiolect.presentation.toolwindow.audio

import com.intellij.openapi.Disposable
import com.intellij.ui.JBColor
import org.openasr.idiolect.recognizer.CustomMicrophone
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JPanel

class WaveformVisualizer(private val microphone: CustomMicrophone) : JPanel(), Runnable, Disposable {
    private var running = false
    private val maxSamples = 512
    private val height = 256
    private val yScale = Short.MAX_VALUE * 2 / height
    private val xPoints = IntArray(maxSamples) { it }
    private val yPoints = IntArray(maxSamples) { 0 }

    init {
        preferredSize = Dimension(maxSamples, 256)
        setToZero()
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2d = g as Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Draw the background
        g2d.color = JBColor.WHITE
        g2d.fillRect(0, 0, width - 1, height - 1)
        g2d.color = JBColor.GRAY
        g2d.drawRect(0, 0, width - 1, height - 1)

        // Draw the data points
        g2d.color = JBColor.BLUE
        g2d.drawPolyline(xPoints, yPoints, maxSamples)
    }


    override fun run() {
        // A larger buffer size can result in smoother audio playback but may also introduce latency.
        // If the buffer size is too small, it can result in choppy playback or gaps in the audio.
        val bufferSize = maxSamples * 2 //  min(maxSamples, 1024) // dataLine!!.bufferSize / 10
        val b = ByteArray(bufferSize)

        running = true
        while (running) {
            // Read `bufferSize` bytes at a time & update the waveformSeries
            val numBytesRead = microphone.read(b, bufferSize)
            if (numBytesRead == -1) return

            // Update waveform visualizer with samples
            for (i in 0 until numBytesRead step 2) {
                val sample = ((b[i + 1].toInt() shl 8) or (b[i].toInt() and 0x00FF))
                yPoints[i shr 1] = (sample - Short.MIN_VALUE) / yScale
            }

            repaint()
        }
    }

    fun start() {
        Thread(this, "Idiolect Waveform Visualizer").start()
    }

    fun stop() {
        running = false
        setToZero()
    }

    fun isRunning() = running

    override fun dispose() {
        stop()
    }

    private fun setToZero() {
        val zeroLevel = height / 2
        for (i in 0 until maxSamples) {
            yPoints[i] = zeroLevel
        }
        repaint()
    }
}
