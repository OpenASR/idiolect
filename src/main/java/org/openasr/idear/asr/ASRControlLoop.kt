package org.openasr.idear.asr

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.VoiceRecordControllerAction
import org.openasr.idear.nlp.*
import org.openasr.idear.tts.TTSService


class ASRControlLoop(private val asrProvider: ASRProvider, private val nlpProvder: NlpProvider) : ASRSystem, Runnable {
    private var speechThread = Thread(this, "ASR Thread")

    override fun start() {
        startRecognition()
        speechThread.start()
    }

    override fun startRecognition() {
        ListeningState.activate()
        asrProvider.startRecognition()
    }

    override fun waitForUtterance(): String = asrProvider.waitForUtterance()

    override fun stopRecognition() {
        asrProvider.stopRecognition()
        ListeningState.standBy()
    }

    override fun terminate() {
        asrProvider.stopRecognition()
        ListeningState.terminate()
    }

    override fun run() {
        while (!ListeningState.isTerminated) {
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

    companion object {
        private val logger = Logger.getInstance(ASRControlLoop::class.java)

        private fun splitCamelCase(s: String): String = s.replace(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"
        ).toRegex(), " ")
    }
}
