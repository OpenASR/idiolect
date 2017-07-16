package org.openasr.idear.tts

import com.intellij.openapi.components.ServiceManager
import org.openasr.idear.settings.IdearConfiguration

/**
 * Created by breandan on 7/9/2015.
 */
object TTSService {
    var ttsProvider: TTSProvider? = null

    init {
        val config = ServiceManager.getService(IdearConfiguration::class.java)
        ttsProvider = config.getTTSProvider()
    }

    fun say(text: String?) {
        ttsProvider?.say(text)
    }

    fun dispose() {
        ttsProvider?.dispose()
        ttsProvider = null
    }
}
