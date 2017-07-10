package org.openasr.idear.tts

/**
 * Created by breandan on 7/9/2015.
 */
object TTSService {
    var ttsProvider: TTSProvider? = null

    init {
// TODO: select from IdearSettingsProvider
//        ttsProvider = MaryTTS()
        ttsProvider = PollyTTS()
    }

    // TODO: list voices by locale
    // TODO: allow user to select voice

    fun say(text: String?) {
        ttsProvider?.say(text)
    }

    fun dispose() {
        ttsProvider?.dispose()
        ttsProvider = null
    }
}
