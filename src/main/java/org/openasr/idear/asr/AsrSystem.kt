package org.openasr.idear.asr

import org.openasr.idear.nlp.NlpProvider
import org.openasr.idear.nlp.NlpRequest

/**
 * Processes audio input, recognises speech to text and executes actions
 */
interface AsrSystem {
    fun supportsAsrAndNlp(asrProvider: AsrProvider, nlpProvider: NlpProvider) = false

    fun initialise(asrProvider: AsrProvider, nlpProvider: NlpProvider) {}

    fun start()

    /**
     * Starts recognition process.
     */
    fun startRecognition()

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    fun stopRecognition()

    /** Blocks until we recognise something from the user. Called from [AsrControlLoop.run] */
    fun waitForUtterance(): String

    fun waitForUtterance(grammar: Array<String>,
                         escapeWords: Array<String> = arrayOf("dont worry", "quit", "forget it", "escape")): String

    fun setGrammar(grammar: Array<String>) {}

    fun terminate()
}
