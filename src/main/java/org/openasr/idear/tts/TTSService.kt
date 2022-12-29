package org.openasr.idear.tts

import org.openasr.idear.settings.IdearConfig
import org.openasr.idear.settings.IdearConfigurable

object TTSService {
    fun say(text: String) {
        IdearConfig.getTtsProvider().say(text)
    }

    fun dispose() = IdearConfig.getTtsProvider().dispose()
}
