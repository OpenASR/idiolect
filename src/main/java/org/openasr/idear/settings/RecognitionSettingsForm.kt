package org.openasr.idear.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.COLUMNS_SHORT
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import org.openasr.idear.asr.AsrService
import java.awt.Font
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.text.JTextComponent
import kotlin.reflect.KProperty

internal class RecognitionSettingsForm {
    private val ttsProviderCombo = ComboBox<String>()
    private val asrProviderCombo = ComboBox<String>()
    private val asrModelPathEdit = JTextField()
    private val nlpProviderCombo = ComboBox<String>()

    init {
        asrModelPathEdit.apply { font = Font("monospaced", font.style, font.size) }
    }

    internal val rootPanel: JPanel = panel {
        group("Providers") {
            row("Text-to-Speech Provider") { cell(ttsProviderCombo).columns(COLUMNS_SHORT) }
            row("Speech Recognition Provider") { cell(asrProviderCombo).columns(COLUMNS_SHORT) }
            row("Speech Recognition Model") { cell(asrModelPathEdit).columns(COLUMNS_LARGE) }
            row("Natural Language Processor") { cell(nlpProviderCombo).columns(COLUMNS_SHORT) }
        }
    }

    internal var asrModelPath by asrModelPathEdit
    internal var ttsService by ttsProviderCombo
    internal var nlpService by nlpProviderCombo
    internal var AsrService by asrProviderCombo

    fun setAsrOptions(options: List<String>) = setOptions(asrProviderCombo, options)
    fun setNlpOptions(options: List<String>) = setOptions(nlpProviderCombo, options)
    fun setTtsOptions(options: List<String>) = setOptions(ttsProviderCombo, options)

    private fun setOptions(comboBox: ComboBox<String>, options: List<String>) {
        comboBox.removeAllItems()
        options.forEach { comboBox.addItem(it) }
    }

    private operator fun JTextComponent.getValue(a: RecognitionSettingsForm, p: KProperty<*>) = text
    private operator fun JTextComponent.setValue(a: RecognitionSettingsForm, p: KProperty<*>, s: String) = setText(s)

    private inline operator fun <reified T> ComboBox<T>.getValue(a: RecognitionSettingsForm, p: KProperty<*>) = selectedItem as T
    private operator fun <T> ComboBox<T>.setValue(a: RecognitionSettingsForm, p: KProperty<*>, item: T) = setSelectedItem(item)

    private inline fun <reified T: Enum<T>> ComboBox<T>.setupEnumItems(crossinline onChanged: (T) -> Unit) {
        T::class.java.enumConstants.forEach(this::addItem)
        addActionListener { onChanged(selectedItem as T) }
    }
}
