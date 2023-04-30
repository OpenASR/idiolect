//package org.openasr.idiolect.recognizer.filter
//
//import kotlin.math.*
//
//class NoiseSuppressor(val sampleRate: Int = 16000, val frameSize: Int = 32000) {
//    private val fft = FFT()
//    private val fftSize = 2 * frameSize
//    private val fftInput = DoubleArray(frameSize)
//    private val fftOutput = Array<ComplexDouble>(frameSize) { ComplexDouble(0.0, 0.0) }
//    private val window = DoubleArray(frameSize) { 0.54 - 0.46 * cos(2 * PI * it / (frameSize - 1)) }
//    private val noiseFloorEstimator = NoiseFloorEstimator()
//    private val gainMask = DoubleArray(frameSize) { 1.0 }
//    private var prevNoiseFloor: Double = 0.0
//    private var windowIndex = 0
//
//    init {
//        precomputeWindowFunction()
//    }
//
//    private fun precomputeWindowFunction() {
//        for (i in 0 until frameSize) {
//            window[i] /= fftSize.toDouble()
//        }
//    }
//
////    fun filterSample(sample: Int): Int {
////        // Convert sample to double and apply window function
////        val x = window.getOrElse(windowIndex) { 0.0 } * sample.toDouble()
////        windowIndex = (windowIndex + 1) % window.size
////
////        // Compute FFT and separate into magnitude and phase components
////        val sampleFft = fft.forwardTransform(x)
////        val magnitude = DoubleArray(frameSize) { i -> sqrt(sampleFft[i].real * sampleFft[i].real + sampleFft[i].imag * sampleFft[i].imag) }   // .norm() = real * real + imag * imag
////        val phase = DoubleArray(frameSize) { i -> atan2(sampleFft[i].imag, sampleFft[i].real) }  // .argument() = atan2(imag, real)
////
////        // Update noise floor estimate
////        val noiseFloor = noiseFloorEstimator.estimateNoiseFloor(magnitude)
////        val alpha = if (noiseFloor > prevNoiseFloor) 0.1 else 0.01
////        prevNoiseFloor = noiseFloor
////        for (i in 0 until frameSize) {
////            gainMask[i] = max(gainMask[i] * alpha, magnitude[i] / noiseFloor)
////        }
////
////        // Apply gain mask and reconstruct FFT
////        for (i in 0 until frameSize) {
////            sampleFft[i] = sampleFft[i].times(gainMask[i])
////            sampleFft[i] = ComplexDouble(cos(phase[i]), sin(phase[i])) * sampleFft[i]
////        }
////        val filteredX = fft.inverseTransform(sampleFft)
////
////        // Convert back to integer and return
////        return filteredX.roundToInt()
////    }
//
//    fun filterSamples(samples: IntArray, numberOfSamples: Int) {
//        for (i in 0 until numberOfSamples) {
//            // Convert sample to double and apply window function
//            val x = window.getOrElse(windowIndex) { 0.0 } * samples[i].toDouble()
//            fftInput[i % frameSize] = x
//
//            // Compute FFT and separate into magnitude and phase components
//            if ((i + 1) % frameSize == 0) {
//                fft.forwardTransform(fftInput, fftOutput)
//
//                for (j in 0 until frameSize) {
//                    val magnitude = sqrt(fftOutput[j].real * fftOutput[j].real + fftOutput[j].imag * fftOutput[j].imag)
//                    val phase = atan2(fftOutput[j].imag, fftOutput[j].real)
//
//                    // Update noise floor estimate
//                    val noiseFloor = noiseFloorEstimator.estimateNoiseFloor(magnitude)
//                    val alpha = if (noiseFloor > prevNoiseFloor) 0.1 else 0.01
//                    prevNoiseFloor = noiseFloor
//
//                    gainMask[j] = max(gainMask[j] * alpha, magnitude / noiseFloor)
//                    val filteredMagnitude = magnitude * gainMask[j]
//                    fftOutput[j] = filteredMagnitude * cos(phase) to filteredMagnitude * sin(phase)
//                }
//
//                fft.inverseTransform(fftOutput, fftInput)
//
//                for (j in 0 until frameSize) {
//                    // Convert back to integer and store in filtered samples array
//                    samples[i - frameSize + j + 1] = fftInput[j].roundToInt()
//                }
//            }
//
//            // Update window index
//            windowIndex = (windowIndex + 1) % window.size
//        }
//    }
//
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
