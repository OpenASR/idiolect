package org.openasr.idear

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.extensions.PluginId
import org.openasr.idear.asr.ASRService
import org.openasr.idear.tts.TTSService

object Idear : Disposable {
    // TODO: PluginManager.getPlugin is deprecated, but this alternative is only supported in #193 (2019.3)
    //       Currently we're supporting <idea-version since-build="131"/> which is pre-2016
//    val plug = PluginManagerCore.getPlugin(PluginId.getId("com.jetbrains.idear"))
    @Suppress("DEPRECATION")
    val plugin = PluginManager.getPlugin(PluginId.getId("com.jetbrains.idear"))!!

    override fun dispose() {
        ASRService.dispose()
        TTSService.dispose()
    }
}