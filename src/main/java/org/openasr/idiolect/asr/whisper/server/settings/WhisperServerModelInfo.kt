package org.openasr.idiolect.asr.whisper.server.settings

import org.openasr.idiolect.asr.models.ModelInfo

// TODO: `slug` probably isn't needed
class WhisperServerModelInfo(name: String, lang: String?, size: Int, sizeText: String, slug: String) : ModelInfo (
    name,
    lang,
    if (lang == "en") "English" else "all",
    size,
    sizeText,
    "$src/$slug/$name${if (lang == null) "" else ".$lang"}.pt"
) {
    fun fullName(): String = "$name${if (lang == null) "" else ".$lang"}"
}
