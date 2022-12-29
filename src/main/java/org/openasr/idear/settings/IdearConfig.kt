package org.openasr.idear.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.application
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.asr.AsrSystem
import org.openasr.idear.nlp.NlpProvider
import org.openasr.idear.tts.TtsProvider
import java.util.concurrent.atomic.AtomicReference

/**
 * Persists State across IDE restarts
 */
@State(name = "Idear",
    storages = [Storage("\$APP_CONFIG\$/idear.xml")],
    category = SettingsCategory.PLUGINS)
class IdearConfig : PersistentStateComponent<IdearConfig.Settings>  {
    private var settings = Settings()

    companion object {
        val settings get() = application.getService(IdearConfig::class.java).settings
        val idearHomePath = System.getProperty("user.home") + "/.idear"

        private var asrProvider = AtomicReference<AsrProvider?>()
        private var ttsProvider = AtomicReference<TtsProvider?>()
        private var nlpProvider = AtomicReference<NlpProvider?>()

        /** Called by AsrService */
        fun initialiseAsrSystem(): AsrSystem {
            val asrProvider = getAsrProvider()
            val nlpProvider = getNlpProvider()

            return ExtensionManager.asrSystemEp.extensionList.first { e -> e.supportsAsrAndNlp(asrProvider, nlpProvider) }
                .apply { initialise(asrProvider, nlpProvider) }
        }

        // TODO: list voices by locale
        // TODO: allow user to select voice
        fun getTtsProvider() = activateExtension(ExtensionManager.ttsSelector, settings.ttsService, ttsProvider)

        private fun getAsrProvider() =
            activateExtension(ExtensionManager.asrSelector, settings.asrService, asrProvider)

        private fun getNlpProvider() = activateExtension(ExtensionManager.nlpSelector, settings.nlpService, nlpProvider)

        private fun <T : ConfigurableExtension> activateExtension(extensionSelector: ExtensionSelector<T>,
                                                                  displayName: String,
                                                                  ref: AtomicReference<T?>,
                                                                  configure: (T.() -> Unit)? = null): T {
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

    override fun getState() = settings

    override fun loadState(state: Settings) {
        settings = state
    }

    data class Settings(var asrService: String = "",
                        var nlpService: String = "",
                        var ttsService: String = "")
}
