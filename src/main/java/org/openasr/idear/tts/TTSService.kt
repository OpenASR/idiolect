package org.openasr.idear.tts

object TTSService {
    var ttsProvider: TTSProvider? = null

    init {
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
