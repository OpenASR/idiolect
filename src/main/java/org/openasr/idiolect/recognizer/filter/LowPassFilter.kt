package org.openasr.idiolect.recognizer.filter

/**
 * If you are trying to remove high-frequency noise from an audio signal,
 * you would want to choose a cutoff frequency that is above the frequency of the noise.
 *
 * Here are some common cutoff frequencies for low-pass filters:
 *
 * 20 Hz: This is a good general-purpose value for removing high-frequency noise.
 * 100 Hz: This is a good value for removing human voices from audio signals.
 * 250 Hz: This is a good value for removing low-frequency rumble from audio signals.
 * 1 kHz: This is a good value for separating different instruments in an audio mix.
 * 10 kHz: This is a good value for removing high-frequency hiss from audio signals.
 *
 * @param cutoffFrequency the frequency above which the filter will attenuate the signal.
 * @param hzPerSample The number of hertz per sample.
 */
class LowPassFilter(
    private val cutoffFrequency: Int,
    private val hzPerSample: Int
) : Filter {
//    // The alpha coefficient is a value between 0 and 1.0f. A value of 0.0f means that the filter will pass all frequencies.
//    // A value of 1.0f means that the filter will attenuate all frequencies.
//    // The alpha coefficient is calculated so that the filter passes low frequencies and attenuates high frequencies.
//    private val alpha = 2.0f / (2.0f + Math.PI * cutoffFrequency * hzPerSample)
//
//    override fun filterSample(sample: Int): Int {
//        val filteredSample = (sample * alpha + (1.0f - alpha) * sample)
//        return filteredSample.toInt()
//    }

    private val coefficients = generateCoefficients(cutoffFrequency, hzPerSample)

    private fun generateCoefficients(cutoffFrequency: Int, hzPerSample: Int): DoubleArray {
        val n = 2 * (cutoffFrequency / hzPerSample + 1).toInt()
        val coefficients = DoubleArray(n)
        for (i in 1 until n) {
            coefficients[i] = Math.sin(2 * Math.PI * i * cutoffFrequency / hzPerSample) / (2 * Math.PI * i * cutoffFrequency / hzPerSample)
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
