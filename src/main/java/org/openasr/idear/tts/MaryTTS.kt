package org.openasr.idear.tts

import com.intellij.openapi.diagnostic.Logger
import marytts.LocalMaryInterface
import marytts.MaryInterface
import marytts.exceptions.MaryConfigurationException
import marytts.exceptions.SynthesisException
import marytts.modules.synthesis.Voice
import marytts.util.data.audio.AudioPlayer
import java.util.*

/**
 * Created by breandan on 7/9/2015.
 */
class MaryTTS : TTSProvider {
    val logger = Logger.getInstance(MaryTTS::class.java)
    private var voice: Voice? = null
    var maryTTS: MaryInterface? = null

    init {
        try {
            maryTTS = LocalMaryInterface()
            val systemLocale = Locale.getDefault()
            if (maryTTS!!.availableLocales.contains(systemLocale)) {
                voice = Voice.getDefaultVoice(systemLocale)
            }

            if (voice == null) {
                voice = Voice.getVoice(maryTTS!!.availableVoices.iterator().next())
            }

            maryTTS!!.locale = voice!!.locale
            maryTTS!!.voice = voice!!.name
        } catch (e: MaryConfigurationException) {
            e.printStackTrace()
        }
    }

    override fun say(text: String?) {
        if (text == null || text.isEmpty()) return

        try {
            val audio = maryTTS!!.generateAudio(text)
            val player = AudioPlayer(audio)
            player.start()
            player.join()
        } catch (e: SynthesisException) {
            logger.error("Sorry! Could not pronounce $text", e)
        } catch (e: InterruptedException) {
            logger.error("Sorry! Could not pronounce $text", e)
        }
    }

    override fun dispose() {
    }
}

fun main(args: Array<String>) {
    val ttService = MaryTTS()
    val scan = Scanner(System.`in`)

    while (true) {
        println("Text to speak:")
        ttService.say(scan.nextLine())
    }
}
