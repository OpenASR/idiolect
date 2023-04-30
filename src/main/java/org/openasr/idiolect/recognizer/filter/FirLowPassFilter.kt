package org.openasr.idiolect.recognizer.filter

import kotlin.math.sin
import kotlin.math.PI

/**
 * A low pass filter implemented using the FIR algorithm.
 *
 * FIR filters will not produce any unwanted oscillations or introduce distortion, but requrire more multiplication operations.
 *

 * @param taps the number of coefficients in the filter. The higher the number, the more accurate the filter will be.
 *
 * For speech processing, a common range for the number of taps is between 100 and 500.
 * A filter with 100 taps can provide a reasonable frequency response for speech signals with a sampling rate of 16 kHz,
 * while a filter with 500 taps can provide a better frequency response with less passband ripple and stopband attenuation.
 * However, the optimal number of taps may vary depending on the specific application and requirements.
 */
class FirLowPassFilter(cutoffFrequency: Int, sampleRate: Int = 16000, private val taps: Int = 100) : Filter {
    private val delayLine = DoubleArray(taps)
    private val coefficients = generateCoefficients(cutoffFrequency, sampleRate, taps);

    override fun filterSample(sample: Int): Int {
        var output = 0.0

        // Shift values in the delay line and add new sample to the beginning
        for (i in 0 until taps - 1) {
            delayLine[i] = delayLine[i + 1]
        }
        delayLine[taps - 1] = sample.toDouble()

        // Compute weighted sum of delay line values and filter coefficients
        for (i in coefficients.indices) {
            output += delayLine[i] * coefficients[i]
        }

        // Return filtered sample as 16-bit signed integer
        return output.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
    }

//    private fun generateCoefficients(cutoffFrequency: Int, sampleRate: Int, taps: Int): DoubleArray {
//        val coefficients = DoubleArray(taps)
//
//        for (i in 1 until taps) {
//            val normalizedFrequency = (2.0 * PI * i) / sampleRate
//            coefficients[i] = sin(normalizedFrequency) / normalizedFrequency
//        }
//
//        return coefficients
//    }

    private fun generateCoefficients(cutoffFrequency: Int, sampleRate: Int, taps: Int): DoubleArray {
        val coefficients = DoubleArray(taps)
        val nyquistFrequency = sampleRate / 2
        val normalizedCutoffFrequency = cutoffFrequency.toDouble() / nyquistFrequency

        for (i in 0 until taps) {
            val normalizedFrequency = PI * (i - (taps - 1) / 2)
            if (i == (taps - 1) / 2) {
                coefficients[i] = 2 * normalizedCutoffFrequency
            } else {
                coefficients[i] = sin(2 * normalizedCutoffFrequency * normalizedFrequency) / normalizedFrequency
            }
        }

        return coefficients
    }

//    private fun generateCoefficients(cutoffFrequency: Int, sampleRate: Int, taps: Int): DoubleArray {
//        val coefficients = DoubleArray(taps)
//
//        // Normalize cutoff frequency to the range [0, 0.5] where 0.5 represents the Nyquist frequency
//        val nyquistFrequency = sampleRate / 2
//        val normalizedCutoffFrequency =  2 * PI * cutoffFrequency / nyquistFrequency
//
//        // Calculate filter coefficients using windowed sinc filter design
//        for (i in 0 until taps) {
//            val tapsFactor = (i - taps / 2)
//
//            val sinc = if (i == taps / 2) {
//                normalizedCutoffFrequency
//            } else {
//                sin(normalizedCutoffFrequency * tapsFactor) / tapsFactor
//            }
//
//            // Use a Hamming window to smooth the filter's transition band
//            val window = 0.54 - 0.46 * cos(2 * PI * i / (taps - 1))
//
//            coefficients[i] = sinc * window
//        }
//
//        return coefficients
//    }
}
