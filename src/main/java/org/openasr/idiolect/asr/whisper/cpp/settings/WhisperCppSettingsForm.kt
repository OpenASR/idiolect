package org.openasr.idiolect.asr.whisper.cpp.settings

import org.openasr.idiolect.asr.AsrProviderSettingsForm

class WhisperCppSettingsForm : AsrProviderSettingsForm<WhisperCppConfigurable>(WhisperCppModelManager) {

    override fun reset() {
        modelPathChooser.text = WhisperCppConfig.settings.modelPath
        languageCombo.selectedItem = WhisperCppConfig.settings.language
        selectModel(WhisperCppConfig.settings.modelPath)
    }
}
