package org.openasr.idiolect.recognizer.filter

import org.junit.Test
import org.junit.Assert.*
import java.util.Random

class FFTTest {

//    @Test
//    fun testBitReversal() {
//        val fftSize = 8
//        val fft = FFT(fftSize)
//
//        // Index:      0   1   2   3   4   5   6   7
//        // Binary:   000 001 010 011 100 101 110 111
//        // Reversed: 000 100 010 110 001 101 011 111
//        assertArrayEquals(arrayOf(0, 4, 2, 6, 1, 5, 3, 7), fft.reverseBits.toTypedArray())
//    }

    @Test
    fun testTwiddleFactors() {
        val fftSize = 8
        val fft = FFT(fftSize)

        val expectedTwiddleFactors = arrayOf(
            ComplexDouble(1.0, 0.0),
            ComplexDouble(0.7071067811865476, -0.7071067811865475),
            ComplexDouble(6.123233995736766e-17, -1.0),
            ComplexDouble(-0.7071067811865477, -0.7071067811865475),
        )

        for (i in 0 until fftSize / 2) {
            assertEquals(expectedTwiddleFactors[i].real, fft.twiddleFactors[i].real, 1e-6)
            assertEquals(expectedTwiddleFactors[i].imag, fft.twiddleFactors[i].imag, 1e-6)
        }
    }

    @Test
    fun testForwardTransform() {
        val fftSize = 8
        val fft = FFT(fftSize)

        val input = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
        // maybe * 1/sqrt(fftSize)
        val expectedOutput = arrayOf(
            ComplexDouble(36.0,0.0),
            ComplexDouble(-4.0, 9.65685425),
            ComplexDouble(-4.0, 4.0),
            ComplexDouble(-4.0, 1.65685425),
            ComplexDouble(-4.0, 0.0),
            ComplexDouble(-4.0, -1.65685425),
            ComplexDouble(-4.0, -4.0),
            ComplexDouble(-4.0, -9.65685425),
//            ComplexDouble(36.0, 0.0),
//            ComplexDouble(-4.0, 4.0),
//            ComplexDouble(-4.0, 0.0),
//            ComplexDouble(-4.0, -4.0),
//            ComplexDouble(-4.0, 0.0),
//            ComplexDouble(-4.0, 4.0),
//            ComplexDouble(-4.0, 0.0),
//            ComplexDouble(-4.0, -4.0)
        )

        val output = Array(fftSize) { ComplexDouble(0.0, 0.0) }
        fft.forwardTransform(input, output)

        assertArrayEquals(expectedOutput, output)
    }

    @Test
    fun testForwardTransform1D() {
        val fftSize = 8
        val fft = FFT(fftSize)

        val input = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
        // maybe * 1/sqrt(fftSize)
//        val expectedOutput = arrayOf(
//            ComplexDouble(36.0,0.0),
//            ComplexDouble(-4.0, 9.65685425),
//            ComplexDouble(-4.0, 4.0),
//            ComplexDouble(-4.0, 1.65685425),
//            ComplexDouble(-4.0, 0.0),
//            ComplexDouble(-4.0, -1.65685425),
//            ComplexDouble(-4.0, -4.0),
//            ComplexDouble(-4.0, -9.65685425),
////            ComplexDouble(36.0, 0.0),
////            ComplexDouble(-4.0, 4.0),
////            ComplexDouble(-4.0, 0.0),
////            ComplexDouble(-4.0, -4.0),
////            ComplexDouble(-4.0, 0.0),
////            ComplexDouble(-4.0, 4.0),
////            ComplexDouble(-4.0, 0.0),
////            ComplexDouble(-4.0, -4.0)
//        )

        val output = DoubleArray(fftSize)
        fft.forwardTransform(input, output)

        println(output)
//        assertArrayEquals(expectedOutput, output)
    }

    @Test
    fun testForwardAndInverseTransform() {
        val fftSize = 4096
        val fft = FFT(fftSize)

        val random = Random(42)
        val input = DoubleArray(fftSize) { random.nextDouble() }

        val outputFreqDomain = Array(fftSize) { ComplexDouble(0.0, 0.0) }
        val outputTimeDomain = DoubleArray(fftSize)

        // Perform forward transform
        fft.forwardTransform(input, outputFreqDomain)

        // Perform inverse transform
        fft.inverseTransform(outputFreqDomain, outputTimeDomain)

        // Compare input and output within a tolerance
        for (i in 0 until fftSize) {
            assertEquals(input[i], outputTimeDomain[i], 1e-6)
        }
    }
}
