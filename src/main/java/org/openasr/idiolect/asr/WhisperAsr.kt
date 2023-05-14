package org.openasr.idiolect.asr

import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.WhisperServerGrpcKt.WhisperServerCoroutineStub
import org.openasr.WhisperServerOuterClass.*

class WhisperAsr : AsrProvider {
    override fun displayName(): String = "Whisper"
    private val server: WhisperServerCoroutineStub
    private val emptyRequest = EmptyRequest.getDefaultInstance()

    init {
        val channel = ManagedChannelBuilder
            .forAddress("localhost", 1634)
            .usePlaintext()
            .build()

        this.server = WhisperServerCoroutineStub(channel)

        val model = LoadModelRequest.newBuilder()
            .setName("base")
            .setLanguage("en")
            .setDevice(ProcessingDevice.cpu)
//            .setDownloadRoot()
//            .setInMemory()
            .build()

        suspend {
            this.server.loadModel(model)
        }
    }

    override fun startRecognition(): Boolean {
        suspend {
            this.server.startRecognition(emptyRequest)
        }
        return true
    }

    override fun stopRecognition(): Boolean {
        suspend {
            this.server.stopRecognition(emptyRequest)
        }
        return true
    }

    override fun setGrammar(grammar: Array<String>) {
        suspend {
            val prompt = Prompt.newBuilder()
                .setText(grammar.joinToString(" "))
                .build()

            server.setPrompt(prompt)
        }
    }

    override fun waitForSpeech(): NlpRequest {
        val server = this.server
        return runBlocking {
            val response = server.waitForSpeech(emptyRequest)
            val alternatives = response.alternativesList
            return@runBlocking NlpRequest(alternatives)
        }
    }
}
