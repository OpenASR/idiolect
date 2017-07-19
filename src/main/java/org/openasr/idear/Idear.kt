package org.openasr.idear

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.extensions.PluginId
import org.openasr.idear.asr.ASRService
import org.openasr.idear.tts.TTSService

class Idear : ApplicationComponent {

    override fun initComponent() {
        initTTSService()
    }

    private fun initTTSService() {
        val id = PluginId.getId("org.openasr.idear")
        val plugin = PluginManager.getPlugin(id)!!

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
