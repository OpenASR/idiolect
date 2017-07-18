package org.openasr.idear.ide

import java.awt.Robot
import java.awt.event.KeyEvent.*

object Keyboard {
    private var robot = Robot()

    fun type(characters: CharSequence) = characters.forEach { type(it) }

    fun type(character: Char) {
        when (character) {
            'a' -> doType(VK_A)
            'b' -> doType(VK_B)
            'c' -> doType(VK_C)
            'd' -> doType(VK_D)
            'e' -> doType(VK_E)
            'f' -> doType(VK_F)
            'g' -> doType(VK_G)
            'h' -> doType(VK_H)
            'i' -> doType(VK_I)
            'j' -> doType(VK_J)
            'k' -> doType(VK_K)
            'l' -> doType(VK_L)
            'm' -> doType(VK_M)
            'n' -> doType(VK_N)
            'o' -> doType(VK_O)
            'p' -> doType(VK_P)
            'q' -> doType(VK_Q)
            'r' -> doType(VK_R)
            's' -> doType(VK_S)
            't' -> doType(VK_T)
            'u' -> doType(VK_U)
            'v' -> doType(VK_V)
            'w' -> doType(VK_W)
            'x' -> doType(VK_X)
            'y' -> doType(VK_Y)
            'z' -> doType(VK_Z)
            'A' -> doType(VK_SHIFT, VK_A)
            'B' -> doType(VK_SHIFT, VK_B)
            'C' -> doType(VK_SHIFT, VK_C)
            'D' -> doType(VK_SHIFT, VK_D)
            'E' -> doType(VK_SHIFT, VK_E)
            'F' -> doType(VK_SHIFT, VK_F)
            'G' -> doType(VK_SHIFT, VK_G)
            'H' -> doType(VK_SHIFT, VK_H)
            'I' -> doType(VK_SHIFT, VK_I)
            'J' -> doType(VK_SHIFT, VK_J)
            'K' -> doType(VK_SHIFT, VK_K)
            'L' -> doType(VK_SHIFT, VK_L)
            'M' -> doType(VK_SHIFT, VK_M)
            'N' -> doType(VK_SHIFT, VK_N)
            'O' -> doType(VK_SHIFT, VK_O)
            'P' -> doType(VK_SHIFT, VK_P)
            'Q' -> doType(VK_SHIFT, VK_Q)
            'R' -> doType(VK_SHIFT, VK_R)
            'S' -> doType(VK_SHIFT, VK_S)
            'T' -> doType(VK_SHIFT, VK_T)
            'U' -> doType(VK_SHIFT, VK_U)
            'V' -> doType(VK_SHIFT, VK_V)
            'W' -> doType(VK_SHIFT, VK_W)
            'X' -> doType(VK_SHIFT, VK_X)
            'Y' -> doType(VK_SHIFT, VK_Y)
            'Z' -> doType(VK_SHIFT, VK_Z)
            '`' -> doType(VK_BACK_QUOTE)
            '0' -> doType(VK_0)
            '1' -> doType(VK_1)
            '2' -> doType(VK_2)
            '3' -> doType(VK_3)
            '4' -> doType(VK_4)
            '5' -> doType(VK_5)
            '6' -> doType(VK_6)
            '7' -> doType(VK_7)
            '8' -> doType(VK_8)
            '9' -> doType(VK_9)
            '-' -> doType(VK_MINUS)
            '=' -> doType(VK_EQUALS)
            '~' -> doType(VK_SHIFT, VK_BACK_QUOTE)
            '!' -> doType(VK_EXCLAMATION_MARK)
            '@' -> doType(VK_AT)
            '#' -> doType(VK_NUMBER_SIGN)
            '$' -> doType(VK_DOLLAR)
            '%' -> doType(VK_SHIFT, VK_5)
            '^' -> doType(VK_CIRCUMFLEX)
            '&' -> doType(VK_AMPERSAND)
            '*' -> doType(VK_ASTERISK)
            '(' -> doType(VK_LEFT_PARENTHESIS)
            ')' -> doType(VK_RIGHT_PARENTHESIS)
            '_' -> doType(VK_UNDERSCORE)
            '+' -> doType(VK_PLUS)
            '\t' -> doType(VK_TAB)
            '\n' -> doType(VK_ENTER)
            '[' -> doType(VK_OPEN_BRACKET)
            ']' -> doType(VK_CLOSE_BRACKET)
            '\\' -> doType(VK_BACK_SLASH)
            '{' -> doType(VK_SHIFT, VK_OPEN_BRACKET)
            '}' -> doType(VK_SHIFT, VK_CLOSE_BRACKET)
            '|' -> doType(VK_SHIFT, VK_BACK_SLASH)
            ';' -> doType(VK_SEMICOLON)
            ':' -> doType(VK_COLON)
            '\'' -> doType(VK_QUOTE)
            '"' -> doType(VK_QUOTEDBL)
            ',' -> doType(VK_COMMA)
            '<' -> doType(VK_SHIFT, VK_COMMA)
            '.' -> doType(VK_PERIOD)
            '>' -> doType(VK_SHIFT, VK_PERIOD)
            '/' -> doType(VK_SLASH)
            '?' -> doType(VK_SHIFT, VK_SLASH)
            ' ' -> doType(VK_SPACE)
            else -> throw IllegalArgumentException("Cannot type character " + character)
        }
    }

    fun type(vararg keys: Int) {
        keys.forEach { robot.keyPress(it) }
        keys.forEach { robot.keyRelease(it) }
    }

    fun type(vararg keys: Char) {
        keys.forEach { robot.keyPress(it.toInt()) }
        keys.forEach { robot.keyRelease(it.toInt()) }
    }

    private fun doType(vararg keyCodes: Int) = doType(keyCodes, 0, keyCodes.size)

    private fun doType(keyCodes: IntArray, offset: Int, length: Int) {
        if (length == 0) return

        robot.keyPress(keyCodes[offset])
        doType(keyCodes, offset + 1, length - 1)
        robot.keyRelease(keyCodes[offset])
    }

    fun pressShift() = robot.keyPress(VK_SHIFT)

    fun releaseShift() = robot.keyRelease(VK_SHIFT)
}

fun main(args: Array<String>) = Keyboard.type("Hello there, how are you?")