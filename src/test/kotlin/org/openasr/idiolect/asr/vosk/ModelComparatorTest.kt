package org.openasr.idiolect.asr.vosk

import org.junit.Assert.*
import org.junit.Test

class ModelComparatorTest {
    @Test
    fun testModelComparator() {
        val comparator = ModelComparator()
        val models = arrayOf(
            createModel(30000, "big"),
            createModel(2000, "small"),
            createModel(20000, "big"),
            createModel(1000, "small"),
            createModel(1500, "small"),
            createModel(40000, "big"),
        )

        // When
        val sorted = models.sortedWith(comparator)

        // Then
        assertArrayEquals(arrayOf(
            // larger size is better
            2000,
            1500,
            1000,
            // but "big" does not support dynamic grammars
            40000,
            30000,
            20000,
        ), sorted.map { it.size }.toTypedArray())
    }

    private fun createModel(size: Int, type: String): ModelInfo =
        ModelInfo("", "", "$size", "", size, "$size", type)
}
