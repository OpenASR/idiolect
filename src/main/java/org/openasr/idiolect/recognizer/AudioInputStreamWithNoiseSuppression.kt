package org.openasr.idiolect.recognizer

import org.openasr.idiolect.recognizer.filter.ComplexDouble
import org.openasr.idiolect.recognizer.filter.FFT
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.TargetDataLine
import kotlin.math.*

class AudioInputStreamWithNoiseSuppression internal constructor(line: TargetDataLine) : AudioInputStream(line) {
//    private val sampleRate: Int = line.format.sampleRate.toInt()
//    private val windowSize: Int = line.format.frameSize
//    private val fftSize = 4096 // 2 * windowSize
    private val fftSize = line.bufferSize / 2
    private val fft = FFT(fftSize)
    private val noiseProfile = DoubleArray(fftSize)

//    private val fftInput = DoubleArray(fftSize)
//    private val fftOutput = Array(fftSize) { ComplexDouble(0.0, 0.0) }

//    private val numberOfFrequencyBins = fftSize / 2 + 1
//    private val magnitudes = DoubleArray(numberOfFrequencyBins)
//    private val window = DoubleArray(windowSize) { 0.54 - 0.46 * cos(2 * PI * it / (windowSize - 1)) }

    //    private val noiseFloorEstimator = NoiseFloorEstimator()
    // If you want to apply the gain to all frequency bins, then the size of gainMask should be fftSize / 2 + 1.
    // If you only want to apply the gain to a subset of frequency bins, then the size of gainMask would be equal to
    // the number of frequency bins you want to apply the gain to.
//    private val gainMask = DoubleArray(numberOfFrequencyBins) { 1.0 }

    //    private var noiseFloor: Double = 0.0
//    private var prevNoiseFloor: Double = 0.0
//    private var windowIndex = 0

    init {
//        precomputeWindowFunction()
    }

//    private fun precomputeWindowFunction() {
//        for (i in 0 until windowSize) {
//            window[i] /= fftSize.toDouble()
//        }
//    }

    fun estimateNoiseProfile(noise: ByteArray) {
        val input = byteArrayToShortArray(noise)
        val output = Array(fftSize) { ComplexDouble(0.0, 0.0) }
        fft.forwardTransform(input, output)
        for (i in output.indices) {
            noiseProfile[i] = sqrt(output[i].real * output[i].real + output[i].imag * output[i].imag)
        }
    }


    /**
     * Will read up to 4096 bytes (as configured by VoskAsr) and apply noise suppression to the stream.
     */
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val numBytesRead = super.read(b, off, len)
        if (numBytesRead > 0) {
            val audio = byteArrayToShortArray(b.sliceArray(off until off + numBytesRead))
            val suppressed = suppressNoise(audio)
            val byteArray = shortArrayToByteArray(suppressed)
            byteArray.copyInto(b, off)
        }
        return numBytesRead
    }

    /**
     * Note - The noise suppression could be done more smoothly by using a more sophisticated algorithm like
     * spectral subtraction or Wiener filtering
     */
    private fun suppressNoise(audio: DoubleArray): DoubleArray {
        val output = Array(fftSize) { ComplexDouble(0.0, 0.0) }
        fft.forwardTransform(audio, output)
        for (i in output.indices) {
            val magnitude = sqrt(output[i].real * output[i].real + output[i].imag * output[i].imag)
            if (magnitude < noiseProfile[i]) {
                output[i] = ComplexDouble(0.0, 0.0)
            }
        }
        val suppressed = DoubleArray(fftSize)
        fft.inverseTransform(output, suppressed)
        return suppressed
    }

    private fun byteArrayToShortArray(b: ByteArray): DoubleArray {
        return DoubleArray(b.size / 2) { i -> ((b[i * 2 + 1].toInt() shl 8) + (b[i * 2].toInt() and 0xff)).toDouble() }
    }

    private fun shortArrayToByteArray(s: DoubleArray): ByteArray {
        val b = ByteArray(s.size * 2)
        for (i in s.indices) {
            val sample = s[i].toInt()
            b[i * 2] = (sample and 0xff).toByte()
            b[i * 2 + 1] = ((sample shr 8) and 0xff).toByte()
        }
        return b
    }
}

//    private class NoiseFloorEstimator {
//        private var noiseFloor = 1.0
//
//        fun estimateNoiseFloor(magnitude: DoubleArray): Double {
//            for (i in 0 until magnitude.size) {
//                noiseFloor = max(0.999 * noiseFloor, magnitude[i])
//            }
//            return noiseFloor
//        }
//    }
//}
