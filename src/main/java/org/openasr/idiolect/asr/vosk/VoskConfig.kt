package org.openasr.idiolect.asr.vosk

import com.intellij.openapi.components.*
import com.intellij.util.application

/**
 * Persists State across IDE restarts
 */
@State(name = "Vosk",
    storages = [Storage("\$APP_CONFIG\$/idiolect.xml")],
    category = SettingsCategory.PLUGINS
)
class VoskConfig : PersistentStateComponent<VoskConfig.Settings> {
    private var settings = Settings()

    companion object {
        val settings get() = application.getService(VoskConfig::class.java).settings

        fun saveModelPath(modelPath: String) {
            settings.modelPath = modelPath
        }
    }

    override fun getState() = settings

    override fun loadState(state: Settings) {
        settings = state
    }

    data class Settings(var modelPath: String = "", var language: String = "")
}
