package org.openasr.idear.settings

import com.intellij.openapi.ui.ComboBox
import javax.swing.JPanel
import javax.swing.JTextField

class RecognitionSettingsForm {
    private lateinit var ttsProviderCombo: ComboBox<String>
    private lateinit var asrProviderCombo: ComboBox<String>
    private lateinit var asrModelPathEdit: JTextField
    private lateinit var nlpProviderCombo: ComboBox<String>
    lateinit var rootPanel: JPanel

    var asrModelPath: String
        get() = asrModelPathEdit.text
        set(value) {
            asrModelPathEdit.text = value
        }

    var ttsService: String
        get() = ttsProviderCombo.selectedItem as String
        set(value) {
            ttsProviderCombo.selectedItem = value
        }

    var nlpService: String
        get() = nlpProviderCombo.selectedItem as String
        set(value) {
            nlpProviderCombo.selectedItem = value
        }

    var asrService: String
        get() = asrProviderCombo.selectedItem as String
        set(value) {
            asrProviderCombo.selectedItem = value
        }


    fun setAsrOptions(options: List<String>) {
        setOptions(asrProviderCombo, options)
    }

    fun setNlpOptions(options: List<String>) {
        setOptions(nlpProviderCombo, options)
    }

    fun setTtsOptions(options: List<String>) {
        setOptions(ttsProviderCombo, options)
    }

    private fun setOptions(comboBox: ComboBox<String>, options: List<String>) {
        comboBox.removeAllItems()
        for (option in options) {
            comboBox.addItem(option)
        }
    }
}
