package org.openasr.idear.recognizer

interface SpeechRecogniser {
    /**
     * Starts recognition process.
     */
    fun startRecognition()

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    fun stopRecognition()
}
