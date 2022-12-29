package org.openasr.idear.asr.vosk

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import com.intellij.util.application

@State(name = "VoskConfiguration", storages = [(Storage("recognition.vosk.xml"))])
class VoskConfiguration : Configurable, PersistentStateComponent<VoskConfiguration.Settings> {
    private val gui by lazy(::VoskSettingsForm)
    private var settings = Settings()

    data class Settings(var modelPath: String = "", var lang: String = "en-us")

    companion object {
        val settings get() = application.getService(VoskConfiguration::class.java).settings

        fun saveModelPath(modelPath: String) {
            settings.modelPath = modelPath
        }
    }

    override fun createComponent() = gui.rootPanel

    override fun isModified(): Boolean {
        return gui.modelPathChooser.text != settings.modelPath
    }

    override fun apply() {
        if (isModified) {
            settings.modelPath = gui.modelPathChooser.text
            settings.lang = gui.languageCombo.selectedItem as String
        }
    }

    override fun reset() {
        gui.modelPathChooser.text = settings.modelPath
        gui.languageCombo.selectedItem = settings.lang
    }

    override fun getDisplayName() = "Vosk"

    override fun getState() = settings

    override fun loadState(state: Settings) {
        settings = state
    }
}
