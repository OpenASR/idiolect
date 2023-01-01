package org.openasr.idiolect.tts

import org.openasr.idiolect.settings.IdiolectConfig

object TtsService {
    fun say(text: String) {
        IdiolectConfig.getTtsProvider().say(text)
    }

    fun dispose() = IdiolectConfig.getTtsProvider().dispose()
}
