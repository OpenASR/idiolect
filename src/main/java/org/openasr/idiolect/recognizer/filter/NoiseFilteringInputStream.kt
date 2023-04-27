package org.openasr.idiolect.recognizer.filter

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.TargetDataLine

/**
 * Removes background ambient noise such as laptop fans fan noise from an audio signal by applying a notch filter
 * to the frequency spectrum of the signal using an FFT.
 *
 * <p>The notch filter is designed to remove frequency components within
 * a small frequency range around the fan noise frequency. This frequency
 * range is defined by the `fanNoiseFrequency` parameter, which should be
 * set to the frequency of the fan noise in Hz. The width of the frequency
 * range is defined by the `notchWidth` parameter, which specifies the
 * number of frequency bins on either side of the fan noise frequency to
 * zero out.
 *
 * <p>The audio signal is first converted to an array of doubles and then
 * padded with zeros to the next power of two to optimize the FFT algorithm.
 * The FFT is then applied to the padded signal to obtain the frequency
 * spectrum. The spectrum is modified by zeroing out the frequency bins
 * around the fan noise frequency using a rectangular notch filter. The
 * inverse FFT is then applied to the modified spectrum to obtain the
 * filtered audio signal.
 *
 * <p>Note that this method assumes that the fan noise frequency is constant
 * throughout the duration of the audio signal. If the fan noise frequency
 * varies over time, this method may not be effective.
 */

/**
 * Removes background ambient noise such as laptop fans fan noise from an audio signal.
 *
 * @param TargetDataLine The target data line from which this stream obtains its data
 * @param noiseFrequency The frequency of the fan noise in Hz.
 * @param notchWidth The width of the frequency range to remove around the fan noise frequency, in number of frequency bins.
 */
class NoiseFilteringInputStream constructor(line: TargetDataLine,
                                            /*sampleRate: Float, sampleSize: Int, bigEndian: Boolean = false,*/
                                            noiseFrequency: Int, notchWidth: Int) : AudioInputStream(line) {
    // Calculate the fan noise frequency in radians per sample.
//    val noiseFrequencyRadiansPerSample = noiseFrequency / format.sampleRate * 2 * Math.PI

    // Calculate the notch width in radians per sample.
//    val notchWidthRadiansPerSample = notchWidth / format.sampleRate * 2 * Math.PI
//    val notchFrequency = 60.0
//    val notchWidth = 2.0
    val filter = NotchFilter(line.format, format.sampleRate, noiseFrequency, notchWidth)

    override fun read(b: ByteArray, offset: Int, len: Int): Int {
        val bytesRead = super.read(b, offset, len)
        val end = offset + bytesRead

        for (i in offset until end step 2) {
            filter.filterSample(b, i)
        }

        return bytesRead
    }
}
