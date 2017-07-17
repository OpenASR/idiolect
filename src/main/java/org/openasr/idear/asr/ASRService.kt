package org.openasr.idear.asr

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.nlp.NlpProvider
import org.openasr.idear.settings.IdearConfiguration
import java.io.IOException

object ASRService {
    private lateinit var asrSystem: ASRSystem

    fun init() {
        try {
            asrSystem = IdearConfiguration.getASRSystem()
            asrSystem.start()
        } catch (e: IOException) {
            logger.error( "Couldn't initialize speech asrProvider!", e)
        }
    }

    fun waitForUtterance() = asrSystem.waitForUtterance()

    fun activate(): Boolean = ListeningState.activate()

    fun deactivate(): Boolean = ListeningState.standBy()

    private fun terminate() = asrSystem.stopRecognition()

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
    ASRService.activate()
}
