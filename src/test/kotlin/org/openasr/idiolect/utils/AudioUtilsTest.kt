package org.openasr.idiolect.utils

import org.junit.Assert.*
import org.junit.Test

class AudioUtilsTest {
    @Test
    fun testReadLittleEndianShorts() {
        // Given
        val b = byteArrayOf(0x40, 0x14, 0x0BB.toByte(), 0x11)
        val shorts = ArrayList<Short>(2)

        // When
        AudioUtils.readLittleEndianShorts(b, b.size) {
            shorts.add(it)
        }

        // Then
        assertEquals(5184.toShort(), shorts[0])
        assertEquals(4539.toShort(), shorts[1])
    }
}
