package org.openasr.idiolect.recognizer.filter

import kotlin.math.*

/**
 * Active Noise Cancelling (ANC) using a Finite Impulse Response (FIR) filter.
 *
 * The filter coefficients can be trained based on a sample of the background noise, or manually set by the user.
 *
 * @param cutoffFrequency determines the frequency above which the active noise cancelling filter will attempt to attenuate the noise
 * @param sampleRate the sample rate of the audio data
 * @param taps the number of coefficients in the filter. The higher the number, the more accurate the filter will be.
 * @param initialCoefficients the initial filter coefficients (default is null)
 * @param epsilon is
 */
class ActiveNoiseCancelling(
    private val cutoffFrequency: Int,
    private val sampleRate: Int = 16000,
    private val taps: Int = 100,
    initialCoefficients: DoubleArray? = null
) : Filter {
    private var delayLine = DoubleArray(taps)
    private val error = DoubleArray(taps)
    var coefficients: DoubleArray = initialCoefficients ?: DoubleArray(taps)
    private var mu: Double = 0.1
    private var epsilon: Double = 1.0
    private var enabled: Boolean = true

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    /**
     * @Param mu Determines how fast the filter adapts to the input signal.
     *          A smaller value of mu will lead to a slower adaptation, while a larger value of mu can lead to
     *          instability and divergence of the filter coefficients.
     *          Defaults to 0.1, Can be >= 0, typically 0.01 or 0.1
     */
    fun setStepSize(mu: Double) {
        this.mu = mu
    }

    /**
     * @param epsilon A small positive regularization parameter that is added to the denominator of the LMS update
     *          equation to prevent division by zero and to ensure numerical stability of the algorithm.
     *          Defaults to 1.0, typically range from 0.01 to 0.1,
     */
    fun setConvergenceFactor(epsilon: Double) {
        this.epsilon = epsilon
    }

    /**
     * Trains the filter coefficients using LMS algorithm.
     * Call this function with background noise audio data to generate new coefficients.
     *
     * @param audioData the audio data containing a sample of the background noise
     * @param durationInSeconds the duration of the audio data sample in seconds
     * @param sampleRate the sample rate of the audio data
     */
    fun trainCoefficients(audioData: ByteArray): DoubleArray {
        // Convert the audio data to double precision floating point format
        val samples = DoubleArray(audioData.size / 2)
        for (i in samples.indices) {
            samples[i] = ((audioData[i * 2 + 1].toInt() shl 8) + (audioData[i * 2].toInt() and 0xff)).toDouble()
        }

        // Normalize the cutoff frequency to a fraction of the sample rate
        val normalizedCutoff = cutoffFrequency.toDouble() / sampleRate.toDouble()

        // Generate the ideal filter response for the given cutoff frequency
        val idealResponse = DoubleArray(taps)
        for (i in idealResponse.indices) {
            val x = 2.0 * Math.PI * normalizedCutoff * (i - (taps - 1) / 2.0)
            idealResponse[i] = if (x == 0.0) 2.0 * normalizedCutoff else Math.sin(x) / x
        }

        // Initialize the filter coefficients to random values
        coefficients = DoubleArray(taps) { Math.random() * 2.0 - 1.0 }

        // Iterate over the samples and update the coefficients
        for (sample in samples) {
            // Filter the sample using the current coefficients
            var output = 0.0
            for (i in coefficients.indices) {
                output += coefficients[i] * delayLine[i]
            }
            delayLine[0] = sample

            // Compute the error between the filtered output and the ideal filter response
            val error = idealResponse[0] - output

            // Update the coefficients using the LMS algorithm
            for (i in coefficients.indices) {
                val d = delayLine[i]
                coefficients[i] += mu * error * d / (epsilon + d * d)
            }
        }

        return coefficients
    }



    /**
     * Filters a single audio sample using the active noise cancelling filter.
     *
     * @param sample the audio sample to filter
     * @return the filtered audio sample
     */
    override fun filterSample(sample: Int): Int {
        if (!enabled) {
            return sample
        }

        val doubleSample = sample.toDouble()
        var output = 0.0

        for (i in coefficients.indices) {
            output += coefficients[i] * delayLine[i]
        }

        delayLine[0] = doubleSample + mu * output

        for (i in (delayLine.size - 1) downTo 1) {
            delayLine[i] = delayLine[i - 1]
        }

        if (output < -32767) {
            output = -32767.0
        } else if (output > 32767) {
            output = 32767.0
        }

        return output.toInt()
    }

/*    fun findBestParameters(
        anc: ActiveNoiseCancelling,
        audioData: ByteArray,
        durationInSeconds: Double,
        sampleRate: Int,
        muValues: DoubleArray,
        epsilonValues: DoubleArray
    ): Pair<Double, Double> {
        var bestMu = 0.0
        var bestEpsilon = 0.0
        var lowestError = Double.MAX_VALUE

        for (mu in muValues) {
            for (epsilon in epsilonValues) {
                anc.setStepSize(mu)
                anc.setConvergenceFactor(epsilon)
                anc.trainCoefficients(audioData, durationInSeconds, sampleRate)

                // Compute the error on a validation set (assuming one is available)
                val validationError = computeValidationError(anc)

                if (validationError < lowestError) {
                    lowestError = validationError
                    bestMu = mu
                    bestEpsilon = epsilon
                }
            }
        }

        return Pair(bestMu, bestEpsilon)
    }

    fun computeValidationError(anc: ActiveNoiseCancelling): Double {
        // TODO: Replace with your own validation data
        val validationData = ByteArray(0)
        val samples = DoubleArray(validationData.size / 2)
        for (i in samples.indices) {
            samples[i] = ((validationData[i * 2 + 1].toInt() shl 8) + (validationData[i * 2].toInt() and 0xff)).toDouble()
        }

        var error = 0.0
        for (sample in samples) {
            val output = anc.filterSample(sample.toInt())
            error += (output - sample).pow(2)
        }

        return error / samples.size
    }*/


}
