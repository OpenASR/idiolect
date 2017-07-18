package org.openasr.idear.tts

import org.openasr.idear.settings.IdearConfiguration

object TTSService {
    fun say(text: String) {
        IdearConfiguration.getTTSProvider().say(text)
    }

    fun dispose() = IdearConfiguration.getTTSProvider().dispose()
}
