package org.openasr.idiolect.asr.whisper.cpp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import io.github.ggerganov.whispercpp.WhisperCpp
import io.github.ggerganov.whispercpp.params.WhisperFullParams
import io.github.ggerganov.whispercpp.params.WhisperSamplingStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.openasr.idiolect.asr.AsrProvider
import org.openasr.idiolect.asr.AsrSystemStateListener
import org.openasr.idiolect.asr.offline.OfflineAsr
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppConfig
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppConfigurable
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppModelManager
import org.openasr.idiolect.nlp.NlpRequest

class WhisperCppAsr : OfflineAsr<WhisperCppConfigurable>(WhisperCppModelManager) {
    private val log = logger<WhisperCppAsr>()
    private var grammar: Array<String>? = null
    private var whisperParams: WhisperFullParams? = null

    override fun displayName(): String = "whisper.cpp"

    companion object {
        private lateinit var instance: WhisperCppAsr
        private val messageBus = ApplicationManager.getApplication()!!.messageBus
        private val whisper = WhisperCpp()

        fun setModel(model: String) {
            if (model.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    WhisperCppConfig.saveModelPath(model)

                    whisper.initContext(model)

                    activate()

                    messageBus.syncPublisher(AsrSystemStateListener.ASR_STATE_TOPIC)
                        .onAsrReady("Speech model has been applied")
                }
            }
        }

        fun activate() {
            instance.activate()
        }
    }

    override fun activate() {
        super.activate()

//        val whisperParams = whisper.getFullDefaultParams(WhisperSamplingStrategy.WHISPER_SAMPLING_BEAM_SEARCH)
        val whisperParams = whisper.getFullDefaultParams(WhisperSamplingStrategy.WHISPER_SAMPLING_GREEDY)
        whisperParams.printProgress(false)
        whisperParams.suppressNonSpeechTokens(true)
//        whisperParams.no_speech_thold = 0.75f   // default 0.6
        whisperParams.setBestOf(4)
//        whisperParams.setBeamSizeAndPatience(4, -1.0f)

        this.whisperParams = whisperParams
    }

    fun finalize() {
        whisper.close()
    }

    override suspend fun setModel(model: String) {
        whisper.initContext(model)
    }

    override fun setGrammar(grammar: Array<String>) {
//        Whisper.
    }

    val SAMPLES_TO_PROCESS = 2048 * 8
    val MAX_SAMPLE_VALUE = 32767.0f

    override fun waitForSpeech(): NlpRequest? {
        var nbytes: Int
        val b = ByteArray(SAMPLES_TO_PROCESS shl 1)
        val floats = FloatArray(SAMPLES_TO_PROCESS)

        val stopWords = AsrProvider.stopWords(grammar)

        while (microphone.stream.read(b).also { nbytes = it } > 0 && listening) {
//            var j = 0
//            for (i in 0 until nbytes step 2) {
            for ((j, i) in (0 until nbytes step 2).withIndex()) {
                val sample = (((b[i + 1].toInt() shl 8) + (b[i].toInt() and 0xff)) / MAX_SAMPLE_VALUE).coerceIn(-1f..1f)

                floats[j] = sample
            }

            val nlpRequest = runBlocking {
                var result = whisper.fullTranscribe(whisperParams, floats)
//                println("results: '$result'")
                result = result.replace(",", "")
                    .replace(Regex("[.?!]$"), "")
                    .lowercase()

                result = AsrProvider.removeStopWords(result, stopWords)

                return@runBlocking if (result.isEmpty()) null else NlpRequest(listOf(result))
            }

            if (nlpRequest != null) {
                return nlpRequest
            }
        }

        return null
    }
}
