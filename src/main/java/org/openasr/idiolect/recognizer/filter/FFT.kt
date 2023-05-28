package org.openasr.idiolect.recognizer.filter

import java.util.concurrent.RecursiveAction
import kotlin.math.*
//import org.apache.commons.math3.transform.FastFourierTransformer


/**
 * A class for performing Fast Fourier Transform (FFT) calculations.
 *
 * The FFT algorithm is implemented using a radix-2 decimation in time algorithm,
 * with precomputed tables for the trigonometric constants. The trigonometric
 * constants are computed using the Cooley-Tukey FFT algorithm, which is a
 * divide-and-conquer algorithm for computing the DFT of a sequence.
 *
 * This implementation uses double precision for the input and output arrays.
 *
 * For more information on FFT, see the following sources:
 * - Cooley, James W., and John W. Tukey. "An algorithm for the machine calculation of complex Fourier series." Mathematics of computation 19.90 (1965): 297-301.
 * - Brigham, E. Oran. The fast Fourier transform and its applications. Prentice Hall, 1988.
 * - https://en.wikipedia.org/wiki/Fast_Fourier_transform
 *
 * @param fftSize defaults to 4096
 */
class FFT(private val fftSize: Int = 4096) {
    internal val twiddleFactors: Array<ComplexDouble>
    internal val twiddle: Array<Double>

    init {
        require(fftSize > 0 && (fftSize and (fftSize - 1)) == 0) {
            "FFT size must be a power of 2"
        }

        // Precompute twiddle factors
        twiddleFactors = Array(fftSize / 2) { i ->
            val angle = -2 * PI * i / fftSize
            ComplexDouble(cos(angle), sin(angle))
        }

        twiddle = Array(fftSize / 2) { i ->
//            val angle = -2 * PI * i / fftSize
//            cos(angle) + sin(angle) * i
            exp(-2 * PI * i)
        }
    }

    /**
     * Computes the forward Fourier transform of the given input using the Cooley-Tukey algorithm.
     * This implementation assumes that the input size is a power of 2. If the input size is not a power of 2,
     * need to pad the input with zeros.
     *
     * @param input the input samples in time domain
     * @param output a buffer to which the frequency domain complex numbers will be written
     * @param magnitudes a buffer to which the magnitudes will be written
     * @param len the number of samples to process
     */
    fun forwardTransform(input: DoubleArray, output: Array<ComplexDouble>) {
        require(input.size == fftSize) { "Input size must be equal to FFT size" }
        require(output.size == fftSize) { "Output size must be equal to FFT size" }

//        transform(input.copyOf(), output, 0, fftSize, 1)
        transform(input, output, 0, fftSize, 1)
    }

    fun forwardTransform(input: DoubleArray, output: DoubleArray) {
        require(input.size == fftSize) { "Input size must be equal to FFT size" }
        require(output.size == fftSize) { "Output size must be equal to FFT size" }

//        transform(input.copyOf(), output, 0, fftSize, 1)
        transform(input, output, 0, fftSize, 1)
    }

    /**
     * Computes the inverse FFT of the given complex input signal, and returns the resulting real-valued time-domain signal.
     *
     * @param input the complex-valued frequency-domain signal to be transformed
     * @param output a buffer to which the real-valued time-domain signal obtained by computing the inverse FFT of the input will be written
     * @param magnitudes a buffer to which the magnitudes will be written
     * @param len the number of samples to process
     */
    fun inverseTransform(input: Array<ComplexDouble>, output: DoubleArray) {
        val data = input.copyOf()
        inverseTransformInternal(data, 0, fftSize, 1)

        for (i in data.indices) {
            output[i] = data[i].real / (fftSize / 2)
            data[i].real = output[i]
            data[i].imag = 0.0
        }

        transform(output, data, 0, fftSize, 1)
        for (i in data.indices) {
            output[i] = data[i].real
        }
    }

    private fun transform(data: DoubleArray, output: Array<ComplexDouble>, start: Int, end: Int, step: Int) {
        val n = end - start

        if (n == 1) {
            output[start] = ComplexDouble(data[start], 0.0)
            return
        }

        val evenStart = start
        val evenEnd = start + n / 2
        val oddStart = start + n / 2
        val oddEnd = end

        transform(data, output, evenStart, evenEnd, 2 * step)
        transform(data, output, oddStart, oddEnd, 2 * step)

        for (k in 0 until n / 2) {
            val t = output[oddStart + k] * twiddleFactors[k * step]
            output[start + k] = output[evenStart + k] + t
            output[oddStart + k] = output[evenStart + k] - t
        }
    }

    private fun transform(data: DoubleArray, output: DoubleArray, start: Int, end: Int, step: Int) {
        val n = end - start

        if (n <= 1) {
            output[start] = data[start]
            return
        }

        val evenStart = start
        val evenEnd = start + n / 2
        val oddStart = start + n / 2
        val oddEnd = end

        transform(data, output, start, evenEnd, 2 * step)
        transform(data, output, oddStart, end, 2 * step)

        for (k in 0 until n / 2) {
            val even = output[start + k]
            val odd = output[oddStart + k] * exp(k * -2 * PI / n)
         // val odd = output[oddStart + k] * twiddle[k * step]
            output[start + k] = even + odd
            output[oddStart + k] = even - odd
        }
    }

    private fun inverseTransformInternal(data: Array<ComplexDouble>, start: Int, end: Int, step: Int) {
        val n = end - start

        if (n == 1) {
            return
        }

        val evenStart = start
        val evenEnd = start + n / 2
        val oddStart = start + n / 2
        val oddEnd = end

        inverseTransformInternal(data, evenStart, evenEnd, 2 * step)
        inverseTransformInternal(data, oddStart, oddEnd, 2 * step)

        for (k in 0 until n) {
            val t = data[oddStart + k] * twiddleFactors[k * step].conjugate()
            data[start + k] = data[evenStart + k] + t
            data[start + k + n / 2] = data[evenStart + k] - t
        }
    }
}


data class ComplexDouble(var real: Double, var imag: Double) {
    operator fun plus(other: ComplexDouble) = ComplexDouble(real + other.real, imag + other.imag)
    operator fun minus(other: ComplexDouble) = ComplexDouble(real - other.real, imag - other.imag)
    operator fun times(other: ComplexDouble) = ComplexDouble(real * other.real - imag * other.imag, real * other.imag + imag * other.real)
    operator fun times(scale: Double) = ComplexDouble(real * scale, imag * scale)

    fun magnitude() = sqrt(real * real + imag * imag)
    fun conjugate() = ComplexDouble(real, -imag)

    operator fun div(other: ComplexDouble): ComplexDouble {
        val denominator = other.real * other.real + other.imag * other.imag
        val realPart = (real * other.real + imag * other.imag) / denominator
        val imagPart = (imag * other.real - real * other.imag) / denominator
        return ComplexDouble(realPart, imagPart)
    }

    operator fun unaryMinus() = ComplexDouble(-real, -imag)

    override fun toString(): String {
        return "$real + $imag i"
    }
}


///**
// * ForkJoinPool pool = new ForkJoinPool();
// * double[] result = pool.invoke(new ParallelFFTTask(data));
// */
//internal class ParallelFFTTask(private val data: DoubleArray) : RecursiveAction() {
//    private val start = 0
//    private val end: Int
//
//    init {
//        end = data.size - 1
//    }
//
//    override fun compute() {
//        if (end - start <= 1) {
//            // Compute the FFT of the current block.
//            val fft: DoubleArray = FFT.computeFFT(data, start, end)
//
//            // Store the result of the FFT in the output array.
//            for (i in start..end) {
//                result.get(i) = fft[i]
//            }
//        } else {
//            // Split the current block into two smaller blocks.
//            val mid = (start + end) / 2
//            val left = ParallelFFTTask(data, start, mid)
//            val right = ParallelFFTTask(data, mid + 1, end)
//
//            // Fork the two smaller blocks to be computed in parallel.
//            left.fork()
//            right.fork()
//
//            // Wait for the two smaller blocks to be computed.
//            left.join()
//            right.join()
//
//            // Combine the results of the two smaller blocks to form the result of the current block.
//            for (i in start..end) {
//                result.get(i) = left.result.get(i) + right.result.get(i)
//            }
//        }
//    }
//}
