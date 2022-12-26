package org.openasr.idear.asr.vosk

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.jsoniter.JsonIterator
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.NlpRequest
import org.openasr.idear.recognizer.CustomMicrophone
import org.openasr.idear.settings.IdearConfiguration
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

//    /**
//     * @see https://alphacephei.com/vosk/models/model-list.json
//     * check "type" field. "small" and "big-lgraph" support grammar, "big" doesn't
//     */
//    override fun defaultModel() =
////      System.getProperty("user.home") + "/.vosk/vosk-model-small-en-gb-0.15" // Lightweight wideband model for Android and RPi
////    System.getProperty("user.home") + "/.vosk/vosk-model-en-us-0.22-lgraph"  // Big US English model with dynamic graph
//      System.getProperty("user.home") + "/.vosk/vosk-model-en-us-daanzu-20200905-lgraph" // 129M Wideband model for dictation from Kaldi-active-grammar project with configurable graph
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
        if (model.isNotEmpty()) {
            this.modelPath = model

            recognizer = Recognizer(Model(modelPath), 16000f)
            recognizer.setMaxAlternatives(alternatives)
        }
    }

    private lateinit var microphone: CustomMicrophone

    override fun activate() {
        if (modelPath.isNullOrEmpty()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("Idear")
                .createNotification("Speech model not configured",
                    """
                    <p>Download and configure the path to your Vosk speech model.<p>
                    <p><a href="https://alphacephei.com/vosk/models">https://alphacephei.com/vosk/models</a></p>
                """.trimIndent(), NotificationType.INFORMATION)
                .addAction(NotificationAction.create("Edit Configuration") { _ ->
                    ShowSettingsUtil.getInstance().showSettingsDialog(null, IdearConfiguration::class.java)
                })
                .notify(null)
        }

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
