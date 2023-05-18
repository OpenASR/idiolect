package org.openasr.idiolect.asr.vosk

import com.google.gson.JsonParser
import org.openasr.idiolect.asr.models.ModelInfo
import org.openasr.idiolect.asr.models.ModelManager
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

object VoskModelManager : ModelManager<VoskConfigurable>(
    "https://alphacephei.com/vosk/models/vosk-model-small-en-us-0.15.zip",
    "https://alphacephei.com/vosk/models",
    VoskConfigurable::class.java
) {
    override fun configuredModelPath() = VoskConfig.settings.modelPath

    override fun listModels(): List<ModelInfo> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://alphacephei.com/vosk/models/model-list.json"))
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        return parseModels(response.body())
    }

    private fun parseModels(json: String): List<VoskModelInfo> =
        JsonParser.parseString(json).asJsonArray
            .map { it.asJsonObject }
            .filter {
                // "small" and "big-lgraph" support grammar, "big" doesn't
//                    it.get("type").asString != "big"
                it.get("obsolete").asString != "true"
                    || it.get("version").asString.startsWith("daanzu-20200905")
            }
            .map {
                VoskModelInfo(
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
}
