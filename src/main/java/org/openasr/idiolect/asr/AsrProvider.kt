package org.openasr.idiolect.asr

import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.recognizer.SpeechRecognizer
import org.openasr.idiolect.settings.ConfigurableExtension

/**
 * Processes audio input, recognises speech to text.
 * Used by the `AsrControlLoop` which then sends the NlpRequest to the NlpProvider
 */
// TODO: delete SpeechRecogniser or refactor
interface AsrProvider : SpeechRecognizer, ConfigurableExtension {
    /**
     * Starts recognition process.
     */
    override fun startRecognition(): Boolean

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    override fun stopRecognition(): Boolean

    /** Blocks until we recognise something from the user. Called from [AsrControlLoop.run] */
    fun waitForSpeech(): NlpRequest?

    fun setGrammar(grammar: Array<String>) {}

    fun setModel(model: String) {}

    companion object {
        val defaultStopWords = listOf("yeah", "ah", "oh")
        fun stopWords(grammar: Array<String>?,
                      stopWords: List<String> = defaultStopWords): List<String> {
            return if (grammar == null) stopWords
            else stopWords.filterNot { grammar.contains(it) ?: false }
        }

        /**
         * Removes the stop words from the utterance
         * @param utterance multiple words separated by space, all lower-case
         * @param stopWords a list of words to remove
         **/
        fun removeStopWords(utterance: String, stopWords: Iterable<String>): String {
            val words = utterance.split(" ")
            val filteredWords = words.filterNot { it in stopWords }
            return filteredWords.joinToString(" ")
        }
    }
}
