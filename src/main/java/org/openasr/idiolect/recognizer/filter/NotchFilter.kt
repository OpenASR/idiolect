package org.openasr.idiolect.recognizer.filter

import javax.sound.sampled.AudioFormat

/**
 * A notch filter that removes a narrow band of frequencies from an audio signal.
 * The filter works by combining a low-pass and high-pass filter to create a band-stop filter
 * that removes a specific frequency band (notch) from the audio signal.
 *
 * @param format the audio format of the input signal
 * @param sampleRate the sample rate of the audio signal, in Hz
 * @param notchFrequency the center frequency of the notch to remove, in Hz
 * @param notchWidth the width of the notch to remove, in Hz.
 *  It determines the range of frequencies that will be filtered out on either side of the center frequency.
 *  A wider notch width will filter out more frequencies, but may also remove some desired frequencies.
 *  A narrower notch width will filter out less, but may not remove all unwanted frequencies.
 */
class NotchFilter(
    private val format: AudioFormat,
    private val sampleRate: Float,
    private val notchFrequency: Int,
    private val notchWidth: Int
) : Filter {
    private val hzPerSample = Filter.calculateHzPerSample(sampleRate, format.sampleSizeInBits)
    private val notchRadius = notchWidth / 2

    // The low-pass filter that removes frequencies below the notch frequency.
    private val lowPassFilter = LowPassFilter(notchFrequency - notchRadius, hzPerSample)
    // The high-pass filter that removes frequencies above the notch frequency.
    private val highPassFilter = HighPassFilter(notchFrequency + notchRadius, hzPerSample)

    override fun filterSample(sample: Int): Int {
        return lowPassFilter.filterSample(sample) - highPassFilter.filterSample(sample)
    }
}
