package org.openasr.idiolect.asr.whisper.cpp.settings

import com.intellij.openapi.options.Configurable
import org.openasr.idiolect.asr.vosk.VoskConfig.Companion.settings
import org.openasr.idiolect.asr.whisper.cpp.WhisperCppAsr

/**
 * Manages the Settings UI
 */
class WhisperCppConfigurable : Configurable {
    private val gui by lazy(::WhisperCppSettingsForm)

    override fun getDisplayName() = "Whisper.cpp"

    override fun createComponent() = gui.rootPanel

    override fun isModified(): Boolean {
        return gui.modelPathChooser.text != settings.modelPath
            || gui.languageCombo.selectedItem != settings.language
    }

    override fun apply() {
        if (gui.modelPathChooser.text != settings.modelPath) {
            settings.modelPath = gui.modelPathChooser.text
            WhisperCppAsr.setModel(settings.modelPath)
            WhisperCppAsr.activate()
        }

        if (gui.languageCombo.selectedItem != settings.language) {
            settings.language = gui.languageCombo.selectedItem as String
        }
    }

    override fun reset() {
        gui.reset()
    }
}
