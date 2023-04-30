package org.openasr.idiolect.recognizer.filter

import java.util.concurrent.RecursiveAction
import kotlin.math.*


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
    private val expTable = DoubleArray(fftSize / 2)
    private val cosTable = DoubleArray(fftSize / 2)
    private val sinTable = DoubleArray(fftSize / 2)
    private val reverseBits = IntArray(fftSize)

    init {
        // Precompute twiddle factors
        for (i in 0 until fftSize / 2) {
            val angle = -2.0 * PI * i / fftSize
            expTable[i] = exp(angle)
            cosTable[i] = cos(angle)
            sinTable[i] = sin(angle)
        }

        // Precompute bit reversal table
        for (i in 0 until fftSize) {
            var j = 0
            var bit = 1
            while (bit < fftSize) {
                if (i and bit != 0) {
                    j += fftSize / bit / 2
                }
                bit = bit shl 1
            }
            reverseBits[i] = j
        }
    }

    /**
     * Computes the forward Fourier transform of the given input using the Cooley-Tukey algorithm.
     *
     * @param input the input samples in time domain
     * @param output a buffer to which the frequency domain complex numbers will be written
     * @param magnitudes a buffer to which the magnitudes will be written
     * @param len the number of samples to process
     */
    fun forwardTransform(input: DoubleArray, output: Array<ComplexDouble>, magnitudes: DoubleArray, len: Int) {
       val fftSize = this.fftSize

        // Bit-reverse input samples
        for (i in 0 until fftSize) {
            val j = reverseBits[i]
            if (j > i) {
                val tmpReal = input[i]
                input[i] = input[j]
                input[j] = tmpReal
            }
        }


//        val x = DoubleArray(len)
//        val log2n = log2(len.toDouble()).toInt()
//        val expTable = this.expTable
//        // Radix-2 algorithm
//        for (s in 1 until log2n + 1) {
//            val m = 1 shl s
//            val wM = exp(-2 * PI / m)
//
//            for (k in 0 until len step m) {
//                var w = ComplexDouble(1.0, 0.0)
//                for (j in k until k + m / 2) {
//                    val t = w * x[j + m / 2]
//                    val u = x[j]
//                    x[j] = u + t.real
//                    x[j + m / 2] = u - t.real
//                    w *= wM
//                }
//            }
//        }

        // Perform Cooley-Tukey FFT algorithm
        val halfLen = len / 2
        var j = halfLen
        for (i in 0 until halfLen) {
            val k = i * 2
            val arg = -2.0 * PI * i / len
            val wReal = cos(arg)
            val wImag = sin(arg)
            val a = ComplexDouble(input[k], 0.0)
            val b = ComplexDouble(input[k + 1], 0.0)
            val c = ComplexDouble(wReal, wImag)
            val d = c * b
            val e = a + d
            val f = a - d
            output[i] = e
            output[j] = f

            // Compute magnitude of each output sample
            magnitudes[i] = sqrt(e.real * e.real + e.imag * e.imag)
            magnitudes[j++] = sqrt(f.real * f.real + f.imag * f.imag)
        }
    }

    /**
     * Computes the inverse FFT of the given complex input signal, and returns the resulting real-valued time-domain signal.
     *
     * @param input the complex-valued frequency-domain signal to be transformed
     * @param output a buffer to which the real-valued time-domain signal obtained by computing the inverse FFT of the input will be written
     * @param magnitudes a buffer to which the magnitudes will be written
     * @param len the number of samples to process
     */
    fun inverseTransform(input: Array<ComplexDouble>, output: DoubleArray, magnitudes: DoubleArray, len: Int) {
        // Compute complex conjugate of input samples
        for (i in 0 until len) {
            input[i] = input[i].conjugate()
        }

        // Apply forward FFT
        forwardTransform(output, input, magnitudes, len)

        // Compute complex conjugate of output samples and scale
        val scale = 1.0 / len
        for (i in 0 until len) {
            input[i] = input[i].conjugate() * scale
            output[i] = input[i].real
        }
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


data class ComplexDouble(var real: Double, val imag: Double) {
    operator fun plus(other: ComplexDouble) = ComplexDouble(real + other.real, imag + other.imag)
    operator fun minus(other: ComplexDouble) = ComplexDouble(real - other.real, imag - other.imag)
    operator fun times(other: ComplexDouble) = ComplexDouble(real * other.real - imag * other.imag, real * other.imag + imag * other.real)
    operator fun times(scale: Double) = ComplexDouble(real * scale, imag * scale)


    fun magnitude() = sqrt(real * real + imag * imag)
    fun conjugate(): ComplexDouble {
        return ComplexDouble(real, -imag)
    }

    operator fun div(other: ComplexDouble): ComplexDouble {
        val denominator = other.real * other.real + other.imag * other.imag
        val realPart = (real * other.real + imag * other.imag) / denominator
        val imagPart = (imag * other.real - real * other.imag) / denominator
        return ComplexDouble(realPart, imagPart)
    }

    operator fun unaryMinus(): ComplexDouble {
        return ComplexDouble(-real, -imag)
    }

    override fun toString(): String {
        return "$real + ${imag}i"
    }
}
