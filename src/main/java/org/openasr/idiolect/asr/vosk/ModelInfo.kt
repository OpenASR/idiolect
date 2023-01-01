package org.openasr.idiolect.asr.vosk

class ModelInfo(val lang: String, val langText: String,
                val name: String, val url: String,
                val size: Int, val sizeText: String,
                val type: String) {

    override fun toString(): String {
        var info = sizeText
        if (type == "big") {
            info += ", dynamic grammars not supported"
        }
        return "$name (${info})"
    }
}
