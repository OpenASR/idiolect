package org.openasr.idear.settings

import com.intellij.openapi.ui.ComboBox
import javax.swing.JPanel

class RecognitionSettingsForm {
    val ttsProviderCombo = ComboBox(TTSServiceId.values())
    val asrProviderCombo = ComboBox(ASRServiceId.values())
    lateinit var rootPanel: JPanel

    var asrService: ASRServiceId
        get() = ASRServiceId.valueOf(asrProviderCombo.selectedItem.toString())
        set(value) { asrProviderCombo.selectedItem = value}
    var ttsService: TTSServiceId
        get() = TTSServiceId.valueOf(ttsProviderCombo.selectedItem.toString())
        set(value) { ttsProviderCombo.selectedItem = value }
}