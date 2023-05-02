package org.openasr.idiolect.asr

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.nlp.*


class AsrControlLoop : AsrSystem, Runnable {
    val log = logger<AsrControlLoop>()

    private lateinit var asrProvider: AsrProvider
    private lateinit var nlpProvider: NlpProvider
    private var speechThread = Thread(this, "Idiolect ASR Control Loop")
    private val messageBus = ApplicationManager.getApplication().messageBus

    override fun supportsAsrAndNlp(asrProvider: AsrProvider, nlpProvider: NlpProvider) = true

    override fun initialise(asrProvider: AsrProvider, nlpProvider: NlpProvider) {
        this.asrProvider = asrProvider
        this.nlpProvider = nlpProvider
    }

    override fun start() {
//        startRecognition()
        if (!speechThread.isAlive) {
            speechThread.start()
        }
    }

    override fun setGrammar(grammar: Array<String>) = asrProvider.setGrammar(grammar)

    override fun startRecognition() = asrProvider.startRecognition()

    override fun stopRecognition() = asrProvider.stopRecognition()

    override fun terminate() = asrProvider.stopRecognition()

    /**
     * Called from IntentHandlers via the likes of promptForUtterance()
     *
     * TODO: This probably needs to be refactored so that there's only one clear call to `asrSystem.waitForUtterance()`
     */
    override fun waitForUtterance(): String = asrProvider.waitForSpeech()?.utterance ?: ""

    /**
     * This will block until the user says something in grammar, or one of the escape words.
     *
     * @param grammar - phrases that we're expecting the user to say
     * @param escapeWords - defaults to "don't worry", "never mind", "quit", "forget it", "escape"
     */
    override fun waitForUtterance(grammar: Array<String>,
                                  escapeWords: Array<String>): String {
        val effectiveGrammar = grammar.plus(escapeWords)
        asrProvider.setGrammar(effectiveGrammar)
        var response = ""

        while (response.isEmpty()) {
            val speech = asrProvider.waitForSpeech() ?: break
//            log.debug("waitForUtterance - ASR has speech")

            for (alternative in speech.alternatives) {
                for (expected in grammar) {
                    if (alternative.contains(expected)) {
                        response = expected
//                        log.debug("...and it's what we were waiting for")
                        break
                    }
                }
                if (alternative in escapeWords) {
//                    log.debug("...escape word")
                    break
                }
            }
        }

        GrammarService.useDictationGrammar()
        return response
    }

    /** Called from the ASR Thread to capture asynchronous user requests */
    override fun run() {
        while (!ListeningState.isTerminated) {
            try {
                ListeningState.waitForStarted()
                // This blocks on a recognition result
                val nlpRequest = asrProvider.waitForSpeech()

                if (nlpRequest != null && nlpRequest.alternatives.isNotEmpty()) {
                    onNlpRequest(nlpRequest)
                }
            } catch (iex: InterruptedException) {
                break
            } catch (ex: Exception) {
                log.warn("Failed to process utterance: ${ex.stackTraceToString()}")
//                break
            }
        }
    }

    override fun onNlpRequest(nlpRequest: NlpRequest) {
        // Display the request as recognised
//        repairUtterance(nlpRequest)
        messageBus.syncPublisher(NlpResultListener.NLP_RESULT_TOPIC).onRecognition(nlpRequest)

        nlpProvider.processNlpRequest(nlpRequest)
    }
}
