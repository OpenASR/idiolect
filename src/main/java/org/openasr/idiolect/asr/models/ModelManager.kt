package org.openasr.idiolect.asr.models

import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ShowSettingsUtil
import org.openasr.idiolect.asr.AsrSystemStateListener
import org.openasr.idiolect.asr.ModelNotAvailableException
import org.openasr.idiolect.settings.IdiolectConfig
import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.function.Consumer
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

abstract class ModelManager<C : Configurable>(
    private val defaultModelURL: String,
    internal val modelsPageUrl: String,
    private val configurable: Class<C>)
{
    protected val httpClient: HttpClient = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()
    private val messageBus = ApplicationManager.getApplication()!!.messageBus

    abstract fun configuredModelPath(): String

    abstract fun listModels(): List<ModelInfo>

    fun initialiseModel(notificationContent: String, setModel: Consumer<String>) {
        if (configuredModelPath().isEmpty()) {
            showNotificationForModel(
                notificationContent,
                configurable,
            ) { setModel.accept(it) }

            throw ModelNotAvailableException()
        } else {
            setModel.accept(configuredModelPath())
        }
    }

    private fun downloadModel(url: String): InputStream {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream())
        return response.body()
    }

    internal fun installModel(url: String): String {
        messageBus.syncPublisher(AsrSystemStateListener.ASR_STATE_TOPIC).onAsrStatus("Installing model...")

        val modelPath = pathForModelUrl(url)
        if (!File(modelPath).exists()) {
            val modelZip = downloadModel(url)
            unpackModel(modelPath, modelZip)
        }

        messageBus.syncPublisher(AsrSystemStateListener.ASR_STATE_TOPIC).onAsrStatus("Model installed")
        return modelPath
    }

    internal fun pathForModelUrl(url: String): String {
        val modelName = url.substringAfterLast('/')
        val modelDir = IdiolectConfig.idiolectHomePath
        return "$modelDir/${modelName.substringBefore(".zip")}"
    }

    protected open fun unpackModel(modelPath: String, modelZip: InputStream) {
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

    private fun <C : Configurable> showNotificationForModel(content: String, configurable: Class<C>, onModelProvided: Consumer<String>) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Idiolect")
            .createNotification("Speech model not configured", content, NotificationType.INFORMATION
            )
            .addAction(NotificationAction.create("Download Default Model") { _ ->
                val modelPath = installModel(defaultModelURL)
                onModelProvided.accept(modelPath)
            })
            .addAction(NotificationAction.create("Edit Configuration") { _ ->
                ShowSettingsUtil.getInstance().showSettingsDialog(null, configurable)
            })
            .notify(null)
    }
}

