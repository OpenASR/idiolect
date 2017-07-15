package org.openasr.idear.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import org.openasr.idear.asr.ASRProvider
import org.openasr.idear.asr.awslex.LexASR
import org.openasr.idear.asr.cmusphinx.CMUSphinxASR
import org.openasr.idear.settings.ASRServiceId.AWS_LEX
import org.openasr.idear.settings.ASRServiceId.CMU_SPHINX
import org.openasr.idear.settings.TTSServiceId.AWS_POLLY
import org.openasr.idear.settings.TTSServiceId.MARY
import org.openasr.idear.tts.MaryTTS
import org.openasr.idear.tts.PollyTTS
import org.openasr.idear.tts.TTSProvider

/**
 * @see http://corochann.com/intellij-plugin-development-introduction-applicationconfigurable-projectconfigurable-873.html
 */
@State(name = "IdearConfiguration",
    storages = arrayOf(Storage("recognition.xml")))
object IdearConfiguration : Configurable, PersistentStateComponent<IdearConfiguration.Settings> {
    override fun getState() = settings
    override fun loadState(state: Settings) {
        this.settings = state
    }

    private var settings = Settings()
    private lateinit var gui: RecognitionSettingsForm

    data class Settings(
        var asrService: ASRServiceId = CMU_SPHINX,
        var ttsService: TTSServiceId = MARY
    )

    fun getASRProvider(): ASRProvider {
        return when (settings.asrService) {
            CMU_SPHINX -> CMUSphinxASR()
            AWS_LEX -> LexASR()
        }
    }

    // TODO: list voices by locale
    // TODO: allow user to select voice
    fun getTTSProvider(): TTSProvider {
        return when (settings.ttsService) {
            MARY -> MaryTTS()
            AWS_POLLY -> PollyTTS()
        }
    }

    override fun getDisplayName() = "Recognition"

    override fun isModified() =
        gui.asrService != settings.asrService ||
            gui.ttsService != settings.ttsService

    override fun createComponent() =
        RecognitionSettingsForm().apply { gui = this }.rootPanel

    override fun apply() {
        settings.ttsService = gui.ttsService
        settings.asrService = gui.asrService
    }

    override fun reset() {
        gui.asrService = settings.asrService
        gui.ttsService = settings.ttsService
    }
}
