package org.openasr.idiolect.asr.whisper.server

import com.intellij.openapi.application.ApplicationManager
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.WhisperServerGrpcKt.WhisperServerCoroutineStub
import org.openasr.WhisperServerOuterClass.*
import org.openasr.idiolect.asr.AsrSystemStateListener
import org.openasr.idiolect.asr.offline.OfflineAsr
import org.openasr.idiolect.asr.whisper.cpp.WhisperCppAsr
import org.openasr.idiolect.asr.whisper.server.settings.WhisperServerConfig
import org.openasr.idiolect.asr.whisper.server.settings.WhisperServerConfigurable
import org.openasr.idiolect.asr.whisper.server.settings.WhisperServerModelManager

class WhisperServerAsr : OfflineAsr<WhisperServerConfigurable>(WhisperServerModelManager) {
    override fun displayName(): String = "whisper_server"
    private lateinit var server: WhisperServerCoroutineStub
    private val emptyRequest = EmptyRequest.getDefaultInstance()

    companion object {
        private lateinit var instance: WhisperServerAsr
        private val messageBus = ApplicationManager.getApplication()!!.messageBus

        fun setModel(model: String) {
            if (model.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    WhisperServerConfig.saveModelPath(model)

                    val parts = model.split(".")
                    val name = parts[0]
                    val language = if (parts.size == 2) parts[1] else null

                    val request = LoadModelRequest.newBuilder()
                        .setName(name)
                        .setLanguage(language)
//                        .setDevice(ProcessingDevice.cpu)
//                        .setDownloadRoot()
//                        .setInMemory()
                        .build()

                    instance.server.loadModel(request)

                    WhisperCppAsr.activate()

                    messageBus.syncPublisher(AsrSystemStateListener.ASR_STATE_TOPIC).onAsrReady("Speech model has been applied")
                }
            }
        }

        fun activate() {
            instance.activate()
        }
    }

    override fun activate() {
        super.activate()
        val channel = ManagedChannelBuilder
            .forAddress("localhost", 1634)
            .userAgent("idiolect")
            .usePlaintext()
            .build()

        server = WhisperServerCoroutineStub(channel)
    }

    override suspend fun setModel(model: String) {
        WhisperServerAsr.setModel(model)
    }

    override fun deactivate() {
//        runBlocking {
//            server.stop
//        }
        stopRecognition()
//        server.
    }

    override fun startRecognition(): Boolean {
        runBlocking {
            server.startRecognition(emptyRequest)
        }
        return true
    }

    override fun stopRecognition(): Boolean {
        runBlocking {
            server.stopRecognition(emptyRequest)
        }
        return true
    }

    override fun setGrammar(grammar: Array<String>) {
        return runBlocking {
            val prompt = Prompt.newBuilder()
                .setText(grammar.joinToString(" "))
                .build()

            server.setPrompt(prompt)
        }
    }

    override fun waitForSpeech(): NlpRequest {
        return runBlocking {
            val response = server.waitForSpeech(emptyRequest)
            val alternatives = response.alternativesList.map { alt ->
                alt.replace(",", "")
                    .replace(Regex("[.?!]$"), "")
                    .lowercase()
            }
            return@runBlocking NlpRequest(alternatives)
        }
    }
}
