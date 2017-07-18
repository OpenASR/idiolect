package org.openasr.idear.settings

import com.intellij.openapi.ui.ComboBox
import javax.swing.JPanel

class RecognitionSettingsForm {
//    private var ttsProviderCombo = ComboBox(TTSServiceId.values().map({option -> option.label}).toTypedArray())
//    private var asrProviderCombo = ComboBox(ASRServiceId.values().map({option -> option.label}).toTypedArray())
    private var ttsProviderCombo = ComboBox(TTSServiceId.values())
    private var asrProviderCombo = ComboBox(ASRServiceId.values())
    lateinit var rootPanel: JPanel

    companion object {
        enum class ASRServiceId(val label: String) {
            CMU_SPHINX("CMU Sphinx"),
            AWS_LEX("Amazon Lex")
        }

        enum class NLPServiceId(val label: String) {
            PATTERN("Pattern"),
            AWS_LEX("Amazon Lex")
        }

        enum class TTSServiceId(val label: String) {
            MARY("Mary TTS"),
            AWS_POLLY("Amazon Polly")
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