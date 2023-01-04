package org.openasr.idiolect.asr

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.nlp.*


class AsrControlLoop : AsrSystem, Runnable {
    val log = logger<AsrControlLoop>()

    private lateinit var asrProvider: AsrProvider
    private lateinit var nlpProvder: NlpProvider
    private var speechThread = Thread(this, "ASR Thread")
    private val messageBus = ApplicationManager.getApplication().messageBus

    override fun supportsAsrAndNlp(asrProvider: AsrProvider, nlpProvider: NlpProvider) = true

    override fun initialise(asrProvider: AsrProvider, nlpProvider: NlpProvider) {
        this.asrProvider = asrProvider
        this.nlpProvder = nlpProvider
    }

    override fun start() =
        startRecognition().also { if (!speechThread.isAlive) speechThread.start() }

    override fun waitForUtterance(): String = asrProvider.waitForSpeech()?.utterance ?: ""

    override fun waitForUtterance(grammar: Array<String>,
                                  escapeWords: Array<String>): String {
        val effectiveGrammar = grammar.plus(escapeWords)
        asrProvider.setGrammar(effectiveGrammar)
        var response = ""

        while (response.isEmpty()) {
            val speech = asrProvider.waitForSpeech() ?: break

            for (alternative in speech.alternatives) {
                for (expected in grammar) {
                    if (alternative.contains(expected)) {
                        response = expected
                        break
                    }
                }
                if (alternative in escapeWords) break
            }
        }

        GrammarService.useDictationGrammar()
        return response
    }

    override fun setGrammar(grammar: Array<String>) = asrProvider.setGrammar(grammar)

    override fun startRecognition() = asrProvider.startRecognition()

    override fun stopRecognition() = asrProvider.stopRecognition()

    override fun terminate() = asrProvider.stopRecognition()

    override fun run() {
        while (!ListeningState.isTerminated) {
            try {
                ListeningState.waitIfStandby()
                // This blocks on a recognition result
                val result = asrProvider.waitForSpeech()

                if (result != null) {
//                if (ListeningState.isInit) {
//                    if (result == Commands.HI_IDEA) {
//                        // Greet invoker
//                        TTSService.say("Hi")
//                        VoiceRecordControllerAction.invoke()
//                    }
//                } else if (ListeningState.isActive) {
                    messageBus.syncPublisher(NlpResultListener.NLP_RESULT_TOPIC).onRecognition(result)

                    nlpProvder.processNlpRequest(result)
//                }
                }
            } catch (iex: InterruptedException) {
                break
            } catch (ex: Exception) {
                log.warn("Failed to process utterance: ${ex.message}")
//                break
            }
        }
    }
}
