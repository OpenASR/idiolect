package org.openasr.idiolect.utils

import org.junit.Assert.*
import org.junit.Test

class StringUtilsTest {
    @Test
    fun testToCamelCase() {
        assertEquals("camel", "camel".toCamelCase())
        assertEquals("camelCase", "camel case".toCamelCase())
        assertEquals("toCamelCase", "to camel case".toCamelCase())
    }

    @Test
    fun testToUpperCamelCase() {
        assertEquals("Camel", "camel".toUpperCamelCase())
        assertEquals("ToUpperCamelCase", "to upper camel case".toUpperCamelCase())
    }

    @Test
    fun testExpandCamelCase() {
        assertEquals("camel", "Camel".expandCamelCase())
        assertEquals("expand camel case", "ExpandCamelCase".expandCamelCase())
        assertEquals("expand camel case", "expandCamelCase".expandCamelCase())
    }

    @Test
    fun testSplitCamelCase() {
        assertArrayEquals(arrayOf("camel"), "camel".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("camel"), "Camel".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("split", "camel"), "splitCamel".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("split", "camel"), "SplitCamel".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("split", "camel", "case"), "splitCamelCase".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("split", "camel", "case"), "SplitCamelCase".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("camel", "c"), "CamelC".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("camel", "1"), "Camel1".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("go", "to", "tab", "9"), "GoToTab9".splitCamelCase().toList().toTypedArray())
        assertArrayEquals(arrayOf("disable", "pce"), "disablePCE".splitCamelCase().toList().toTypedArray())
    }

    @Test
    fun testSpeechFriendlyFileName() {
        assertEquals("string utils test", speechFriendlyFileName("StringUtilsTest.kt"))
        assertEquals(".editorconfig", speechFriendlyFileName(".editorconfig"))
        assertEquals(".gitignore", speechFriendlyFileName(".gitignore"))
        assertEquals(".env", speechFriendlyFileName(".env"))
    }
}
