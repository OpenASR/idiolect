package org.openasr.idear.asr

interface ASRProvider {
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
}