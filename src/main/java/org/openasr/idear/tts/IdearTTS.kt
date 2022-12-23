package org.openasr.idear.tts

import marytts.*
import marytts.exceptions.MaryConfigurationException
import marytts.modules.synthesis.Voice
import marytts.util.data.audio.AudioPlayer
import java.util.*

object IdearTTS : TtsProvider {
    //    val logger = Logger.getInstance(javaClass)
    private lateinit var voice: Voice
    private lateinit var maryTTS: MaryInterface

    override fun displayName() = "Mary TTS"

    override fun activate() {
        if (!this::maryTTS.isInitialized) {
            try {
                // This line is important: https://github.com/OpenASR/idear/issues/31
                Thread.currentThread().contextClassLoader = this.javaClass.classLoader

                maryTTS = LocalMaryInterface()
                val systemLocale = Locale.getDefault()
//                logger.info("Getting MaryTTS voice for: $systemLocale")
                voice = if (systemLocale in maryTTS.availableLocales)
                    Voice.getDefaultVoice(systemLocale)
                else
                    Voice.getVoice(maryTTS.availableVoices.iterator().next())

                maryTTS.locale = voice.locale
                maryTTS.voice = voice.name
            } catch (e: MaryConfigurationException) {
//                logger.error(e)
            }
        }
    }

    @Synchronized
    override fun say(utterance: String): Boolean =
        // macSay if Mac or jvmSay otherwise
        if (System.getProperty("os.name").lowercase().contains("mac")) {
            macSay(utterance)
        } else {
            jvmSay(utterance)
        }

    fun jvmSay(utterance: String): Boolean {
        if (utterance.isEmpty()) return false

        try {
            AudioPlayer(maryTTS.generateAudio(utterance)).start()
        } catch (e: Exception) {
//          logger.error("Sorry! Could not pronounce $utterance", e)
            return false
        }

        return true
    }

    fun macSay(utterance: String) =
        try { Runtime.getRuntime().exec("say \"$utterance\""); true } catch (e: Exception) { false }

    override fun dispose() = Unit
}

fun main() =
    with(Scanner(System.`in`)) { do { print("Text to speak: ") } while (IdearTTS.say(nextLine())) }
