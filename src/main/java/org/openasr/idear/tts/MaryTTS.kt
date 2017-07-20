package org.openasr.idear.tts

import com.intellij.openapi.diagnostic.Logger
import marytts.*
import marytts.exceptions.MaryConfigurationException
import marytts.modules.synthesis.Voice
import marytts.util.data.audio.AudioPlayer
import java.util.*

/**
 * Created by breandan on 7/9/2015.
 */
object MaryTTS : TTSProvider {
    val logger = Logger.getInstance(MaryTTS::class.java)
    private lateinit var voice: Voice
    private lateinit var maryTTS: MaryInterface

    init {
        try {
            maryTTS = LocalMaryInterface()
            val systemLocale = Locale.getDefault()
            voice = if (maryTTS.availableLocales.contains(systemLocale))
                Voice.getDefaultVoice(systemLocale)
            else
                Voice.getVoice(maryTTS.availableVoices.iterator().next())

            maryTTS.locale = voice.locale
            maryTTS.voice = voice.name
        } catch (e: MaryConfigurationException) {
            logger.error(e)
        }
    }

    @Synchronized
    override fun say(utterance: String): Boolean {
        if (utterance.isEmpty()) return false

        try {
            AudioPlayer(maryTTS.generateAudio(utterance)).start()
        } catch (e: Exception) {
            logger.error("Sorry! Could not pronounce $utterance", e)
            return false
        }

        return true
    }

    override fun dispose() {}
}

fun main(args: Array<String>) =
        with(Scanner(System.`in`)) {
            do {
                print("Text to speak: ")
            } while (MaryTTS.say(nextLine()))
        }
