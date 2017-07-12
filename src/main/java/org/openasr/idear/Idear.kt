package org.openasr.idear

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.extensions.PluginId
import org.openasr.idear.asr.ASRService
import org.openasr.idear.asr.GrammarService
import org.openasr.idear.tts.TTSService

class Idear : ApplicationComponent {

    override fun initComponent() {
        ASRService.init()
        GrammarService.init()
        initTTSService()
    }

    private fun initTTSService() {
        val id = PluginId.getId("org.openasr.idear")
        val plugin = PluginManager.getPlugin(id)!!

        val current = Thread.currentThread().contextClassLoader
        try {
            val classLoader = plugin.pluginClassLoader
            Thread.currentThread().contextClassLoader = classLoader
        } finally {
            Thread.currentThread().contextClassLoader = current
        }
    }

    override fun disposeComponent() {
        ServiceManager.getService(ASRService::class.java).dispose()
        TTSService.dispose()
    }

    override fun getComponentName(): String {
        return "Idear"
    }
}
