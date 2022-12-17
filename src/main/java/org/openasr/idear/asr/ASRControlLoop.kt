package org.openasr.idear.asr

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.VoiceRecordControllerAction
import org.openasr.idear.nlp.*
import org.openasr.idear.tts.TTSService


class ASRControlLoop : AsrSystem, Runnable {
    companion object {
        private val logger = Logger.getInstance(ASRControlLoop::class.java)
    }

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
                logger.info("Recognized: $result")

                nlpProvder.processUtterance(result)
            }
        }
    }
}
