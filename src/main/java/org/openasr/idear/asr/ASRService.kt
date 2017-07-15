package org.openasr.idear.asr

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.asr.cmusphinx.CMUSphinxASR
import org.openasr.idear.recognizer.awslex.LexRecognizer
import java.io.IOException

object ASRService {
    private lateinit var speechThread: Thread
    private lateinit var recognizer: ASRProvider

    fun init() {
        try {
            val lex = LexRecognizer()
            lex.startRecognition()
            // TODO: LexVoiceASR(nlpResultListener) : ASRProvider, NlpProvider
            // TODO: recogniser.withNlpService( LexTextNlp(nlpResultListener): NlpProvider )


            recognizer = CMUSphinxASR()
            speechThread = Thread(ASRControlLoop(recognizer), "ASR Thread")
            recognizer.startRecognition()
            // Fire up control-loop
            speechThread.start()
        } catch (e: IOException) {
            logger.error( "Couldn't initialize speech recognizer!", e)
        }
    }

    fun activate(): Boolean = ListeningState.activate()

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

    private val logger = Logger.getInstance(ASRService::class.java)
}

// This is for testing purposes solely
fun main(args: Array<String>) {
    val asr = ASRService
    asr.activate()
}
