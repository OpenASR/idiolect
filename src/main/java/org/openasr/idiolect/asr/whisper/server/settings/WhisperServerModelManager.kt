package org.openasr.idiolect.asr.whisper.server.settings

import org.openasr.idiolect.asr.models.ModelInfo
import org.openasr.idiolect.asr.models.ModelManager
import org.openasr.idiolect.asr.whisper.server.WhisperServerAsr
import java.io.File

const val src = "https://openaipublic.azureedge.net/main/whisper/models"

/** @see https://github.com/openai/whisper/blob/main/whisper/__init__.py */
object WhisperServerModelManager : ModelManager<WhisperServerConfigurable>(
//    "$src/ed3a0b6b1c0edf879ad9b11b1af5a0e6ab5db9205f891f668f8b0e6c6326e34e/base.pt",
    "",
    "https://github.com/openai/whisper/tree/main#available-models-and-languages",
    WhisperServerConfigurable::class.java
) {
    init {
        if (WhisperServerConfig.settings.modelPath.isEmpty()) {
            val modelDir = File(modelDir())
            val modelPath = modelDir.list { dir, name -> name.endsWith(".pt") }?.first()
            if (modelPath != null) {
                WhisperServerConfig.saveModelPath(modelPath)
            }
        }
    }

    override fun modelDir() = "${System.getenv("XDG_CACHE_HOME") ?: (System.getProperty("user.home") + "/.cache")}/whisper"

    override fun configuredModelPath() = WhisperServerConfig.settings.modelPath

    override fun listModels(): List<ModelInfo> {
        return listOf(
            WhisperServerModelInfo("tiny", "en", 39, "39M params, ~1 GB RAM",
                "d3dd57d32accea0b295c96e26691aa14d8822fac7d9d27d5dc00b4ca2826dd03"),
            WhisperServerModelInfo("tiny", null, 39, "39M params, ~1 GB RAM",
                "65147644a518d12f04e32d6f3b26facc3f8dd46e5390956a9424a650c0ce22b9"),
            WhisperServerModelInfo("base", "en",74,  "74M params, ~1 GB RAM",
                "25a8566e1d0c1e2231d1c762132cd20e0f96a85d16145c3a00adf5d1ac670ead"),
            WhisperServerModelInfo("base", null, 74, "74M params, ~1 GB RAM",
                "ed3a0b6b1c0edf879ad9b11b1af5a0e6ab5db9205f891f668f8b0e6c6326e34e"),
            WhisperServerModelInfo("small", "en", 244, "244M params, ~2 GB RAM",
                "f953ad0fd29cacd07d5a9eda5624af0f6bcf2258be67c92b79389873d91e0872"),
            WhisperServerModelInfo("small", null, 244, "244M params, ~2 GB RAM",
                "9ecf779972d90ba49c06d968637d720dd632c55bbf19d441fb42bf17a411e794"),
            WhisperServerModelInfo("medium", "en", 769, "769M params, ~5 GB RAM",
                "d7440d1dc186f76616474e0ff0b3b6b879abc9d1a4926b7adfa41db2d497ab4f"),
            WhisperServerModelInfo("medium", null, 769, "769M params, ~5 GB RAM",
                "345ae4da62f9b3d59415adc60127b97c714f32e89e936602e85993674d08dcb1"),
            WhisperServerModelInfo("large-v2", null, 1550, "1550 M params, ~10 GB RAM",
                "81f7c96c852ee8fc832187b0132e569d6c3065a3252ed18e56effd0b6a73e524"),
        )
    }

    override fun installModel(modelInfo: ModelInfo) {
        WhisperServerAsr.setModel((modelInfo as WhisperServerModelInfo).fullName())
    }

    override fun installModel(url: String): String {
        // TODO: verify that model is installed by whisper-server
        return url
    }
}
