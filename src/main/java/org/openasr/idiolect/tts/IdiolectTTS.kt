package org.openasr.idiolect.tts

import com.intellij.openapi.diagnostic.Logger
import io.github.jonelo.jAdapterForNativeTTS.engines.SpeechEngineNative
import org.openasr.idiolect.settings.IdiolectConfig
import java.util.*

object IdiolectTTS : TtsProvider {
    val logger = Logger.getInstance(javaClass)
    val speechEngine = SpeechEngineNative.getInstance()

    override fun displayName() = "Native TTS"

    @Synchronized
    override fun say(utterance: String) {
        sayWithVoice(utterance, IdiolectConfig.settings.ttsService)
    }

    fun sayWithVoice(utterance: String, voice: String) {
        speechEngine.apply { setVoice(voice) }
            .say(utterance)
            .waitFor()
    }

    override fun dispose() = Unit
}

fun main() =
    with(Scanner(System.`in`)) { do { print("Text to speak: "); IdiolectTTS.say(nextLine()) } while (true) }
