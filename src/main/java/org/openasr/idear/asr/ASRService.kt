package org.openasr.idear.asr

import org.openasr.idear.asr.cmusphinx.CMUSphinxASR
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger

class ASRService {
    private lateinit var speechThread: Thread
    private lateinit var recognizer: ASRProvider

    fun init() {
        try {
            recognizer = CMUSphinxASR()
//            recognizer = LexASR()
            // TODO: LexVoiceASR(nlpResultListener) : ASRProvider, NlpProvider
            // TODO: recogniser.withNlpService( LexTextNlp(nlpResultListener): NlpProvider )

            speechThread = Thread(ASRControlLoop(recognizer), "ASR Thread")
            recognizer.startRecognition()
            // Fire up control-loop
            speechThread.start()
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Couldn't initialize speech recognizer:", e)
        }
    }

    fun activate(): Boolean {
        return ListeningState.activate()
    }

    fun deactivate(): Boolean {
        return ListeningState.standBy()
    }

    fun terminate() = recognizer.stopRecognition()

    fun dispose() {
        // Deactivate in the first place, therefore actually
        // prevent activation upon the user-input
        deactivate()
        terminate()
    }

    companion object {
        private val logger = Logger.getLogger(ASRService::class.java.simpleName)
    }
}

// This is for testing purposes solely
fun main(args: Array<String>) {
    val asr = ASRService()
    asr.init()
    asr.activate()
}
