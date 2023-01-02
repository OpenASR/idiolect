package org.openasr.idiolect.asr

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.asr.ListeningState.Status.ACTIVE
import org.openasr.idiolect.nlp.NlpResultListener.Companion.NLP_RESULT_TOPIC
import org.openasr.idiolect.settings.IdiolectConfig
import javax.sound.sampled.LineUnavailableException

object AsrService {
    private val log = logger<AsrService>()
    private val messageBus = ApplicationManager.getApplication().messageBus
    private lateinit var asrSystem: AsrSystem
    @Volatile private var isListening = false

    init {
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
        this.asrSystem = asrSystem.apply { if (terminated && status == ACTIVE) start() }
    }

    fun waitForUtterance() = asrSystem.waitForUtterance()

    fun waitForUtterance(grammar: Array<String>) = asrSystem.waitForUtterance(grammar)

    fun setGrammar(grammar: Array<String>) = asrSystem.setGrammar(grammar)

    fun toggleListening() {
//      val settings = ApplicationManager.getApplication().getService(AceConfig::class.java).state
//      val aceJumpDefaults = settings.allowedChars
        if (isListening) {
            isListening = false
//            settings.allowedChars = aceJumpDefaults
            deactivate()
        } else {
            activate()
            isListening = true
//            settings.allowedChars = "1234567890"
        }

        messageBus.syncPublisher(NLP_RESULT_TOPIC).onListening(isListening)
    }

    /** Called when the user presses the start button. */
    fun activate() {
        ListeningState.activate()
        if (!this::asrSystem.isInitialized) initialiseAsrSystem()
        asrSystem.startRecognition()
    }

    /** Called when the user presses the stop button. */
    fun deactivate() {
        ListeningState.standBy()
        asrSystem.stopRecognition()
    }

    private fun terminate() {
        ListeningState.terminate()
        asrSystem.terminate()
    }

    fun dispose() {
        // Deactivate in the first place, therefore actually
        // prevent activation upon the user-input
        deactivate()
        terminate()
    }

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
fun main() {
    AsrService.activate()
}