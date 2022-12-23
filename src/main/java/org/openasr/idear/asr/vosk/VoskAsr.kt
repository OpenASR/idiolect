package org.openasr.idear.asr.vosk

import com.intellij.openapi.components.service
import com.jsoniter.JsonIterator
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.nlp.NlpRequest
import org.openasr.idear.recognizer.CustomMicrophone
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class VoskAsr : AsrProvider {
    private lateinit var recognizer: Recognizer
    private var modelPath: String? = defaultModel().also { println("Path to model: $it") }
    private val alternatives = 4

    override fun displayName() = "Vosk"

    override fun defaultModel(): String = unpackModelAndReturnPath("vosk-model-small-en-us-0.15.zip")

    private fun unpackModelAndReturnPath(model: String): String {
        val modelZip = javaClass.getResourceAsStream("/$model")
        val modelDir = System.getProperty("user.home") + "/.idear"
        val modelPath = "$modelDir/${model.substringBefore(".zip")}"
        val modelFile = File(modelPath)
        if (!modelFile.exists()) {
            println("Unzipping model to $modelDir")
            modelFile.parentFile.mkdirs()
            modelZip.use { zip ->
                ZipInputStream(zip).use { zis ->
                    var entry: ZipEntry? = zis.nextEntry
                    while (entry != null) {
                        val file = File(modelDir, entry.name)
                        if (entry.isDirectory) file.mkdirs()
                        else file.outputStream().use { fos -> zis.copyTo(fos) }
                        entry = zis.nextEntry
                    }
                }
            }
        }

        return modelPath
    }

    // https://alphacephei.com/vosk/models/model-list.json
    override fun setModel(model: String) {
        if (model.isNotEmpty()) this.modelPath = model
    }

    private lateinit var microphone: CustomMicrophone

    override fun activate() {
        recognizer = Recognizer(Model(modelPath), 16000f)
        recognizer.setMaxAlternatives(alternatives)

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
//        recognizer.setGrammar(grammar.joinToString("\",\"", "[\"", "\"]"))
    }

    /** Blocks until we recognise something from the user. Called from [ASRControlLoop.run] */
    override fun waitForSpeech(): NlpRequest? {
        var nbytes: Int
        val b = ByteArray(4096)

        while (microphone.stream.read(b).also { nbytes = it } >= 0) {
            if (recognizer.acceptWaveForm(b, nbytes)) {
                val nlpRequest = tryParseResult(recognizer.result)

                if (nlpRequest != null) {
                    return nlpRequest
                }
            }
        }

        return tryParseResult(recognizer.finalResult)
    }

    private fun parsePartialResult(json: String) = JsonIterator.deserialize(json).get("partial").toString()

    private fun tryParseResult(json: String): NlpRequest? {
        if (alternatives == 0) {
            val utterance = parseResult(json)
            if (utterance.isNotEmpty()) {
                return NlpRequest(listOf(utterance))
            }
        } else {
            val alternatives = parseAlternatives(json)
//            if (alternatives.isNotEmpty() && alternatives[0])
            return NlpRequest(alternatives)
        }

        return null
    }

    private fun parseResult(json: String) = JsonIterator.deserialize(json).get("text").toString()

    /** Use this instead of parseResult if alternatives > 0 */
    private fun parseAlternatives(json: String): List<String> {
        return JsonIterator.deserialize(json).get("alternatives", '*', "text")
                .asList().map { it.toString() }
    }
}
