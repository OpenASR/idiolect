package org.openasr.idiolect.recognizer.filter

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

/**
 * @See FirLowPassFilter
 */
class FirHighPassFilter(cutoffFrequency: Int, sampleRate: Int = 16000, private val taps: Int = 100) : Filter {
    private val delayLine = DoubleArray(taps)
    private val coefficients: DoubleArray = generateCoefficients(cutoffFrequency, sampleRate, taps);

    override fun filterSample(sample: Int): Int {
        var output = 0.0
        delayLine[0] = sample.toDouble()

        for (i in coefficients.indices) {
            output += coefficients[i] * delayLine[i]
        }

        // Shift delay line
//        for (i in delayLine.lastIndex downTo 1) {
        for (i in (taps - 2) downTo 0) {
            delayLine[i + 1] = delayLine[i]
        }

        return output.toInt()
    }

    fun getTaps() = taps

    private fun generateCoefficients(cutoffFrequency: Int, sampleRate: Int, taps: Int): DoubleArray {
        val coefficients = DoubleArray(taps)

        val omegaC = 2 * PI * cutoffFrequency / sampleRate
        for (n in 0 until taps) {
            val t = (n - (taps - 1) / 2.0) / sampleRate
            if (t == 0.0) {
                coefficients[n] = 1.0 - omegaC / PI
            } else {
                coefficients[n] = -sin(omegaC * t) / (PI * t)
            }
        }

        return coefficients
    }
}







