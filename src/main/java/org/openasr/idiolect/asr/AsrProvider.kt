package org.openasr.idiolect.asr

import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.recognizer.SpeechRecognizer
import org.openasr.idiolect.settings.ConfigurableExtension

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

    fun setModel(model: String) {}
}
