package org.openasr.idear.utils

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
    }
}
