package org.openasr.idear.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import org.openasr.idear.asr.ASRControlLoop
import org.openasr.idear.asr.ASRService
import org.openasr.idear.asr.ASRSystem
import org.openasr.idear.asr.awslex.LexASR
import org.openasr.idear.asr.cmusphinx.CMUSphinxASR
import org.openasr.idear.nlp.IntellijNlpResultListener
import org.openasr.idear.nlp.NlpResultListener
import org.openasr.idear.nlp.PatternBasedNlpProvider
import org.openasr.idear.nlp.lex.LexNlp
import org.openasr.idear.recognizer.awslex.LexRecognizer
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.ASRServiceId
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.ASRServiceId.AWS_LEX as LEX_ASR
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.ASRServiceId.CMU_SPHINX
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.NLPServiceId
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.NLPServiceId.AWS_LEX as LEX_NLP
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.NLPServiceId.PATTERN
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.TTSServiceId
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.TTSServiceId.AWS_POLLY
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.TTSServiceId.MARY
import org.openasr.idear.tts.MaryTTS
import org.openasr.idear.tts.PollyTTS

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

    data class Settings(var asrService: ASRServiceId = CMU_SPHINX,
                        var nlpService: NLPServiceId = PATTERN,
                        var ttsService: TTSServiceId = MARY)

    fun getASRSystem(): ASRSystem {
        if (settings.asrService == LEX_ASR && settings.nlpService == LEX_NLP) {
            return LexRecognizer()
        } else {
            return ASRControlLoop(getASRProvider(), getNLPProvider())
        }
    }

    fun getASRProvider() =
            when (settings.asrService) {
                CMU_SPHINX -> CMUSphinxASR()
                LEX_ASR -> LexASR()
            }

    fun getNLPProvider(/*listener: NlpResultListener*/) =
            when (settings.nlpService) {
                PATTERN -> PatternBasedNlpProvider()
                LEX_NLP -> LexNlp(IntellijNlpResultListener())
            }

    // TODO: list voices by locale
    // TODO: allow user to select voice
    fun getTTSProvider() =
            when (settings.ttsService) {
                MARY -> MaryTTS()
                AWS_POLLY -> PollyTTS()
            }

    override fun getDisplayName() = "Recognition"

    override fun isModified() =
            gui.asrService != settings.asrService ||
                    gui.ttsService != settings.ttsService

    override fun createComponent() = RecognitionSettingsForm().apply { gui = this }.rootPanel

    override fun apply() {
        val changed = (settings.ttsService != gui.ttsService || settings.asrService != gui.asrService)
        if (changed) {
            settings.ttsService = gui.ttsService
            settings.asrService = gui.asrService
            ASRService.setASRSystem(getASRSystem())
        }
    }

    override fun reset() {
        gui.asrService = settings.asrService
        gui.ttsService = settings.ttsService
    }
}
