package org.openasr.idiolect.asr.whisper.cpp.settings

import com.intellij.openapi.components.*
import com.intellij.util.application

/**
 * Persists State across IDE restarts
 */
@State(name = "Whisper.cpp",
    storages = [Storage("\$APP_CONFIG\$/idiolect.xml")],
    category = SettingsCategory.PLUGINS
)
class WhisperCppConfig : PersistentStateComponent<WhisperCppConfig.Settings> {
    private var settings = Settings()

    companion object {
        val settings get() = application.getService(WhisperCppConfig::class.java).settings

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
