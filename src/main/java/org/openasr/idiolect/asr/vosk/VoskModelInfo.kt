package org.openasr.idiolect.asr.vosk

import org.openasr.idiolect.asr.models.ModelInfo

class VoskModelInfo(lang: String, langText: String,
                    name: String, url: String,
                    size: Int, sizeText: String,
                    val type: String): ModelInfo(name, lang, langText, size, sizeText, url) {

    override fun toString(): String {
        var info = sizeText
        if (type == "big") {
            info += ", dynamic grammars not supported"
        }
        return "$name (${info})"
    }
}
