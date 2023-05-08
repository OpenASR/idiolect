package org.openasr.idiolect.actions.recognition

import org.junit.Assert.*
import org.junit.Test
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.testing.TestContext

class ExtractActionRecognizerTest {
    private val context = NlpContext(TestContext())

    @Test
    fun testResolveIntroduceVariable() {
        val recognizer = ExtractFieldOrVariable()

        // When
        val result = recognizer.tryResolveIntent(NlpRequest(listOf("extract variable sum")), context)

        // Then
        assertEquals("IntroduceVariable", result!!.slots!!["actionId"])
        assertEquals("sum", result.slots!!["name"])
    }

    @Test
    fun testResolveIntroduceFieldWithoutName() {
        val recognizer = ExtractFieldOrVariable()

        // When
        val result = recognizer.tryResolveIntent(NlpRequest(listOf("extract to field")), context)

        // Then
        assertEquals("IntroduceField", result!!.slots!!["actionId"])
    }

    @Test
    fun testResolveMultiWordName() {
        val recognizer = ExtractFieldOrVariable()

        // When
        val result = recognizer.tryResolveIntent(NlpRequest(listOf("extract to field my cool thing")), context)

        // Then
        assertEquals("my cool thing", result!!.slots!!["name"])
    }
}
