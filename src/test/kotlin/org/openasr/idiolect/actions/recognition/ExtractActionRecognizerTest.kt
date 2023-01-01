package org.openasr.idiolect.actions.recognition

import org.junit.Assert.*
import org.junit.Test
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.testing.TestContext

class ExtractActionRecognizerTest {
    private val context = TestContext()

    @Test
    fun testResolveIntroduceVariable() {
        val recognizer = ExtractActionRecognizer()

        // When
        val result = recognizer.tryResolveIntent(NlpRequest(listOf("extract variable sum")), context)

        // Then
        assertEquals("IntroduceVariable", result!!.actionId)
        assertEquals("sum", result.typeAfter)
        assertTrue(result.hitTabAfter)
        assertFalse(result.fulfilled)
    }

    @Test
    fun testResolveIntroduceFieldWithoutName() {
        val recognizer = ExtractActionRecognizer()

        // When
        val result = recognizer.tryResolveIntent(NlpRequest(listOf("extract to field")), context)

        // Then
        assertEquals("IntroduceField", result!!.actionId)
        assertNull(result.typeAfter)
        assertFalse(result.hitTabAfter)
        assertFalse(result.fulfilled)
    }

    @Test
    fun testResolveMultiWordName() {
        val recognizer = ExtractActionRecognizer()

        // When
        val result = recognizer.tryResolveIntent(NlpRequest(listOf("extract to field my cool thing")), context)

        // Then
        assertEquals("myCoolThing", result!!.typeAfter)
    }
}
