package org.openasr.idiolect.recognizer

interface SpeechRecognizer {
    /**
     * Starts recognition process.
     */
    fun startRecognition(): Boolean

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    fun stopRecognition(): Boolean
}
