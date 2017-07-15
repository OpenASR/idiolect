package org.openasr.idear.asr

import org.openasr.idear.recognizer.SpeechRecognizer

// TODO: delete SpeechRecogniser or refactor
interface ASRProvider : SpeechRecognizer {
    /**
     * Starts recognition process.
     */
    override fun startRecognition()

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    override fun stopRecognition()

    /** Blocks until a we recognise something from the user. Called from [ASRControlLoop.run] */
    fun waitForUtterance(): String
}