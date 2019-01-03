package com.darkprograms.speech.util


/*************************************************************************
 * Compilation:  javac FFT.java
 * Execution:    java FFT N
 * Dependencies: Complex.java
 *
 * Compute the FFT and inverse FFT of a length N complex sequence.
 * Bare bones implementation that runs in O(N log N) time. Our goal
 * is to optimize the clarity of the code, rather than performance.
 *
 * Limitations
 * -----------
 * -  assumes N is a power of 2
 *
 * -  not the most memory efficient algorithm (because it uses
 * an object type for representing complex numbers and because
 * it re-allocates memory for the subarray, instead of doing
 * in-place or reusing a single temporary array)
 *
 */

/*************************************************************************
 * @author Skylion implementation
 * @author Princeton University for the actual algorithm.
 */

object FFT {

    // compute the FFT of x[], assuming its length is a power of 2
    fun fft(x: Array<Complex>): Array<Complex> {
        val N = x.size

        // base case
        if (N == 1) return arrayOf(x[0])

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) {
            throw RuntimeException("N is not a power of 2")
        }

        // fft of even terms
        val even = arrayOfNulls<Complex>(N / 2)
        for (k in 0 until N / 2) {
            even[k] = x[2 * k]
        }
        val q = fft(even.filterNotNull().toTypedArray())

        // fft of odd terms
        val odd = even  // reuse the array
        for (k in 0 until N / 2) {
            odd[k] = x[2 * k + 1]
        }
        val r = fft(odd.filterNotNull().toTypedArray())

        // combine
        val y = arrayOfNulls<Complex>(N)
        for (k in 0 until N / 2) {
            val kth = -2.0 * k.toDouble() * Math.PI / N
            val wk = Complex(Math.cos(kth), Math.sin(kth))
            y[k] = q[k].plus(wk.times(r[k]))
            y[k + N / 2] = q[k].minus(wk.times(r[k]))
        }
        return y.filterNotNull().toTypedArray()
    }


    // compute the inverse FFT of x[], assuming its length is a power of 2
    fun ifft(x: Array<Complex>): Array<Complex> {
        val N = x.size
        var y = arrayOfNulls<Complex>(N)

        // take conjugate
        for (i in 0 until N) {
            y[i] = x[i].conjugate()
        }

        // compute forward FFT
        val z = fft(y.filterNotNull().toTypedArray())

        // take conjugate again
        for (i in 0 until N) {
            z[i] = z[i].conjugate()
        }

        // divide by N
        for (i in 0 until N) {
            z[i] = z[i].times(1.0 / N)
        }

        return z
    }

    // compute the circular convolution of x and y
    fun cconvolve(x: Array<Complex>, y: Array<Complex>): Array<Complex> {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.size != y.size) {
            throw RuntimeException("Dimensions don't agree")
        }

        val N = x.size

        // compute FFT of each sequence
        val a = fft(x)
        val b = fft(y)

        // point-wise multiply
        val c = arrayOfNulls<Complex>(N)
        for (i in 0 until N) {
            c[i] = a[i].times(b[i])
        }

        // compute inverse FFT
        return ifft(c.filterNotNull().toTypedArray())
    }


    // compute the linear convolution of x and y
    fun convolve(x: Array<Complex>, y: Array<Complex>): Array<Complex> {
        val ZERO = Complex(0.0, 0.0)

        val a = arrayOfNulls<Complex>(2 * x.size)
        System.arraycopy(x, 0, a, 0, x.size)
        for (i in x.size until 2 * x.size) a[i] = ZERO

        val b = arrayOfNulls<Complex>(2 * y.size)
        System.arraycopy(y, 0, b, 0, y.size)
        for (i in y.size until 2 * y.size) b[i] = ZERO

        return cconvolve(a.filterNotNull().toTypedArray(), b.filterNotNull().toTypedArray())
    }
}