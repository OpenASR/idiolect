package org.openasr.idiolect.asr.vosk

import org.openasr.idiolect.asr.AsrProviderSettingsForm

class VoskSettingsForm : AsrProviderSettingsForm<VoskConfigurable>(VoskModelManager) {

    override fun reset() {
        modelPathChooser.text = VoskConfig.settings.modelPath
        languageCombo.selectedItem = VoskConfig.settings.language
    }
}
