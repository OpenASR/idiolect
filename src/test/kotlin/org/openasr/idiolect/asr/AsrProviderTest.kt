package org.openasr.idiolect.asr

import org.junit.Assert.*
import org.junit.Test

class AsrProviderTest {
    @Test
    fun testStopWordsNoGrammarAndDefaults() {
        // Given
        val grammar = null

        // When
        val stopWords = AsrProvider.stopWords(grammar)

        // Then
        assertArrayEquals(AsrProvider.defaultStopWords.toTypedArray(), stopWords.toTypedArray())
        assertFalse("Stop words should not contain 'i' as it helps to make utterances more natural", stopWords.contains("i"))
    }

    @Test
    fun testStopWordsNoGrammarAndCustom() {
        // Given
        val grammar = null

        // When
        val stopWords = AsrProvider.stopWords(grammar, listOf("ah", "oh"))

        // Then
        assertFalse("Stop words should not contain 'yeah' as it was not in the custom set", stopWords.contains("yeah"))
    }

    @Test
    fun testStopWordsGrammarAndDefault() {
        // Given "yeah" could be a valid response to a question, if it is explicitly allowed in grammar
        val grammar = arrayOf("yeah", "yes", "i")

        // When
        val stopWords = AsrProvider.stopWords(grammar)

        // Then
        assertFalse("Stop words should not contain 'yeah' as it is in grammar", stopWords.contains("yeah"))
        assertFalse("Stop words should not contain 'i' as it is in grammar", stopWords.contains("i"))
    }

    @Test
    fun testRemoveStopWords() {
        // Given
        val utterance = "this is a test string"
        val stopWords = listOf("is", "a")

        // When
        val actual = AsrProvider.removeStopWords(utterance, stopWords) // call the mock function

        // Then
        assertEquals("this test string", actual)
    }
}
