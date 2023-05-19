package org.openasr.idiolect.asr.whisper.server.settings

import com.intellij.openapi.options.Configurable
import org.openasr.idiolect.asr.vosk.VoskConfig.Companion.settings
import org.openasr.idiolect.asr.whisper.server.WhisperServerAsr

/**
 * Manages the Settings UI
 */
class WhisperServerConfigurable : Configurable {
    private val gui by lazy(::WhisperServerSettingsForm)

    override fun getDisplayName() = "Whisper-server"

    override fun createComponent() = gui.rootPanel

    override fun isModified(): Boolean {
        return gui.modelPathChooser.text != settings.modelPath
            || gui.languageCombo.selectedItem != settings.language
    }

    override fun apply() {
        if (gui.modelPathChooser.text != settings.modelPath) {
            settings.modelPath = gui.modelPathChooser.text

            WhisperServerAsr.setModel(settings.modelPath)
        }

        if (gui.languageCombo.selectedItem != settings.language) {
            settings.language = gui.languageCombo.selectedItem as String
        }
    }

    override fun reset() {
        gui.reset()
    }
}
