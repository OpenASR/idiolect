package org.openasr.idiolect.utils

object WordToNumberConverter {
    fun getNumber(input: String): Int {
        var accumulator = 0
        var total = 0
        val words = input.trim { it <= ' ' }
                .split("\\s+".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

        for (word in words) {
            accumulator += when (word) {
                "one" -> 1
                "two" -> 2
                "three" -> 3
                "four" -> 4
                "five" -> 5
                "six" -> 6
                "seven" -> 7
                "eight" -> 8
                "nine" -> 9
                "ten" -> 10
                "eleven" -> 11
                "twelve" -> 12
                "thirteen" -> 13
                "fourteen" -> 14
                "fifteen" -> 15
                "sixteen" -> 16
                "seventeen" -> 17
                "eighteen" -> 18
                "nineteen" -> 19
                "twenty" -> 20
                "thirty" -> 30
                "forty" -> 40
                "fifty" -> 50
                "sixty" -> 60
                "seventy" -> 70
                "eighty" -> 80
                "ninety" -> 90
                else -> 0
            }
            when (word) {
                "hundred" -> accumulator *= 100
                "thousand" -> {
                    accumulator *= 1000
                    total += accumulator
                    accumulator = 0
                }
            }
        }

        return total + accumulator
    }
}
