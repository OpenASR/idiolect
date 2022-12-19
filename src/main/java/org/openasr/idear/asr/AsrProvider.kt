package org.openasr.idear.asr

import org.openasr.idear.nlp.NlpRequest
import org.openasr.idear.recognizer.SpeechRecognizer
import org.openasr.idear.settings.ConfigurableExtension

// TODO: delete SpeechRecogniser or refactor
interface AsrProvider : SpeechRecognizer, ConfigurableExtension {
    /**
     * Starts recognition process.
     */
    override fun startRecognition()

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    override fun stopRecognition()

    /** Blocks until we recognise something from the user. Called from [AsrControlLoop.run] */
    fun waitForSpeech(): NlpRequest?

    fun setGrammar(grammar: Array<String>) {}

    fun defaultModel() = ""

    fun setModel(model: String) {}
}
