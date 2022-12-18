package org.openasr.idear.asr.vosk

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.jsoniter.JsonIterator
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.recognizer.CustomMicrophone
import org.vosk.Model
import org.vosk.Recognizer


class VoskAsr : AsrProvider {
    private val log = logger<VoskAsr>()
    private lateinit var recognizer: Recognizer
    private var modelPath: String? = defaultModel()
    private val alternatives = 0;

    override fun displayName() = "Vosk"

    override fun defaultModel() =
//      System.getProperty("user.home") + "/.vosk/vosk-model-small-en-gb-0.15" // Lightweight wideband model for Android and RPi
//    System.getProperty("user.home") + "/.vosk/vosk-model-en-us-0.22-lgraph"  // Big US English model with dynamic graph
      System.getProperty("user.home") + "/.vosk/vosk-model-en-us-daanzu-20200905-lgraph" // 129M Wideband model for dictation from Kaldi-active-grammar project with configurable graph


    /** @see https://alphacephei.com/vosk/models/model-list.json */
    override fun setModel(model: String) {
        if (model.isNotEmpty()) this.modelPath = model
    }

    private lateinit var microphone: CustomMicrophone

    override fun activate() {
        recognizer = Recognizer(Model(modelPath), 16000f)
//        recognizer.setMaxAlternatives(alternatives)

        microphone = service()
        microphone.open()
    }

    override fun deactivate() {
        microphone.close()
    }

    /**
     * Starts recognition process.
     */
    override fun startRecognition() {
        microphone.startRecording()
    }

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    override fun stopRecognition() {
        microphone.stopRecording()
    }

    /**
     * @param grammar eg: ["hello", "world", "[unk]"]
     */
    override fun setGrammar(grammar: Array<String>) {
        recognizer.reset()
        recognizer.setGrammar(grammar.joinToString("\",\"", "[\"", "\"]"))
    }

    /** Blocks until we recognise something from the user. Called from [ASRControlLoop.run] */
    override fun waitForUtterance(): String {
        var nbytes: Int
        val b = ByteArray(4096)

        while (microphone.stream.read(b).also { nbytes = it } >= 0) {
            if (recognizer.acceptWaveForm(b, nbytes)) {
                val text = parseResult(recognizer.result)
                if (text.isNotEmpty()) {
                    return text
                }
            }
        }

        return parseResult(recognizer.finalResult)
    }

    private fun parsePartialResult(json: String) = JsonIterator.deserialize(json).get("partial").toString()

    private fun parseResult(json: String) = JsonIterator.deserialize(json).get("text").toString()

    /** Use this instead of parseResult if alternatives > 0 */
    private fun parseAlternatives(json: String): List<String> {
        return JsonIterator.deserialize(json).get("alternatives", '*', "text")
                .asList().map { it.toString() }
    }
}
