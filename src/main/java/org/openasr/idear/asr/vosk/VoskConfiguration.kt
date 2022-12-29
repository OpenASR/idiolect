package org.openasr.idear.asr.vosk

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import com.intellij.util.application

@State(name = "VoskConfiguration",
    storages = [Storage("\$APP_CONFIG$/idear.vosk.xml"
//        , storageClass = com.intellij.configurationStore.FileBasedStorage::class
    )],
    category = SettingsCategory.PLUGINS)
class VoskConfiguration : Configurable, PersistentStateComponent<VoskConfiguration.Settings> {
    private val gui by lazy(::VoskSettingsForm)
    private var settings = Settings()

    override fun getDisplayName() = "Vosk"

    companion object {
        val settings get() = application.getService(VoskConfiguration::class.java).settings

        fun saveModelPath(modelPath: String) {
            settings.modelPath = modelPath
        }
    }

    override fun createComponent() = gui.rootPanel

    override fun isModified(): Boolean {
        return gui.modelPathChooser.text != settings.modelPath
            || gui.languageCombo.selectedItem != settings.lang
    }

    override fun apply() {
        if (gui.modelPathChooser.text != settings.modelPath) {
            settings.modelPath = gui.modelPathChooser.text

            VoskAsr.instance.activate()
        }

        if (gui.languageCombo.selectedItem != settings.lang) {
            settings.lang = gui.languageCombo.selectedItem as String
        }
    }

    override fun reset() {
        gui.modelPathChooser.text = settings.modelPath
        gui.languageCombo.selectedItem = settings.lang
    }

    override fun getState() = settings

    override fun loadState(state: Settings) {
        settings = state
    }

    data class Settings(var modelPath: String = "", var lang: String = "US English")
}
