package org.openasr.idear.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.extensions.ExtensionPointListener
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.options.Configurable
import org.openasr.idear.asr.AsrSystem
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.nlp.*
import org.openasr.idear.tts.*
import java.util.concurrent.atomic.AtomicReference

/*
 * @see http://corochann.com/intellij-plugin-development-introduction-applicationconfigurable-projectconfigurable-873.html
 */
@State(name = "IdearConfiguration", storages = [(Storage("recognition.xml"))])
class IdearConfiguration : Configurable, PersistentStateComponent<IdearConfiguration.Settings> {
    companion object {
        private val asrSystemEp: ExtensionPointName<AsrSystem> = ExtensionPointName.create("org.openasr.idear.asrSystem")
        private val asrEp: ExtensionPointName<AsrProvider> = ExtensionPointName.create("org.openasr.idear.asrProvider")
        private val ttsEp: ExtensionPointName<TtsProvider> = ExtensionPointName.create("org.openasr.idear.ttsProvider")
        private val nlpEp: ExtensionPointName<NlpProvider> = ExtensionPointName.create("org.openasr.idear.nlpProvider")

        private var asrSelector: ExtensionSelector<AsrProvider> = ExtensionSelector(asrEp)
        private var ttsSelector: ExtensionSelector<TtsProvider> = ExtensionSelector(ttsEp)
        private var nlpSelector: ExtensionSelector<NlpProvider> = ExtensionSelector(nlpEp)

        private var asrProvider = AtomicReference<AsrProvider?>()
        private var ttsProvider = AtomicReference<TtsProvider?>()
        private var nlpProvider = AtomicReference<NlpProvider?>()

        private var settings = Settings()

        /** Called by AsrService */
        fun getASRSystem(): AsrSystem {
            val asrProvider = getAsrProvider()
            val nlpProvider = getNlpProvider()

            var extension = asrSystemEp.extensionList.first { e -> e.supportsAsrAndNlp(asrProvider, nlpProvider) }
            extension.initialise(asrProvider, nlpProvider)

            return extension
        }

        // TODO: list voices by locale
        // TODO: allow user to select voice
        fun getTtsProvider() = getExtension(ttsSelector, settings.ttsService, ttsProvider)

        private fun getAsrProvider() = getExtension(asrSelector, settings.asrService, asrProvider)

        private fun getNlpProvider() = getExtension(nlpSelector, settings.nlpService, nlpProvider)

        private fun <T : ConfigurableExtension> getExtension(extensionSelector: ExtensionSelector<T>,
                                                             displayName: String,
                                                             originalExtension: AtomicReference<T?>): T {
            val extension = extensionSelector.getExtensionByName(displayName)

            if (extension != originalExtension.get()) {
                originalExtension.get()?.deactivate()
                extension!!.activate()
                originalExtension.set(extension)
            }
            return extension
        }
    }

    override fun getDisplayName() = "Idear"

    override fun getState() = settings
    override fun loadState(state: Settings) {
        settings = state
    }

    private var gui = RecognitionSettingsForm()

    data class Settings(var asrService: String = "",
                        var asrModelPath: String = "",
                        var nlpService: String = "",
                        var ttsService: String = "")

    override fun isModified() = gui.asrService != settings.asrService ||
            gui.asrModelPath != settings.asrModelPath ||
            gui.nlpService != settings.nlpService ||
            gui.ttsService != settings.ttsService


    override fun createComponent() = RecognitionSettingsForm().apply { gui = this }.rootPanel

    override fun apply() {
        if (isModified) {
            settings.asrService = gui.asrService
            settings.nlpService = gui.nlpService
            settings.ttsService = gui.ttsService
            settings.asrModelPath = gui.asrModelPath
        }
    }

    override fun reset() {
        gui.setAsrOptions(asrEp.extensionList.map { e -> e.displayName() })
        gui.setNlpOptions(nlpEp.extensionList.map { e -> e.displayName() })
        gui.setTtsOptions(ttsEp.extensionList.map { e -> e.displayName() })

        gui.asrService = settings.asrService
        gui.nlpService = settings.nlpService
        gui.ttsService = settings.ttsService

        gui.asrModelPath = if (settings.asrModelPath.isNullOrEmpty())
            getAsrProvider().defaultModel()
        else
            settings.asrModelPath
    }
}

private class ExtensionSelector<T : ConfigurableExtension>(
    val extensionPointName: ExtensionPointName<T>
) : ExtensionPointListener<T> {
    private val options = HashMap<String, ExtensionOption<T>>()

    init {
        extensionPointName.addExtensionPointListener(this, null)

        for (extension in extensionPointName.extensionList) {
            val option = ExtensionOption(extension)
            options[extension.displayName()] = option
        }
    }

    fun getExtensionByName(displayName: String): T {
        for (option in options.entries) {
            if (option.key == displayName) {
                return option.value.extension
            }
        }

        return extensionPointName.extensions.first()
    }

    override fun extensionAdded(extension: T, pluginDescriptor: PluginDescriptor) {
        val option = ExtensionOption(extension)
        options[extension.displayName()] = option
    }

    override fun extensionRemoved(extension: T, pluginDescriptor: PluginDescriptor) {
        val option = options.remove(extension.displayName())
    }
}

private class ExtensionOption<T : ConfigurableExtension>(val extension: T) {
    override fun toString() = extension.displayName()
}
