package org.openasr.idear.settings

import com.intellij.openapi.extensions.ExtensionPointListener
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.ui.ComboBox
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.nlp.NlpProvider
import org.openasr.idear.tts.TtsProvider
import javax.swing.JPanel
import javax.swing.JTextField

private class ExtensionSelector<T : ConfigurableExtension>(
    extensionPointName: ExtensionPointName<T>,
    val combo: ComboBox<ExtensionOption<T>>
) : ExtensionPointListener<T>  {
    private val options = HashMap<String, ExtensionOption<T>>()

    init {
        extensionPointName.addExtensionPointListener(this, null)

        for (extension in extensionPointName.extensionList) {
            val option = ExtensionOption(extension)
            options[extension.displayName()] = option
            combo.addItem(option)
        }
    }

    fun getExtensionByName(displayName: String): T {
        for (option in options.entries) {
            if (option.key == displayName) {
                return option.value.extension
            }
        }
    }

    override fun extensionAdded(extension: T, pluginDescriptor: PluginDescriptor) {
        val option = ExtensionOption(extension)
        options[extension.displayName()] = option
        combo.addItem(option)
    }

    override fun extensionRemoved(extension: T, pluginDescriptor: PluginDescriptor) {
        val option = options.remove(extension.displayName())
        combo.removeItem(option)
    }
}

private class ExtensionOption<T : ConfigurableExtension>(val extension: T) {
    override fun toString() = extension.displayName()
}


class RecognitionSettingsForm {
    private val ASR_EP: ExtensionPointName<AsrProvider> = ExtensionPointName.create("org.openasr.idear.asrProvider")
    private val TTS_EP: ExtensionPointName<TtsProvider> = ExtensionPointName.create("org.openasr.idear.ttsProvider")
    private val NLP_EP: ExtensionPointName<NlpProvider> = ExtensionPointName.create("org.openasr.idear.nlpProvider")

    private lateinit var ttsProviderCombo: ComboBox<ExtensionOption<TtsProvider>>
    private lateinit var asrProviderCombo: ComboBox<ExtensionOption<AsrProvider>>
    private lateinit var asrModelPathEdit: JTextField
    private lateinit var nlpProviderCombo: ComboBox<ExtensionOption<NlpProvider>>
    lateinit var rootPanel: JPanel
    private var asrSelector: ExtensionSelector<AsrProvider> = ExtensionSelector(ASR_EP, asrProviderCombo)
    private var ttsSelector: ExtensionSelector<TtsProvider>
    private var nlpSelector: ExtensionSelector<NlpProvider>

    init {
        ttsSelector = ExtensionSelector(TTS_EP, ttsProviderCombo)
        nlpSelector = ExtensionSelector(NLP_EP, nlpProviderCombo)
    }

    var asrModelPath: String
        get() = asrModelPathEdit.text
        set(value) {
            asrModelPathEdit.text = value
        }

    var ttsService: String
        get() = getTtsProvider().displayName()
        set(value) = setTtsProvider(value)

    var nlpService: String
        get() = getNlpProvider().displayName()
        set(value) = setNlpProvider(value)

    var asrService: String
        get() = getAsrProvider().displayName()
        set(value) = setAsrProvider(value)

    fun getAsrProvider() = (asrProviderCombo.selectedItem as ExtensionOption<AsrProvider>).extension

    fun setAsrProvider(displayName: String) {
        val extension = asrSelector.getExtensionByName(displayName)
        asrProviderCombo.selectedItem = extension
        asrModelPathEdit.text = extension.defaultModel()
    }

    fun getTtsProvider() = (ttsProviderCombo.selectedItem as ExtensionOption<TtsProvider>).extension

    fun setTtsProvider(displayName: String) {
        val extension = ttsSelector.getExtensionByName(displayName)
        ttsProviderCombo.selectedItem = extension
    }

    fun getNlpProvider() = (nlpProviderCombo.selectedItem as ExtensionOption<AsrProvider>).extension

    fun setNlpProvider(displayName: String) {
        val extension = nlpSelector.getExtensionByName(displayName)
        nlpProviderCombo.selectedItem = extension
    }
}
