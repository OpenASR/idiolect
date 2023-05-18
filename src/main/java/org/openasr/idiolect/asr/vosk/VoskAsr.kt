package org.openasr.idiolect.asr.vosk

import com.google.gson.JsonParser.*
import com.intellij.notification.*
import com.intellij.openapi.application.ApplicationManager
import org.openasr.idiolect.asr.*
import org.openasr.idiolect.asr.AsrSystemStateListener.Companion.ASR_STATE_TOPIC
import org.openasr.idiolect.nlp.NlpRequest
import org.vosk.*
import java.io.*
import java.net.http.*
import java.util.zip.*


class VoskAsr : OfflineAsr<VoskConfigurable>(VoskModelManager) {
    override fun displayName() = "Vosk"
    private var grammar: Array<String>? = null

    companion object {
        private lateinit var instance: VoskAsr
        private val messageBus = ApplicationManager.getApplication()!!.messageBus

        lateinit var recognizer: Recognizer

        private val alternatives = 4

        init {
            System.setProperty("jna.nounpack", "false")
            System.setProperty("jna.noclasspath", "false")
        }

        fun setModel(model: String) {
            if (model.isNotEmpty()) {
                VoskConfig.saveModelPath(model)

                recognizer = Recognizer(Model(model), 16000f)
                recognizer.setMaxAlternatives(alternatives)

                messageBus.syncPublisher(ASR_STATE_TOPIC).onAsrReady("Speech model has been applied")
            }
        }

        fun activate() {
            instance.activate()
        }
    }

    init {
        instance = this
    }

    override fun setModel(model: String) = VoskAsr.setModel(model)

    /**
     * @param grammar eg: ["hello", "world", "[unk]"]
     */
    override fun setGrammar(grammar: Array<String>) {
        recognizer//.apply { setGrammar(grammar.joinToString("\",\"", "[\"", "\"]")) }
            .reset()
    }

    /** Blocks until we recognise something from the user. Called from [AsrControlLoop.run] */
    override fun waitForSpeech(): NlpRequest {
        var nbytes: Int
        val b = ByteArray(4096)

        val stopWords = AsrProvider.stopWords(grammar)

        while (microphone.stream.read(b).also { nbytes = it } > 0 && listening) {
//            log.debug("We have $nbytes bytes for Vosk...")
            if (recognizer.acceptWaveForm(b, nbytes)) {
//                log.debug("...and Vosk has a recognition for us: ${recognizer.result}")
                val nlpRequest = tryParseResult(recognizer.result, stopWords)
//                log.debug("parsed NlpRequest: $nlpRequest")
                if (nlpRequest.alternatives.isNotEmpty()) return nlpRequest
            }
        }

        return tryParseResult(recognizer.finalResult, stopWords)
    }

    private fun tryParseResult(json: String, stopWords: List<String>): NlpRequest = NlpRequest(parseVosk(json, stopWords))


    /** Use this instead of parseResult if alternatives > 0 */
    private fun parseVosk(json: String, stopWords: List<String>): List<String> =
        parseString(json).asJsonObject.let { jo ->
            jo.get("alternatives").run {
                if (isJsonNull) listOf(jo.get("text").toString())
                else asJsonArray.map { it.asJsonObject.get("text").asString }
            }
        }.filter {
            // I see a LOT of "yeah" | "i" | "ah".
            // - "yeah" could be a valid response to a question, if it is explicitly allowed in grammar
            // - "i" could be in grammar as it helps to make utterances more natural, but probably not by itself
            it.isNotEmpty() && !stopWords.contains(it) && it != "i"
        }.map {
            AsrProvider.removeStopWords(it, stopWords)
        }
}
