package org.openasr.idear.asr

import org.openasr.idear.nlp.NlpProvider

/**
 * Processes audio input, recognises speech to text and executes actions
 */
interface AsrSystem {
    fun supportsAsrAndNlp(asrProvider: AsrProvider, nlpProvider: NlpProvider) = false

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

    /** Blocks until a we recognise something from the user. Called from [ASRControlLoop.run] */
    fun waitForUtterance(): String

    fun terminate()
}