package org.openasr.idear.asr.vosk

import com.google.gson.JsonParser.*
import com.intellij.openapi.components.service
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType.INFORMATION
import com.intellij.notification.NotificationGroupManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.ShowSettingsUtil
import org.openasr.idear.asr.AsrProvider
import org.openasr.idear.asr.AsrSystemStateListener.Companion.ASR_STATE_TOPIC
import org.openasr.idear.asr.ModelNotAvailableException
import org.openasr.idear.nlp.NlpRequest
import org.openasr.idear.recognizer.CustomMicrophone
import org.openasr.idear.settings.IdearConfiguration
import java.io.File
import org.vosk.Model
import org.vosk.Recognizer
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class VoskAsr : AsrProvider {
    private lateinit var microphone: CustomMicrophone

    override fun displayName() = "Vosk"

    companion object {
        private val messageBus = ApplicationManager.getApplication()?.messageBus
        private val httpClient = HttpClient.newBuilder().build()
        private lateinit var recognizer: Recognizer

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

        internal fun installModel(url: String) {
            messageBus!!.syncPublisher(ASR_STATE_TOPIC).onAsrStatus("Installing model...")

            val modelPath = pathForModelUrl(url)
            if (!File(modelPath).exists()) {
                val modelZip = downloadModel(url)
                unpackModel(modelPath, modelZip)
            }
            setModel(modelPath)

            messageBus.syncPublisher(ASR_STATE_TOPIC).onAsrReady("Model has been installed")
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
            val modelDir = IdearConfiguration.idearHomePath
            return "$modelDir/${modelName.substringBefore(".zip")}"
        }

        fun setModel(model: String) {
            if (model.isNotEmpty()) {
                VoskConfiguration.saveModelPath(model)

                recognizer = Recognizer(Model(model), 16000f)
                recognizer.setMaxAlternatives(alternatives)
            }
        }

        lateinit var instance: VoskAsr
    }

    init {
        instance = this
    }

    override fun setModel(model: String) = VoskAsr.setModel(model)

    val pathToPropertiesFile by lazy {
        File(System.getProperty("user.home") + "/.idear")
            .apply { if (!exists()) createNewFile() }
            .absolutePath
    }

    override fun activate() {
        if (VoskConfiguration.settings.modelPath.isEmpty()) {
            showNotificationForModel()

            throw ModelNotAvailableException()
        } else {
            setModel(VoskConfiguration.settings.modelPath)
        }

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
    override fun setGrammar(grammar: Array<String>) =
        recognizer//.apply { setGrammar(grammar.joinToString("\",\"", "[\"", "\"]")) }
            .reset()

    /** Blocks until we recognise something from the user. Called from [ASRControlLoop.run] */
    override fun waitForSpeech(): NlpRequest {
        var nbytes: Int
        val b = ByteArray(4096)

        while (microphone.stream.read(b).also { nbytes = it } >= 0) {
            if (recognizer.acceptWaveForm(b, nbytes)) {
                return tryParseResult(recognizer.result)
            }
        }

        return tryParseResult(recognizer.finalResult)
    }

    private fun tryParseResult(json: String): NlpRequest = NlpRequest(parseVosk(json))

    /** Use this instead of parseResult if alternatives > 0 */
    private fun parseVosk(json: String): List<String> =
        parseString(json).asJsonObject.let { jo ->
            jo.get("alternatives").run {
                if (isJsonNull) listOf(jo.get("text").toString())
                else asJsonArray.map { it.asJsonObject.get("text").asString }
            }
        }

    private fun showNotificationForModel() {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Idear")
            .createNotification("Speech model not configured",
                """
                    <p>Download and configure the path to your Vosk speech model.<p>
                    <p><a href="https://alphacephei.com/vosk/models">https://alphacephei.com/vosk/models</a></p>
                """.trimIndent(), INFORMATION)
            .addAction(NotificationAction.create("Download Default Model") { _ ->
                installModel(defaultModelURL)
            })
            .addAction(NotificationAction.create("Edit Configuration") { _ ->
                ShowSettingsUtil.getInstance().showSettingsDialog(null, VoskConfiguration::class.java)
            })
            .notify(null)
    }
}
