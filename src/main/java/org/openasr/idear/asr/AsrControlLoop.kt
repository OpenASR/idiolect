package org.openasr.idear.asr

import com.intellij.openapi.diagnostic.logger
import org.openasr.idear.actions.VoiceRecordControllerAction
import org.openasr.idear.nlp.*
import org.openasr.idear.tts.TTSService


class AsrControlLoop : AsrSystem, Runnable {
    val log = logger<AsrControlLoop>()

    private lateinit var asrProvider: AsrProvider
    private lateinit var nlpProvder: NlpProvider

    private var speechThread = Thread(this, "ASR Thread")

    override fun supportsAsrAndNlp(asrProvider: AsrProvider, nlpProvider: NlpProvider) = true

    override fun initialise(asrProvider: AsrProvider, nlpProvider: NlpProvider) {
        this.asrProvider = asrProvider
        this.nlpProvder = nlpProvider
    }

    override fun start() {
        startRecognition()
        speechThread.start()
    }

    override fun startRecognition() {
        asrProvider.startRecognition()
    }

    override fun waitForUtterance() = asrProvider.waitForUtterance()

    override fun setGrammar(grammar: Array<String>) {
        asrProvider.setGrammar(grammar)
    }

    override fun stopRecognition() {
        asrProvider.stopRecognition()
    }

    override fun terminate() {
        asrProvider.stopRecognition()
    }

    override fun run() {
        while (!ListeningState.isTerminated) {
            ListeningState.waitIfStandby()
            // This blocks on a recognition result
            val result = asrProvider.waitForUtterance()

            if (ListeningState.isInit) {
                if (result == Commands.HI_IDEA) {
                    // Greet invoker
                    TTSService.say("Hi")
                    VoiceRecordControllerAction.invoke()
                }
            } else if (ListeningState.isActive) {
                log.info("Recognized: $result")
                println("Recognized: $result")

                nlpProvder.processUtterance(result)
            }
        }
    }
}
