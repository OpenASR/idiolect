package org.openasr.idear.settings

import com.intellij.openapi.ui.ComboBox
import javax.swing.JPanel

class RecognitionSettingsForm {
    private var ttsProviderCombo = ComboBox(TTSServiceId.values())
    private var asrProviderCombo = ComboBox(ASRServiceId.values())
    lateinit var rootPanel: JPanel

    companion object {
        enum class ASRServiceId(val label: String) {
            CMU_SPHINX("cmuSphinx"),
            AWS_LEX("awsLex")
        }

        enum class TTSServiceId(val label: String) {
            MARY("mary"),
            AWS_POLLY("awsPolly")
        }
    }

    var asrService: ASRServiceId
        get() = ASRServiceId.valueOf(asrProviderCombo.selectedItem.toString())
        set(value) {
            asrProviderCombo.selectedItem = value
        }
    var ttsService: TTSServiceId
        get() = TTSServiceId.valueOf(ttsProviderCombo.selectedItem.toString())
        set(value) {
            ttsProviderCombo.selectedItem = value
        }
}