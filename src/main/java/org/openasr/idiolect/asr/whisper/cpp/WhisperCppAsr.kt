package org.openasr.idiolect.asr.whisper.cpp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import io.github.ggerganov.whispercpp.WhisperCpp
import io.github.ggerganov.whispercpp.params.WhisperFullParams
import io.github.ggerganov.whispercpp.params.WhisperSamplingStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
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
    val SAMPLES_TO_PROCESS = 16 * 1024 * 4 //  CustomMicrophone.format.sampleRate.toInt() * 10 // 2048 * 16
    val MAX_SAMPLE_VALUE = 32767.0f

    private var grammar: Array<String>? = null
    private var whisperParams: WhisperFullParams? = null
    private val _speechResults = MutableSharedFlow<NlpRequest?>()
    val speechResults: SharedFlow<NlpRequest?> = _speechResults

    override fun displayName(): String = "whisper.cpp"

    companion object {
        private lateinit var instance: WhisperCppAsr
        private val whisper = WhisperCpp()

        /**
         * @param model - absolute path, or just the name (eg: "base", "base-en" or "base.en")
         */
        fun setModel(model: String) {
            if (model.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    WhisperCppConfig.saveModelPath(model)

                    whisper.initContext(model)

                    activate()

                    val messageBus = ApplicationManager.getApplication()!!.messageBus
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
        whisperParams.temperature_inc = 0f
        //  >= -0.3 : good
        //     -0.4 : close
        //  >  -0.8 : clear speech mis-recognised or
        //  <= -0.8 : mis-pronounced
        //  <  -1.0 : is mumbled/hard to hear
        whisperParams.logprob_thold = -0.8f     // default -1.0
        // ignore no_speech_prob >= 0.75
        whisperParams.no_speech_thold = 0.75f   // default 0.6
//        whisperParams.setBestOf(4)              // for greedy
//        whisperParams.setBeamSizeAndPatience(40, -1.0f)

        this.whisperParams = whisperParams
    }

    fun finalize() {
        whisper.close()
    }

    /**
     * @param model - absolute path, or just the name (eg: "base", "base-en" or "base.en")
     */
    override suspend fun setModel(model: String) {
        whisper.initContext(model)
    }

    override fun setGrammar(grammar: Array<String>) {
//        Whisper.
    }

    override fun startRecognition(): Boolean {
        val started = super.startRecognition()   // start the mic

        var nbytes: Int
        val audioData = ByteArray(SAMPLES_TO_PROCESS shl 1)

        CoroutineScope(Dispatchers.IO).launch {
            while (microphone.stream.read(audioData).also { nbytes = it } > 0 && listening) {
                val result = processSpeech(audioData, nbytes) // Blocking call to process speech
                if (result != null) {
                    _speechResults.emit(result)
                }
            }
            _speechResults.emit(null)
        }

        return started
    }

//    override fun stopRecognition(): Boolean {
//        val stopped = super.stopRecognition()    // stop the mic
//        return stopped
//    }

    internal fun processSpeech(audioData: ByteArray, nbytes: Int): NlpRequest? {
        val floats = FloatArray(nbytes / 2)
        val stopWords = AsrProvider.stopWords(grammar)

        for ((j, i) in (0 until nbytes step 2).withIndex()) {
            val sample = (((audioData[i + 1].toInt() shl 8) + (audioData[i].toInt() and 0xff)) / MAX_SAMPLE_VALUE).coerceIn(-1f..1f)

            floats[j] = sample
        }

        var result = whisper.fullTranscribe(whisperParams, floats)

        result = result.replace(",", "")
            .replace(Regex("[.?!]$"), "")
            .lowercase()

        result = AsrProvider.removeStopWords(result, stopWords)

        return if (result.isEmpty()) null else NlpRequest(listOf(result))
    }

    override fun waitForSpeech(): NlpRequest? {
        return runBlocking {
            speechResults.first()
        }
    }
}
