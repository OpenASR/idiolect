package org.openasr.idiolect.asr.whisper.cpp

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.openasr.idiolect.asr.AsrProvider
import org.openasr.idiolect.asr.AsrSystemStateListener
import org.openasr.idiolect.asr.offline.OfflineAsr
import org.openasr.idiolect.asr.vosk.VoskAsr
import org.openasr.idiolect.asr.whisper.cpp.params.WhisperFullParams
import org.openasr.idiolect.asr.whisper.cpp.params.WhisperSamplingStrategy
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppConfig
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppConfigurable
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppModelManager
import org.openasr.idiolect.nlp.NlpRequest

class WhisperCppAsr : OfflineAsr<WhisperCppConfigurable>(WhisperCppModelManager) {
    private val log = logger<VoskAsr>()

    private lateinit var whiperParams: WhisperJavaParams
    private var grammar: Array<String>? = null

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

        whiperParams = whisper.getDefaultJavaParams(WhisperSamplingStrategy.WHISPER_SAMPLING_BEAM_SEARCH)
//        whiperParams = whisper.getDefaultParams(WhisperSamplingStrategy.WHISPER_SAMPLING_BEAM_SEARCH)
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

    val SAMPLES_TO_PROCESS = 2048
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
                val result = whisper.fullTranscribe(whiperParams, floats)
                println("results: $result")

//                AsrProvider.removeStopWords(it, stopWords)
                return@runBlocking NlpRequest(listOf("TODO"))
            }

            return nlpRequest
        }

        return null
    }
}
