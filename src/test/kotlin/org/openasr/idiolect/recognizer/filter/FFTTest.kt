package org.openasr.idiolect.recognizer.filter

import org.junit.Test
import org.junit.Assert.*
import kotlin.math.abs
import kotlin.math.sqrt

class FFTTest {
    @Test
    fun testForwardAndInverseTransform() {
        val fftSize = 16
        val numBins = fftSize / 2 + 1
        val fft = FFT(fftSize)

        // Generate a test signal with random values between -1.0 and 1.0
        val windowSize = 32
        val input = DoubleArray(fftSize) { -1.0 + 2.0 * Math.random() }
        val output = Array(fftSize) { ComplexDouble(0.0, 0.0) }
        val magnitudes = DoubleArray(numBins) { 0.0 }

        // Transform the input signal
        fft.forwardTransform(input, output, magnitudes, windowSize)

        // Make sure the spectrum has the expected size
        assertEquals(fftSize, output.size)

        // Test the symmetry of the spectrum
        var zeros = Int.MAX_VALUE
        for (i in 0 until fftSize) {
            if (output[i].real == 0.0 && output[i].imag == 0.0) {
                zeros++
            }
            assertEquals(output[i], output[(fftSize - i) % fftSize].conjugate())
        }
        assertNotEquals("Spectrum is all 0", 0, zeros)

        // Verify that the magnitude of the output matches the expected values
        for (i in 0 until numBins) {
            val expectedMagnitude = sqrt(output[i].real * output[i].real + output[i].imag * output[i].imag)
            assertEquals(expectedMagnitude, magnitudes[i], 1e-10)
        }

        // Inverse transform the spectrum
        fft.inverseTransform(output, input, magnitudes, windowSize)

        // Test the similarity of the input and output signals
        for (i in 0 until fftSize) {
            assertTrue(abs(input[i] - output[i].real) < 1e-9)
        }
    }

    /*@Test
    fun testRealForwardAndInverseTransform() {
        val size = 16
        val fft = FFT(size)

        // Generate a test signal with random values between -1.0 and 1.0
        val input = DoubleArray(size) { -1.0 + 2.0 * Math.random() }

        // Transform the input signal
        val spectrum = fft.realForwardTransform(input)

        // Make sure the spectrum has the expected size
        assertEquals(size / 2 + 1, spectrum.size)

        // Test the symmetry of the spectrum
        for (i in 1 until size / 2) {
            assertEquals(spectrum[i], spectrum[(size - i) % size].conjugate())
        }

        // Inverse transform the spectrum
        val output = fft.realInverseTransform(spectrum)

        // Make sure the output has the same size as the input
        assertEquals(size, output.size)

        // Test the similarity of the input and output signals
        for (i in 0 until size) {
            assertTrue(abs(input[i] - output[i]) < 1e-9)
        }
    }*/
}
