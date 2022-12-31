package org.openasr.idear.asr.vosk

import com.intellij.openapi.options.Configurable
import org.openasr.idear.asr.vosk.VoskConfig.Companion.settings

/**
 * Manages the Settings UI
 */
class VoskConfigurable : Configurable {
    private val gui by lazy(::VoskSettingsForm)

    override fun getDisplayName() = "Vosk"

    override fun createComponent() = gui.rootPanel

    override fun isModified(): Boolean {
        return gui.modelPathChooser.text != settings.modelPath
            || gui.languageCombo.selectedItem != settings.language
    }

    override fun apply() {
        if (gui.modelPathChooser.text != settings.modelPath) {
            settings.modelPath = gui.modelPathChooser.text
            VoskAsr.setModel(settings.modelPath)
            VoskAsr.activate()
        }

        if (gui.languageCombo.selectedItem != settings.language) {
            settings.language = gui.languageCombo.selectedItem as String
        }
    }

    override fun reset() {
        gui.reset(settings)
    }
}
