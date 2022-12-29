package org.openasr.idear.tts

import io.github.jonelo.jAdapterForNativeTTS.engines.*
import java.util.*

object IdearTTS : TtsProvider {
    //    val logger = Logger.getInstance(javaClass)
    val speechEngine = SpeechEngineNative.getInstance()

    private var voice: Voice? = null

    override fun displayName() = "Native TTS"

    override fun activate() {
        val voice = speechEngine.availableVoices.firstOrNull()

        speechEngine.setVoice(voice?.name)
    }

    @Synchronized
    override fun say(utterance: String) = speechEngine.say(utterance)

    override fun dispose() = Unit
}

fun main() =
    with(Scanner(System.`in`)) { do { print("Text to speak: "); IdearTTS.say(nextLine()) } while (true) }
