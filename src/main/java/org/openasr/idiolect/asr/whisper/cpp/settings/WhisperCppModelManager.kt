package org.openasr.idiolect.asr.whisper.cpp.settings

import org.openasr.idiolect.asr.models.ModelInfo
import org.openasr.idiolect.asr.models.ModelManager
import java.io.File
import java.io.InputStream

const val src = "https://huggingface.co/ggerganov/whisper.cpp"
const val pfx = "resolve/main/ggml"

object WhisperCppModelManager : ModelManager<WhisperCppConfigurable>(
    "$src/$pfx-base.bin",
    src,
    WhisperCppConfigurable::class.java
) {
    override fun configuredModelPath() = WhisperCppConfig.settings.modelPath

    override fun listModels(): List<ModelInfo> {
        return listOf(
            WhisperCppModelInfo("tiny", "en", 390, "~390 MB"),
            WhisperCppModelInfo("tiny", null, 390, "~390 MB"),
            WhisperCppModelInfo("base", "en",500, "~500 MB"),
            WhisperCppModelInfo("base", null, 500, "~500 MB"),
            WhisperCppModelInfo("small", "en", 1000, "~1.0 GB"),
            WhisperCppModelInfo("small", null, 1000, "~1.0 GB"),
            WhisperCppModelInfo("medium", "en", 2600, "~2.6 GB"),
            WhisperCppModelInfo("medium", "en", 2600, "~2.6 GB"),
            WhisperCppModelInfo("large-v1", null, 4700, "~4.7 GB"),
            WhisperCppModelInfo("large", null, 4700, "~4.7 GB"),
        )
    }

    override fun unpackModel(modelPath: String, modelBin: InputStream) {
        val file = File(modelPath)
        val modelDir = file.parentFile
        modelDir.mkdirs()

        file.outputStream().use { fos -> modelBin.copyTo(fos) }
    }
}
