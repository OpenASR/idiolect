package org.openasr.idiolect.recognizer.filter

/**
 * Fast Fourier Transform implementation for real-valued input data.
 * This class computes the forward and inverse Fourier transforms of
 * a real-valued input array using the Cooley-Tukey algorithm.
 *
 * <p>The forward Fourier transform of an input array of size N returns
 * an output array of size N/2+1, where the first element is the DC
 * (or average) component of the signal and the remaining elements
 * are the complex Fourier coefficients. The inverse Fourier transform
 * of an input array of size N/2+1 returns an output array of size N,
 * which should be identical to the original input array.
 *
 * <p>This implementation is based on the description given in the
 * book "Numerical Recipes in C", Second Edition, by William H. Press,
 * Saul A. Teukolsky, William T. Vetterling, and Brian P. Flannery.
 *
 * <p>Example usage:
 * <pre>{@code
 *   // Compute the Fourier transform of a real-valued input array
 *   val input = doubleArrayOf(1.0, 2.0, 3.0, 4.0)
 *   val fft = FFT(input.size)
 *   val output = fft.forward(input)
 *
 *   // Compute the inverse Fourier transform of the output array
 *   val inverse = fft.inverse(output)
 * }</pre>
 *
 * @param size The size of the input data to transform.
 */
class FFT(private val size: Int) {
    // Precompute cosine and sine values
    private val cosTable = DoubleArray(size / 2)
    private val sinTable = DoubleArray(size / 2)

    init {
        for (i in 0 until size / 2) {
            // Compute cosine and sine values for each index
            cosTable[i] = Math.cos(2.0 * Math.PI * i / size)
            sinTable[i] = Math.sin(2.0 * Math.PI * i / size)
        }
    }

    /**
     * Computes the forward Fourier transform of the input data.
     *
     * @param input The input data to transform.
     * @return The output of the Fourier transform.
     */
    fun forward(input: DoubleArray): DoubleArray {
        val output = input.copyOf()

        // Bit-reverse the input array
        var j = 0
        for (i in 0 until size) {
            if (j > i) {
                output[i] = input[j]
                output[j] = input[i]
            }
            var k = size / 2
            while (k >= 1 && j >= k) {
                j -= k
                k /= 2
            }
            j += k
        }

        // Compute the Fourier transform
        for (step in 1 until size) {
            val sign = if (step % 2 == 0) 1 else -1
            for (i in 0 until size step step * 2) {
                for (j in i until i + step) {
                    val k = j + step
                    // Compute the butterfly operation using precomputed cosine and sine values
                    val t = sign * (cosTable[(j - i) * size / step / 2] * output[k] + sinTable[(j - i) * size / step / 2] * output[k])
                    output[k] = sign * (-sinTable[(j - i) * size / step / 2] * output[k] + cosTable[(j - i) * size / step / 2] * output[k])
                    output[j] += t
                }
            }
        }

        return output
    }

    /**
     * Computes the inverse Fourier transform of the input data.
     *
     * @param input The input data to transform.
     * @return The output of the inverse Fourier transform.
     */
    fun inverse(input: DoubleArray): DoubleArray {
        val output = input.copyOf()

        // Reverse the output of the forward Fourier transform
        output.reverse()
        forward(output)
        output.reverse()

        // Normalize the output by the size of the input array
        for (i in output.indices) {
            output[i] /= size.toDouble()
        }

        return output
    }
}
