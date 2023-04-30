package org.openasr.idiolect.recognizer.filter

class Complex(var real: Double, var imaginary: Double) {
    constructor(real: Int, imaginary: Int) : this(real.toDouble(), imaginary.toDouble())

    override fun toString(): String {
        return "($real + $imaginary * i)"
    }

    fun add(other: Complex): Complex {
        return Complex(real + other.real, imaginary + other.imaginary)
    }

    fun subtract(other: Complex): Complex {
        return Complex(real - other.real, imaginary - other.imaginary)
    }

    fun multiply(other: Complex): Complex {
        return Complex(real * other.real - imaginary * other.imaginary, real * other.imaginary + imaginary * other.real)
    }

    fun divide(other: Complex): Complex {
        val denominator = other.real * other.real + other.imaginary * other.imaginary
        return Complex((real * other.real + imaginary * other.imaginary) / denominator, (imaginary * other.real - real * other.imaginary) / denominator)
    }

    fun abs(): Double {
        return Math.sqrt(real * real + imaginary * imaginary)
    }

    fun phase(): Double {
        return Math.atan2(imaginary, real)
    }
}
