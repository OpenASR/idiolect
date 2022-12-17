package org.openasr.idear.asr

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.settings.IdearConfiguration
import java.io.IOException

object ASRService {
    private val logger = Logger.getInstance(javaClass)
    private lateinit var asrSystem: AsrSystem

    init {
        try {
            asrSystem = IdearConfiguration.getASRSystem()
            asrSystem.start()
        } catch (e: IOException) {
            logger.error("Couldn't initialize speech asrProvider!", e)
        }
    }

    fun setASRSystem(asrSystem: AsrSystem) {
        val status = ListeningState.getStatus()
        var terminated = false

        if (this.asrSystem != asrSystem) {
            terminate()
            terminated = true
        }

        this.asrSystem = asrSystem
        if (terminated && status == ListeningState.Status.ACTIVE) {
            asrSystem.start()
        }
    }

    fun waitForUtterance() = asrSystem.waitForUtterance()

    /** Called from AsrService when the user presses the start button. */
    fun activate() {
        ListeningState.activate()
        asrSystem.startRecognition()
    }

    /** Called from AsrService when the user presses the stop button. */
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

}

// This is for testing purposes solely
fun main() {
    ASRService.activate()
}
