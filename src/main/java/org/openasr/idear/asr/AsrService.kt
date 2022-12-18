package org.openasr.idear.asr

import com.intellij.openapi.diagnostic.logger
import org.openasr.idear.settings.IdearConfiguration

object AsrService {
    private val log = logger<AsrService>()
    private lateinit var asrSystem: AsrSystem

    init {
      System.setProperty("jna.nounpack", "false")
      System.setProperty("jna.noclasspath", "false")
      try {
          asrSystem = IdearConfiguration.getAsrSystem()
          asrSystem.start()
      } catch (e: Exception) {
          log.error("Couldn't initialize speech asrProvider!", e)
      }
    }

    fun setAsrSystem(asrSystem: AsrSystem) {
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

    fun setGrammar(grammar: Array<String>) {
        asrSystem.setGrammar(grammar)
    }

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
    AsrService.activate()
}
