package org.openasr.idear.tts

import marytts.LocalMaryInterface
import marytts.MaryInterface
import marytts.exceptions.MaryConfigurationException
import marytts.exceptions.SynthesisException
import marytts.modules.synthesis.Voice
import marytts.util.data.audio.AudioPlayer
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by breandan on 7/9/2015.
 */
object TTSService {
    val logger = Logger.getLogger(TTSService::class.java.simpleName)
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

    fun say(text: String?) {
        if (text == null || text.isEmpty()) {
            return
        }

        try {
            val audio = maryTTS!!.generateAudio(text)
            val player = AudioPlayer(audio)
            player.start()
            player.join()
        } catch (e: SynthesisException) {
            logger.log(Level.SEVERE, String.format("Sorry! Could not pronounce '%s'", text), e)
        } catch (e: InterruptedException) {
            logger.log(Level.SEVERE, String.format("Sorry! Could not pronounce '%s'", text), e)
        }
    }

    fun dispose() {

    }

}

fun main(args: Array<String>) {
    val ttService = TTSService
    val scan = Scanner(System.`in`)

    while (true) {
        println("Text to speak:")
        ttService.say(scan.nextLine())
    }
}
