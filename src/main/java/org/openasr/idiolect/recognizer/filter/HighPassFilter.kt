package org.openasr.idiolect.recognizer.filter

/**
 * A high-pass filter.
 *
 * If you are trying to remove low-frequency noise from an audio signal,
 * you would want to choose a cutoff frequency that is below the frequency of the noise.
 *
 * @param cutoffFrequency The frequency of the high-pass filter.
 * @param hzPerSample The number of hertz per sample.
 */
class HighPassFilter(
    private val cutoffFrequency: Int,
    private val hzPerSample: Int
) : Filter {
//    private val alpha = 1.0f - 2.0f / (2.0f + Math.PI * cutoffFrequency * hzPerSample)
//
//    override fun filterSample(sample: Int): Int {
//        val filteredSample = (sample * alpha - (1.0f - alpha) * sample)
//        return filteredSample.toInt()
//    }

    private val coefficients = generateCoefficients(cutoffFrequency, hzPerSample)

    private fun generateCoefficients(cutoffFrequency: Int, hzPerSample: Int): DoubleArray {
        val n = 2 * cutoffFrequency / hzPerSample + 1
        val coefficients = DoubleArray(n)
        for (i in 1 until n) {
            coefficients[i] =
                ((1 - Math.cos(2 * Math.PI * i * cutoffFrequency / hzPerSample)) /
                (2 * Math.PI * i * cutoffFrequency / hzPerSample))
        }
        return coefficients
    }

    override fun filterSample(sample: Int): Int {
        var filteredSample = 0
        for (i in 0 until coefficients.size) {
            filteredSample += (coefficients[i] * sample).toInt()
        }
        return filteredSample
    }
}
