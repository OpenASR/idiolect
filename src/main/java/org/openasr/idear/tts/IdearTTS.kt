package org.openasr.idear.tts

import com.intellij.openapi.diagnostic.Logger
import io.github.jonelo.jAdapterForNativeTTS.engines.*
import org.openasr.idear.settings.IdearConfiguration
import java.util.*

object IdearTTS : TtsProvider {
    val logger = Logger.getInstance(javaClass)
    val speechEngine = SpeechEngineNative.getInstance()

    override fun displayName() = "Native TTS"

    @Synchronized
    override fun say(utterance: String) =
        speechEngine.apply { setVoice(IdearConfig.settings.ttsService) }.say(utterance)

    fun sayWithVoice(utterance: String, voice: String) =
        speechEngine.apply { setVoice(voice) }.say(utterance)

    override fun dispose() = Unit
}

fun main() =
    with(Scanner(System.`in`)) { do { print("Text to speak: "); IdearTTS.say(nextLine()) } while (true) }
