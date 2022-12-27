package org.openasr.idear.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.extensions.ExtensionPointListener
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.extensions.PluginDescriptor
import com.intellij.openapi.options.Configurable
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.asr.AsrService
import org.openasr.idear.asr.AsrSystem
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

        var settings = Settings()

        fun saveModelPath(modelPath: String) {
            settings.asrModelPath = modelPath
        }

        /** Called by AsrService */
        fun initialiseAsrSystem(): AsrSystem {
            val asrProvider = getAsrProvider()
            val nlpProvider = getNlpProvider()

            return asrSystemEp.extensionList.first { e -> e.supportsAsrAndNlp(asrProvider, nlpProvider) }
                .apply { initialise(asrProvider, nlpProvider) }
        }

        // TODO: list voices by locale
        // TODO: allow user to select voice
        fun getTtsProvider() = activateExtension(ttsSelector, settings.ttsService, ttsProvider, null)

        private fun getAsrProvider() =
            activateExtension(asrSelector, settings.AsrService, asrProvider) { setModel(settings.asrModelPath) }


        private fun getNlpProvider() = activateExtension(nlpSelector, settings.nlpService, nlpProvider, null)


        private fun <T : ConfigurableExtension> activateExtension(extensionSelector: ExtensionSelector<T>,
                                                                  displayName: String,
                                                                  ref: AtomicReference<T?>,
                                                                  configure: (T.() -> Unit)?): T {
            val extension = extensionSelector.getExtensionByName(displayName)

            val current = ref.get()

            if (current != extension) {
                current?.deactivate()
                configure?.invoke(extension)
                extension.activate()
                ref.set(extension)
            }
            return extension
        }
    }

    override fun getDisplayName() = "Idear"


    override fun getState() = settings

    /**
     * This method is called when new component state is loaded.
     * The method can and will be called several times, if config files were externally changed while IDE was running.
     */
    override fun loadState(state: Settings) {
        settings = state
    }

    data class Settings(var AsrService: String = "",
                        var asrModelPath: String = "",
                        var nlpService: String = "",
                        var ttsService: String = "")

    override fun isModified(): Boolean =
        gui.AsrService != settings.AsrService ||
            gui.asrModelPath != settings.asrModelPath ||
            gui.nlpService != settings.nlpService ||
            gui.ttsService != settings.ttsService

    private val gui by lazy(::RecognitionSettingsForm)

    override fun createComponent() = gui.rootPanel

    /**
     * Stores the settings from the Swing form to the configurable component.
     * This method is called on EDT upon user's request.
     */
    override fun apply() {
        if (isModified) {
            settings.AsrService = gui.AsrService
            settings.nlpService = gui.nlpService
            settings.ttsService = gui.ttsService
            settings.asrModelPath = gui.asrModelPath

            AsrService.setAsrSystem(initialiseAsrSystem())
        }
    }

    /**
     * Loads the settings from the configurable component to the Swing form.
     * This method is called on EDT immediately after the form creation or later upon user's request.
     */
    override fun reset() {
        gui.setAsrOptions(asrEp.extensionList.map { e -> e.displayName() })
        gui.setNlpOptions(nlpEp.extensionList.map { e -> e.displayName() })
        gui.setTtsOptions(ttsEp.extensionList.map { e -> e.displayName() })

        gui.AsrService = settings.AsrService
        gui.nlpService = settings.nlpService
        gui.ttsService = settings.ttsService
        gui.asrModelPath = settings.asrModelPath
    }
}

private class ExtensionSelector<T : ConfigurableExtension>(
    val extensionPointName: ExtensionPointName<T>
) : ExtensionPointListener<T> {
    private val options =
        HashMap(extensionPointName.extensionList.associate { e -> e.displayName() to ExtensionOption(e) })

    init {
        extensionPointName.addExtensionPointListener(this, null)
    }

    fun getExtensionByName(displayName: String): T =
        options.entries.firstOrNull { it.key == displayName }?.value?.extension ?: extensionPointName.extensions.first()

    override fun extensionAdded(extension: T, pluginDescriptor: PluginDescriptor) {
        options[extension.displayName()] = ExtensionOption(extension)
    }

    override fun extensionRemoved(extension: T, pluginDescriptor: PluginDescriptor) {
        options.remove(extension.displayName())
    }
}

private class ExtensionOption<T : ConfigurableExtension>(val extension: T) {
    override fun toString() = extension.displayName()
}
