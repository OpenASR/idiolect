package org.openasr.idear.asr

interface ASRProvider {
    fun startRecognition()
    fun stopRecognition()

    /** Blocks until a we recognise something from the user. Called from [ASRControlLoop.run] */
    fun waitForUtterance(): String
}