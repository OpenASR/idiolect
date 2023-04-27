package org.openasr.idiolect.asr.vosk

import com.google.gson.JsonParser.*
import com.intellij.notification.*
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.util.messages.MessageBus
import org.openasr.idiolect.asr.*
import org.openasr.idiolect.asr.AsrSystemStateListener.Companion.ASR_STATE_TOPIC
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.recognizer.CustomMicrophone
import org.openasr.idiolect.settings.IdiolectConfig
import org.vosk.*
import java.io.*
import java.net.URI
import java.net.http.*
import java.util.zip.*
import javax.sound.sampled.AudioInputStream


class VoskAsr : AsrProvider {
    private lateinit var microphone: CustomMicrophone
    private var grammar: Array<String>? = null

    override fun displayName() = "Vosk"

    companion object {
        private lateinit var instance: VoskAsr
        private lateinit var messageBus: MessageBus
        private val httpClient = HttpClient.newBuilder().build()
        lateinit var recognizer: Recognizer

        private val alternatives = 4
        private const val defaultModelURL = "https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip"

        init {
            System.setProperty("jna.nounpack", "false")
            System.setProperty("jna.noclasspath", "false")
        }

        fun listModels(): List<ModelInfo> {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://alphacephei.com/vosk/models/model-list.json"))
                .build()
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

            return parseModels(response.body())
        }

        private fun parseModels(json: String): List<ModelInfo> =
            parseString(json).asJsonArray
            .map { it.asJsonObject }
            .filter {
                // "small" and "big-lgraph" support grammar, "big" doesn't
//                    it.get("type").asString != "big"
                    it.get("obsolete").asString != "true"
                        || it.get("version").asString.startsWith("daanzu-20200905")
            }
            .map {
                ModelInfo(
                    it.get("lang").asString,
                    it.get("lang_text").asString,
                    it.get("name").asString,
                    it.get("url").asString,
                    it.get("size").asInt,
                    it.get("size_text").asString,
                    it.get("type").asString
                )
            }
            .sortedWith(ModelComparator())

        internal fun installModel(url: String): String {
            messageBus.syncPublisher(ASR_STATE_TOPIC).onAsrStatus("Installing model...")

            val modelPath = pathForModelUrl(url)
            if (!File(modelPath).exists()) {
                val modelZip = downloadModel(url)
                unpackModel(modelPath, modelZip)
            }

            messageBus.syncPublisher(ASR_STATE_TOPIC).onAsrStatus("Model installed")
            return modelPath
        }

        private fun downloadModel(url: String): InputStream {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build()
            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())
            return response.body()
        }

        private fun unpackModel(modelPath: String, modelZip: InputStream) {
            val modelDir = File(modelPath).parentFile
            modelDir.mkdirs()

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

        internal fun pathForModelUrl(url: String): String {
            val modelName = url.substringAfterLast('/')
            val modelDir = IdiolectConfig.idiolectHomePath
            return "$modelDir/${modelName.substringBefore(".zip")}"
        }

        fun setModel(model: String) {
            if (model.isNotEmpty()) {
                VoskConfig.saveModelPath(model)

                initialiseRecogniserForModel(model)

                messageBus.syncPublisher(ASR_STATE_TOPIC).onAsrReady("Model has been applied")
            }
        }

        fun initialiseRecogniserForModel(model: String) {
            recognizer = Recognizer(Model(model), 16000f)
            recognizer.setMaxAlternatives(alternatives)
        }

        fun activate() {
            instance.activate()
        }
    }

    init {
        instance = this
    }

    override fun setModel(model: String) = VoskAsr.setModel(model)

    override fun activate() {
        if (VoskConfig.settings.modelPath.isEmpty()) {
            showNotificationForModel()

            throw ModelNotAvailableException()
        }

        messageBus = ApplicationManager.getApplication()!!.messageBus

        setModel(VoskConfig.settings.modelPath)

        microphone = service()
        microphone.open()
    }

    override fun deactivate() = microphone.close()

    /**
     * Starts recognition process.
     */
    override fun startRecognition() = microphone.startRecording()

    /**
     * Stops recognition process.
     * Recognition process is paused until the next call to startRecognition.
     */
    override fun stopRecognition() = microphone.stopRecording()

    /**
     * @param grammar eg: ["hello", "world", "[unk]"]
     */
    override fun setGrammar(grammar: Array<String>) {
        recognizer//.apply { setGrammar(grammar.joinToString("\",\"", "[\"", "\"]")) }
            .reset()
    }

    /** Blocks until we recognise something from the user. Called from [ASRControlLoop.run] */
    override fun waitForSpeech(): NlpRequest {
        return processAudioInputStream(microphone.stream)
    }

    public fun processAudioInputStream(stream: AudioInputStream): NlpRequest {
        var nbytes: Int
        val b = ByteArray(4096)

        val stopWords = AsrProvider.stopWords(grammar)

        while (stream.read(b).also { nbytes = it } >= 0) {
            if (recognizer.acceptWaveForm(b, nbytes)) {
                val result = tryParseResult(recognizer.result, stopWords)
                if (result.alternatives.isNotEmpty()) return result
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

    private fun showNotificationForModel() {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Idiolect")
            .createNotification("Speech model not configured",
                """
                    <p>Download and configure the path to your Vosk speech model.<p>
                    <p><a href="https://alphacephei.com/vosk/models">https://alphacephei.com/vosk/models</a></p>
                """.trimIndent(), INFORMATION)
            .addAction(NotificationAction.create("Download Default Model") { _ ->
                val modelPath = installModel(defaultModelURL)
                setModel(modelPath)
            })
            .addAction(NotificationAction.create("Edit Configuration") { _ ->
                ShowSettingsUtil.getInstance().showSettingsDialog(null, VoskConfigurable::class.java)
            })
            .notify(null)
    }
}
