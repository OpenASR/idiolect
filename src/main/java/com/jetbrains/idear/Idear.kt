package com.jetbrains.idear

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.extensions.PluginId
import com.jetbrains.idear.asr.ASRService
import com.jetbrains.idear.asr.GrammarService
import com.jetbrains.idear.tts.TTSService

class Idear : ApplicationComponent {

    override fun initComponent() {
        ServiceManager.getService(ASRService::class.java).init()
        ServiceManager.getService(GrammarService::class.java).init()
        initTTSService()
    }

    private fun initTTSService() {
        val id = PluginId.getId("com.jetbrains.idear")
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
