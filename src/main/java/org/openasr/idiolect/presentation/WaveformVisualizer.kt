package org.openasr.idiolect.presentation

import com.intellij.openapi.application.invokeLater
import com.intellij.ui.JBColor
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.openasr.idiolect.utils.AudioUtils
import java.awt.Dimension
import java.io.InputStream
import javax.sound.sampled.TargetDataLine
import javax.swing.JPanel
import kotlin.math.min

class WaveformVisualizer : JPanel(), Runnable {
    private val dataset = XYSeriesCollection()
    private val waveformSeries = XYSeries("Waveform")
    private val chart: JFreeChart
    private var dataLine: TargetDataLine? = null
    private var stream: InputStream? = null
    private var running = false
    private val maxSamples = 1024

    init {
//        preferredSize = Dimension(1000, 500)
        dataset.addSeries(waveformSeries)
        chart = createChart(dataset)
        chart.removeLegend()
        val chartPanel = ChartPanel(chart)
        chartPanel.preferredSize = Dimension(600, 300)
        add(chartPanel)
    }

    override fun run() {
        // A larger buffer size can result in smoother audio playback but may also introduce latency.
        // If the buffer size is too small, it can result in choppy playback or gaps in the audio.
        val bufferSize = min(maxSamples, 1024) // dataLine!!.bufferSize / 10

        running = true
        while (running) {
            val b = ByteArray(bufferSize)
            val read = stream!!.read(b, 0, bufferSize)
//            val read = dataLine!!.read(b, 0, bufferSize)
            if (read == -1) break

            // Update waveform visualizer with samples
            invokeLater {
                clear()
                AudioUtils.readLittleEndianShorts(b, read) { addSample(it) }
            }
        }
    }

    fun start() {
        Thread(this).start()
    }

    fun stop() {
        running = false
        clear()
    }

    fun isRunning() = running

    fun setDataLine(dataLine: TargetDataLine?) {
        this.dataLine = dataLine
    }

    fun setStream(stream: InputStream) {
        this.stream = stream
    }

    private fun createChart(dataset: XYSeriesCollection): JFreeChart {
        val xAxis = NumberAxis()
        xAxis.isAutoRange = false
        xAxis.lowerBound = 0.0
        xAxis.upperBound = (maxSamples / 2).toDouble()
        xAxis.isTickLabelsVisible = false
        xAxis.isAxisLineVisible = false

        val yAxis = NumberAxis()
        yAxis.isAutoRange = false
        yAxis.lowerBound = -32768.0
        yAxis.upperBound = 32767.0
        yAxis.isTickLabelsVisible = false
        yAxis.isAxisLineVisible = false

        val renderer = XYLineAndShapeRenderer()
        renderer.setSeriesPaint(0, JBColor.BLUE)
        renderer.setSeriesShapesVisible(0, false)
        renderer.drawSeriesLineAsPath = true

        val plot = XYPlot(dataset, xAxis, yAxis, renderer)
        plot.orientation = PlotOrientation.VERTICAL
        plot.isDomainGridlinesVisible = false
        plot.isRangeGridlinesVisible = false

        return JFreeChart(plot)
    }

    fun addSample(sample: Short) {
        waveformSeries.add(waveformSeries.itemCount.toDouble(), sample)
    }

    fun clear() {
        waveformSeries.clear()
    }
}
