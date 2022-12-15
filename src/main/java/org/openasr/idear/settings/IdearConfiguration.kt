package org.openasr.idear.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.extensions.ExtensionPointName
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
        private val AsrSystemEp: ExtensionPointName<AsrSystem> = ExtensionPointName.create("org.openasr.idear.asrSystem")
        private val AsrEp: ExtensionPointName<AsrProvider> = ExtensionPointName.create("org.openasr.idear.asrProvider")
        private val TtsEp: ExtensionPointName<TtsProvider> = ExtensionPointName.create("org.openasr.idear.ttsProvider")
        private val NlpEp: ExtensionPointName<NlpProvider> = ExtensionPointName.create("org.openasr.idear.nlpProvider")

        private val ttsProvider = AtomicReference<TtsProvider>()
        private val nlpProvider = AtomicReference<NlpProvider>()
        private val asrProvider = AtomicReference<AsrProvider>()

        private var settings = Settings()

        /** Called by AsrService */
        fun getASRSystem(): AsrSystem {
            val asrProvider = getAsrProvider()
            val nlpProvdier = getNlpProvider()

            var extension = AsrSystemEp.extensionList.firstOrNull { e -> e.supportsAsrAndNlp(asrProvider, nlpProvdier) }
            if (extension == null) {
                extension = AsrSystemEp.extensionList.first()
            }

            return extension!!
        }

        // TODO: list voices by locale
        // TODO: allow user to select voice
        private fun getTtsProvider() = getExtension(TtsEp.extensionList, settings.ttsService)

        private fun getAsrProvider() = getExtension(AsrEp.extensionList, settings.asrService)

        private fun getNlpProvider() = getExtension(NlpEp.extensionList, settings.nlpService)

        private fun <T : ConfigurableExtension> getExtension(extensions: List<T>, displayName: String): T {
            var extension = extensions.firstOrNull { e -> e.displayName() == displayName }
            if (extension == null) {
                extension = extensions.first()
            }

            extension!!.activate()
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

    override fun isModified() = gui.getAsrProvider().displayName() != settings.asrService ||
            gui.asrModelPath != settings.asrModelPath ||
            gui.ttsService != settings.ttsService ||
            gui.nlpService != settings.nlpService

    override fun createComponent() = RecognitionSettingsForm().apply { gui = this }.rootPanel

    override fun apply() {
        if (isModified) {
            settings.nlpService = gui.nlpService
            settings.ttsService = gui.ttsService
            settings.asrService = gui.asrService
            settings.asrModelPath = gui.asrModelPath
        }
    }

    override fun reset() {
        gui.asrModelPath = settings.asrModelPath
        gui.asrService = settings.asrService
        gui.ttsService = settings.ttsService
        gui.nlpService = settings.nlpService
    }
}
