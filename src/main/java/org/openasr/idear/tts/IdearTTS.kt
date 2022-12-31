package org.openasr.idear.tts

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import io.github.jonelo.jAdapterForNativeTTS.engines.*
import org.openasr.idear.recognizer.CustomMicrophone
import org.openasr.idear.settings.IdearConfig
import java.util.*

object IdearTTS : TtsProvider {
    val logger = Logger.getInstance(javaClass)
    val speechEngine = SpeechEngineNative.getInstance()

    override fun displayName() = "Native TTS"

    @Synchronized
    override fun say(utterance: String) {
        sayWithVoice(utterance, IdearConfig.settings.ttsService)
    }

    fun sayWithVoice(utterance: String, voice: String) {
        speechEngine.apply { setVoice(voice) }
            .say(utterance)
            .waitFor()
    }

    override fun dispose() = Unit
}

fun main() =
    with(Scanner(System.`in`)) { do { print("Text to speak: "); IdearTTS.say(nextLine()) } while (true) }
