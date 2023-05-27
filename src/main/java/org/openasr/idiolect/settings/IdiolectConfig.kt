package org.openasr.idiolect.settings

import com.intellij.openapi.components.*
import com.intellij.util.application
import org.openasr.idiolect.asr.*
import org.openasr.idiolect.nlp.NlpProvider
import org.openasr.idiolect.recognizer.CustomMicrophone
import org.openasr.idiolect.tts.TtsProvider
import java.util.concurrent.atomic.AtomicReference

/**
 * Persists State across IDE restarts
 */
@State(name = "Idiolect",
    storages = [Storage("\$APP_CONFIG\$/idiolect.xml")],
    category = SettingsCategory.PLUGINS)
class IdiolectConfig : PersistentStateComponent<IdiolectConfig.Settings>  {
    private var settings = Settings()

    companion object {
        val settings get() = application.getService(IdiolectConfig::class.java).settings
        val idiolectHomePath = System.getProperty("user.home") + "/.idiolect"
        private val microphone: CustomMicrophone = service()

        private var asrProvider = AtomicReference<AsrProvider?>()
        private var ttsProvider = AtomicReference<TtsProvider?>()
        private var nlpProvider = AtomicReference<NlpProvider?>()

        init {
            initialiseAudioInput()
        }

        /** Called by AsrService */
        fun initialiseAsrSystem(asrService: String? = null): AsrSystem {
            val asrProvider = getAsrProvider(asrService)
            val nlpProvider = getNlpProvider()

            return ExtensionManager.asrSystemEp.extensionList.first { e -> e.supportsAsrAndNlp(asrProvider, nlpProvider) }
                .apply { initialise(asrProvider, nlpProvider) }
        }

        private fun initialiseAudioInput() {
            microphone.useInputDevice(settings.audioInputDevice)
            microphone.setVolume(settings.audioGain)
            microphone.setNoiseLevel(settings.audioNoise)
        }

        fun getTtsProvider() = activateExtension(ExtensionManager.ttsSelector, settings.ttsService, ttsProvider)

        private fun getAsrProvider(asrService: String?) =
            activateExtension(ExtensionManager.asrSelector, asrService ?: settings.asrService, asrProvider)

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
                        var ttsService: String = "",
                        var audioInputDevice: String = "",
                        var audioGain: Int = CustomMicrophone.DEFAULT_GAIN,
                        var audioNoise: Int = CustomMicrophone.DEFAULT_NOISE)
}
