package org.openasr.idiolect.asr.azure

import com.intellij.openapi.components.*
import com.intellij.util.application

/**
 * Persists State across IDE restarts
 */
@State(name = "Azure",
    storages = [Storage("\$APP_CONFIG\$/idiolect.xml")],
    category = SettingsCategory.PLUGINS
)
class AzureConfig : PersistentStateComponent<AzureConfig.Settings> {
    private var settings = Settings()

    companion object {
        val settings get() = application.getService(AzureConfig::class.java).settings
    }

    override fun getState() = settings

    override fun loadState(state: Settings) {
        settings = state
    }

    data class Settings(var speechSubscriptionKey: String = "", var serviceRegion: String = "")
}
