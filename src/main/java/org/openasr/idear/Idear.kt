package org.openasr.idear

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.PluginId
import org.openasr.idear.asr.ASRService
import org.openasr.idear.tts.TTSService

object Idear : Disposable {
    val plugin = PluginManager.getPlugin(PluginId.getId("com.jetbrains.idear"))!!

    override fun dispose() {
        ASRService.dispose()
        TTSService.dispose()
    }
}