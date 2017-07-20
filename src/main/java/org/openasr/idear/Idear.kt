package org.openasr.idear

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.extensions.PluginId
import org.openasr.idear.asr.ASRService
import org.openasr.idear.tts.TTSService

object Idear : ApplicationComponent {
    val plugin = PluginManager.getPlugin(PluginId.getId("com.jetbrains.idear"))!!

    override fun initComponent() = initTTSService()

    private fun initTTSService() {
        val currentThread = Thread.currentThread()
        val currentClassLoader = Thread.currentThread().contextClassLoader
        try {
            currentThread.contextClassLoader = plugin.pluginClassLoader
        } finally {
            currentThread.contextClassLoader = currentClassLoader
        }
    }

    override fun disposeComponent() {
        ASRService.dispose()
        TTSService.dispose()
    }

    override fun getComponentName() = "Idear"
}
