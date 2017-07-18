package org.openasr.idear.settings

import com.intellij.openapi.ui.ComboBox
import javax.swing.JPanel

class RecognitionSettingsForm {
    private lateinit var ttsProviderCombo: ComboBox<TTSServiceId>
    private lateinit var asrProviderCombo: ComboBox<ASRServiceId>
    private lateinit var nlpProviderCombo: ComboBox<NLPServiceId>
    lateinit var rootPanel: JPanel

    init {
        TTSServiceId.values().forEach { ttsProviderCombo.addItem(it) }
        NLPServiceId.values().forEach { nlpProviderCombo.addItem(it) }
        ASRServiceId.values().forEach { asrProviderCombo.addItem(it) }
    }

    companion object {
        enum class ASRServiceId(val label: String) {
            CMU_SPHINX("CMU Sphinx"), AWS_LEX("Amazon Lex");

            override fun toString() = label
        }

        enum class NLPServiceId(val label: String) {
            PATTERN("Pattern"), AWS_LEX("Amazon Lex");

            override fun toString() = label
        }

        enum class TTSServiceId(val label: String) {
            MARY("Mary TTS"), AWS_POLLY("Amazon Polly");

            override fun toString() = label
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

    var nlpService: NLPServiceId
        get() = NLPServiceId.valueOf(nlpProviderCombo.selectedItem.toString())
        set(value) {
            nlpProviderCombo.selectedItem = value
        }
}