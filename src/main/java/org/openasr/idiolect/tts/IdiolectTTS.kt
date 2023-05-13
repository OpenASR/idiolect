package org.openasr.idiolect.tts

import io.github.jonelo.jAdapterForNativeTTS.engines.SpeechEngineNative
import org.openasr.idiolect.settings.IdiolectConfig
import java.util.*

class IdiolectTTS : TtsProvider {
    companion object {
        val speechEngine = SpeechEngineNative.getInstance()
    }

    override fun displayName() = "Native TTS"

    @Synchronized
    override fun say(utterance: String) {
        sayWithVoice(utterance, IdiolectConfig.settings.ttsService)
    }

    fun sayWithVoice(utterance: String, voice: String? = null) =
        speechEngine.apply { setVoice( voice ?: speechEngine.availableVoices.first().name) }
            .say(utterance)

    override fun dispose() = Unit
}

fun main() =
    with(Scanner(System.`in`)) { do { print("Text to speak: "); IdiolectTTS().sayWithVoice(nextLine()) } while (true) }
