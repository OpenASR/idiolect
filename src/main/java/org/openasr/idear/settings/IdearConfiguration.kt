package org.openasr.idear.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.options.Configurable
import org.openasr.idear.asr.ASRControlLoop
import org.openasr.idear.asr.awslex.LexASR
import org.openasr.idear.asr.cmusphinx.CMUSphinxASR
import org.openasr.idear.nlp.*
import org.openasr.idear.nlp.lex.LexNlp
import org.openasr.idear.recognizer.awslex.LexRecognizer
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.ASRServiceId
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.ASRServiceId.CMU_SPHINX
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.NLPServiceId
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.NLPServiceId.PATTERN
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.TTSServiceId
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.TTSServiceId.*
import org.openasr.idear.tts.*
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.ASRServiceId.AWS_LEX as LEX_ASR
import org.openasr.idear.settings.RecognitionSettingsForm.Companion.NLPServiceId.AWS_LEX as LEX_NLP

/*
 * @see http://corochann.com/intellij-plugin-development-introduction-applicationconfigurable-projectconfigurable-873.html
 */
@State(name = "IdearConfiguration", storages = [(Storage("recognition.xml"))])
class IdearConfiguration : Configurable, PersistentStateComponent<IdearConfiguration.Settings> {
    companion object {
        var settings = Settings()
        fun getASRSystem() =
                if (settings.asrService == LEX_ASR && settings.nlpService == LEX_NLP) LexRecognizer()
                else ASRControlLoop(getASRProvider(), getNLPProvider())

        // TODO: list voices by locale
        // TODO: allow user to select voice
        fun getTTSProvider() =
                when (settings.ttsService) {
                    MARY -> MaryTTS
                    AWS_POLLY -> PollyTTS
                }

        private fun getASRProvider() =
                when (settings.asrService) {
                    CMU_SPHINX -> CMUSphinxASR
                    LEX_ASR -> LexASR()
                }

        private fun getNLPProvider(/*listener: NlpResultListener*/) =
                when (settings.nlpService) {
                    PATTERN -> PatternBasedNlpProvider()
                    LEX_NLP -> LexNlp(IntellijNlpResultListener())
                }
    }

    override fun getState() = settings
    override fun loadState(state: Settings) {
        settings = state
    }

    private var gui = RecognitionSettingsForm()

    data class Settings(var asrService: ASRServiceId = CMU_SPHINX,
                        var nlpService: NLPServiceId = PATTERN,
                        var ttsService: TTSServiceId = MARY)

    override fun getDisplayName() = "Idear"

    override fun isModified() = gui.asrService != settings.asrService ||
            gui.ttsService != settings.ttsService ||
            gui.nlpService != settings.nlpService

    override fun createComponent() = RecognitionSettingsForm().apply { gui = this }.rootPanel

    override fun apply() {
        if (isModified) {
            settings.nlpService = gui.nlpService
            settings.ttsService = gui.ttsService
            settings.asrService = gui.asrService
        }
    }

    override fun reset() {
        gui.asrService = settings.asrService
        gui.ttsService = settings.ttsService
        gui.nlpService = settings.nlpService
    }
}
