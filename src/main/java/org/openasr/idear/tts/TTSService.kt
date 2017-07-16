package org.openasr.idear.tts

import org.openasr.idear.settings.IdearConfiguration

object TTSService {
    var ttsProvider = IdearConfiguration.getTTSProvider()

    fun say(text: String?) {
        ttsProvider.say(text)
    }

    fun dispose() {
        ttsProvider.dispose()
    }
}
