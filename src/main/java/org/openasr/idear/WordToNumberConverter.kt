package com.jetbrains.idear

/**
 * Created by breandan on 10/25/2015.
 */
object WordToNumberConverter {
    fun getNumber(input: String): Int {
        var accumulator = 0
        var total = 0
        val words = input.trim { it <= ' ' }.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (word in words) {
            when (word) {
                "zero" -> accumulator += 0
                "one" -> accumulator += 1
                "two" -> accumulator += 2
                "three" -> accumulator += 3
                "four" -> accumulator += 4
                "five" -> accumulator += 5
                "six" -> accumulator += 6
                "seven" -> accumulator += 7
                "eight" -> accumulator += 8
                "nine" -> accumulator += 9
                "ten" -> accumulator += 10
                "eleven" -> accumulator += 11
                "twelve" -> accumulator += 12
                "thirteen" -> accumulator += 13
                "fourteen" -> accumulator += 14
                "fifteen" -> accumulator += 15
                "sixteen" -> accumulator += 16
                "seventeen" -> accumulator += 17
                "eighteen" -> accumulator += 18
                "nineteen" -> accumulator += 19
                "twenty" -> accumulator += 20
                "thirty" -> accumulator += 30
                "forty" -> accumulator += 40
                "fifty" -> accumulator += 50
                "sixty" -> accumulator += 60
                "seventy" -> accumulator += 70
                "eighty" -> accumulator += 80
                "ninety" -> accumulator += 90
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