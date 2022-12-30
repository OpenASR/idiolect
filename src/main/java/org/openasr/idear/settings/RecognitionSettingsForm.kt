package org.openasr.idear.settings

import com.intellij.openapi.observable.util.*
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.dsl.builder.COLUMNS_SHORT
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import org.openasr.idear.tts.IdearTTS
import javax.swing.JPanel
import javax.swing.text.JTextComponent
import kotlin.reflect.KProperty

internal class RecognitionSettingsForm {
    private val ttsProviderCombo = ComboBox<String>()
        .apply {
            whenItemSelectedFromUi {
                IdearTTS.sayWithVoice("Hello, my name is $it!", it)
                IdearConfig.settings.ttsService = it
            }
        }

    private val asrProviderCombo = ComboBox<String>()
    private val nlpProviderCombo = ComboBox<String>()

    internal val rootPanel: JPanel = panel {
        group("Providers") {
            row("Text-to-Speech Provider") { cell(ttsProviderCombo).columns(COLUMNS_SHORT) }
            row("Speech Recognition Provider") { cell(asrProviderCombo).columns(COLUMNS_SHORT) }
            row("Natural Language Processor") { cell(nlpProviderCombo).columns(COLUMNS_SHORT) }
// Nice-to-have, but throws NPE
//            row { link("Custom phrases") { _ ->
//                CustomUtteranceActionRecognizer.openCustomPhrasesFile(ProjectManager.getInstance().defaultProject)
//            }}
        }
    }

    internal var ttsService by ttsProviderCombo
    internal var nlpService by nlpProviderCombo
    internal var asrService by asrProviderCombo

    fun reset(settings: IdearConfig.Settings) {
        setAsrOptions(ExtensionManager.asrEp.extensionList.map { e -> e.displayName() })
        setNlpOptions(ExtensionManager.nlpEp.extensionList.map { e -> e.displayName() })
        setTtsOptions(IdearTTS.speechEngine.availableVoices?.map { it.name } ?: emptyList())

        asrService = settings.asrService
        nlpService = settings.nlpService
        ttsService = settings.ttsService
    }

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
