package org.openasr.idear.tts

import org.openasr.idear.settings.IdearConfig

object TtsService {
    fun say(text: String) {
        IdearConfig.getTtsProvider().say(text)
    }

    fun dispose() = IdearConfig.getTtsProvider().dispose()
}
