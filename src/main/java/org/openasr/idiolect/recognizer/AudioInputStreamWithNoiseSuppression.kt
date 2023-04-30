package org.openasr.idiolect.recognizer

import org.openasr.idiolect.recognizer.filter.ComplexDouble
import org.openasr.idiolect.recognizer.filter.FFT
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.TargetDataLine
import kotlin.math.*

class AudioInputStreamWithNoiseSuppression internal constructor(line: TargetDataLine) : AudioInputStream(line) {
    private val sampleRate: Int = line.format.sampleRate.toInt()
    private val windowSize: Int = line.format.frameSize
    private val fftSize = 4096 // 2 * windowSize
    private val fftInput = DoubleArray(fftSize)
    private val fftOutput = Array(fftSize) { ComplexDouble(0.0, 0.0) }
    private val fft = FFT(fftSize)
    private val numberOfFrequencyBins = fftSize / 2 + 1
    private val magnitudes = DoubleArray(numberOfFrequencyBins)
    private val window = DoubleArray(windowSize) { 0.54 - 0.46 * cos(2 * PI * it / (windowSize - 1)) }

    //    private val noiseFloorEstimator = NoiseFloorEstimator()
    // If you want to apply the gain to all frequency bins, then the size of gainMask should be fftSize / 2 + 1.
    // If you only want to apply the gain to a subset of frequency bins, then the size of gainMask would be equal to
    // the number of frequency bins you want to apply the gain to.
    private val gainMask = DoubleArray(numberOfFrequencyBins) { 1.0 }

    //    private var noiseFloor: Double = 0.0
    private var prevNoiseFloor: Double = 0.0
    private var windowIndex = 0

    init {
        precomputeWindowFunction()
    }

    private fun precomputeWindowFunction() {
        for (i in 0 until windowSize) {
            window[i] /= fftSize.toDouble()
        }
    }

    /**
     * Will read up to 4096 bytes (as configured by VoskAsr) and apply noise suppression to the stream.
     */
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val numBytesRead = super.read(b, off, len)
        if (numBytesRead == -1) {
            return -1
        }

        applySuppression(b, off, off + numBytesRead)

        return numBytesRead
    }

    fun applySuppression(b: ByteArray, off: Int, end: Int) {
        val inputs = IntArray(10)
        val outputs = ShortArray(10)
        var minInput = 0
        var maxInput = 0
        var minOutput = 0
        var maxOutput = 0
        var minInFft = 0.0
        var maxInFft = 0.0
        var minOutFft = 0.0
        var maxOutFft = 0.0

        // step by 2 bytes because `sampleSizeInBytes = format.sampleSizeInBits / 8` = 2
        for (i in off until end step 2) {
            val sample = ((b[i + 1].toInt() shl 8) + (b[i].toInt() and 0xff))

            if (sample > maxInput) {
                maxInput = sample.toInt()
            }
            if (sample < minInput) {
                minInput = sample.toInt()
            }

            // Convert sample to double and apply window function
            fftInput[windowIndex] = window[windowIndex] * sample
//            fftInput[windowIndex] = sample.toDouble()
            windowIndex++

            if (windowIndex >= windowSize) {   // len <= 4096, windowSize = 640 bytes
                // Apply FFT
                fft.forwardTransform(fftInput, fftOutput, magnitudes, windowIndex)

                // Estimate noise floor and update gain mask
                //val noiseFloor = noiseFloorEstimator.estimateNoiseFloor(magnitudes)
//                var noiseFloor = 0.0
//                for (m in 0 until numberOfFrequencyBins step 2) {
//                    // we could multiply by 0.999  to make the noise floor estimation slightly more conservative,
//                    // as it takes into account the possibility of variations in the signal that might be mistaken for noise
//                    noiseFloor = max(0.999 * noiseFloor, magnitudes[m])
//                }

                // Update the gain mask based on the current noise floor estimate
//                if (noiseFloor > 0.0) {
//                    for (j in 0 until numberOfFrequencyBins step 1) {
////                        gainMask[j] = min(1.0, max(0.0, (noiseFloor - prevNoiseFloor) / noiseFloor))
////                        gainMask[j] = min(1.0, (noiseFloor - prevNoiseFloor) / noiseFloor)
//                        gainMask[j] = 1.0 // 10.09
//                    }
////                    prevNoiseFloor = noiseFloor
//
//                    val delta = noiseFloor - prevNoiseFloor
//                    if (delta > 0.0) {
//                        for (j in 0 until numberOfFrequencyBins step 2) {
//                            val attenuation = min(1.0, delta / noiseFloor)
//                            gainMask[j] = min(1.0, magnitudes[j] / (noiseFloor * attenuation))
//                        }
//                        prevNoiseFloor = noiseFloor
//                    }
//                }


                // Apply gain mask to the FFT output
//                for (j in 0 until numberOfFrequencyBins step 1) {
//                    fftOutput[j].real *= gainMask[j]
//
//                    if (fftOutput[j].real > maxOutFft) {  // this is actually the same range as input
//                        maxOutFft = fftOutput[j].real
//                    }
//                    if (fftOutput[j].real < minOutFft) {
//                        minOutFft = fftOutput[j].real
//                    }
//                }

                // Inverse FFT to get the denoised samples
                fft.inverseTransform(fftOutput, fftInput, magnitudes, windowIndex)

                var k = i + 2 - windowSize * 2
                for (j in 0 until windowSize) {
                    val filtered = (fftInput[j] / window[j]).toInt().toShort()

                    if (fftInput[j] > maxInFft) {
                        maxInFft = fftInput[j]
                    }
                    if (fftInput[j] < minInFft) {
                        minInFft = fftInput[j]
                    }

                    if (filtered > maxOutput) {
                        maxOutput = filtered.toInt()
                    }
                    if (filtered < minOutput) {
                        minOutput = filtered.toInt()
                    }

                    b[k++] = (filtered.toInt() and 0xFF).toByte()
                    b[k++] = (filtered.toInt() shr 8).toByte()
                }

                windowIndex = 0
            }
        }

        println("input range: $minInput to $maxInput")
        println("output range: $minOutput to $maxOutput")
        println("fft in range: $minInFft to $maxInFft")
        println("fft out range: $minOutFft to $maxOutFft")
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
