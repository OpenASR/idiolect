package org.openasr.idiolect.asr

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.DefaultLogger
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.messages.MessageBus
import org.openasr.idiolect.asr.ListeningState.Status.STARTED
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResultListener.Companion.NLP_RESULT_TOPIC
import org.openasr.idiolect.settings.IdiolectConfig
import org.openasr.idiolect.settings.PrintlnLogger
import org.openasr.idiolect.tts.TtsService
import javax.sound.sampled.LineUnavailableException

/**
 * Coordinates the selection, creation and orchestration of the system components:
 * - AsrSystem
 * - AsrProvider
 * - NlpProvider
 *
 * Entry points:
 *  - activate() - Called when the user presses the start button
 */
@Service
class AsrService {
    private val log = logger<AsrService>()
    private val messageBus: MessageBus by lazy { ApplicationManager.getApplication().messageBus }
    private lateinit var asrSystem: AsrSystem
    @Volatile private var isListening = false

    init {
        PrintlnLogger.installForLocalDev()
//        System.setProperty("jna.nounpack", "false")
//        System.setProperty("jna.noclasspath", "false")

//        initialiseAsrSystem()
    }

    fun setAsrSystem(asrSystem: AsrSystem) {
        val status = ListeningState.getStatus()
        val terminated =
            if (this::asrSystem.isInitialized && this.asrSystem != asrSystem) {
                terminate()
                true
            } else false

        // If last asrSystem was previously active but terminated then swap restart
        this.asrSystem = asrSystem.apply {
            if (terminated && status == STARTED) start()
        }
    }

    fun onNlpRequest(nlpRequest: NlpRequest) {
        asrSystem.onNlpRequest(nlpRequest)
    }

    fun promptForUtterance(prompt: String): String {
        TtsService.say(prompt)
        return waitForUtterance()
    }

    fun promptForUtterance(prompt: String, grammar: Array<String>): String {
        TtsService.say(prompt)
        return waitForUtterance(grammar)
    }

    fun sayWithMicOff(prompt: String) {
        val wasListening = setListeningState(false)
        TtsService.say(prompt)

        if (wasListening) {
            setListeningState(true)
        }
    }

    /**
     * Called from IntentHandlers via the likes of promptForUtterance()
     *
     * TODO: This probably needs to be refactored so that there's only one clear call to `asrSystem.waitForUtterance()`
     */
    fun waitForUtterance() = asrSystem.waitForUtterance()

    /**
     * This will block until the user says something in grammar, or one of the escape words
     * or an escape word: "dont worry", "never mind", "quit", "forget it", "escape"
     *
     * @param grammar - phrases that we're expecting the user to say
     */
    fun waitForUtterance(grammar: Array<String>) = asrSystem.waitForUtterance(grammar)

    fun waitForUtterance(grammar: Array<String>, escapeWords: Array<String>) = asrSystem.waitForUtterance(grammar, escapeWords)

    fun setGrammar(grammar: Array<String>) = asrSystem.setGrammar(grammar)

    fun toggleListening() {
        setListeningState(!isListening)
    }

    fun setListeningState(listening: Boolean): Boolean {
        val wasListening = isListening
        if (isListening != listening) {
//      val settings = ApplicationManager.getApplication().getService(AceConfig::class.java).state
//      val aceJumpDefaults = settings.allowedChars
            if (listening) {
                activate()
                isListening = true
//            settings.allowedChars = "1234567890"
            } else {
                isListening = false
//            settings.allowedChars = aceJumpDefaults
                deactivate()
            }

            messageBus.syncPublisher(NLP_RESULT_TOPIC).onListening(isListening)
        }

        return wasListening
    }

    /** Called when the user presses the start button. */
    fun activate() {
        if (!this::asrSystem.isInitialized) {
            initialiseAsrSystem()
        }
        asrSystem.startRecognition()
        ListeningState.started()
    }

    /** Called when the user presses the stop button. */
    fun deactivate() {
        ListeningState.stopped()
        asrSystem.stopRecognition()
    }

    private fun terminate() {
        asrSystem.terminate()
        ListeningState.terminated()
    }

    fun dispose() {
        // Deactivate in the first place, therefore actually
        // prevent activation upon the user-input
        deactivate()
        terminate()
    }

    /**
     * Initialises the pre-configured ASR and NLP Providers.
     * - Load speech model or authenticate & connect to remote service
     * - Open microphone
     */
    private fun initialiseAsrSystem() {
        try {
            val asrSystem = IdiolectConfig.initialiseAsrSystem()
            asrSystem.start()
            this.asrSystem = asrSystem
        } catch (e: LineUnavailableException) {
            log.error("Couldn't initialize microphone", e)
            messageBus.syncPublisher(NLP_RESULT_TOPIC).onFailure("Could not open microphone")
            throw e
        } catch (e: ModelNotAvailableException) {
            log.info(e.message)
            throw e
        } catch (e: Exception) {
            log.error("Couldn't initialize speech asrProvider", e)
            messageBus.syncPublisher(NLP_RESULT_TOPIC).onFailure("Failed to initialise speech service")
            throw e
        }
    }
}

// This is for testing purposes solely
//fun main() {
//    AsrService.activate()
//}
