package org.openasr.idiolect.asr.whisper.server.settings

import org.openasr.idiolect.asr.AsrProviderSettingsForm

class WhisperServerSettingsForm : AsrProviderSettingsForm<WhisperServerConfigurable>(WhisperServerModelManager) {

    override fun reset() {
        modelPathChooser.text = WhisperServerConfig.settings.modelPath
        languageCombo.selectedItem = WhisperServerConfig.settings.language
    }
}
