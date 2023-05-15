package org.openasr.idiolect.asr

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.WhisperServerGrpcKt.WhisperServerCoroutineStub
import org.openasr.WhisperServerOuterClass.*

class WhisperAsr : AsrProvider {
    override fun displayName(): String = "Whisper"
    private lateinit var server: WhisperServerCoroutineStub
    private val emptyRequest = EmptyRequest.getDefaultInstance()

    override fun activate() {
        super.activate()
        val channel = ManagedChannelBuilder
            .forAddress("localhost", 1634)
            .userAgent("idiolect")
            .usePlaintext()
            .build()

        this.server = WhisperServerCoroutineStub(channel)
    }

    override fun setModel(model: String) {
        runBlocking {
            val request = LoadModelRequest.newBuilder()
                .setName("base")
                .setLanguage("en")
//                .setDevice(ProcessingDevice.cpu)
//                .setDownloadRoot()
//                .setInMemory()
                .build()

            server.loadModel(request)
        }
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
