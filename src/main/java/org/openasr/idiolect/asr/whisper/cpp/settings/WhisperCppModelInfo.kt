package org.openasr.idiolect.asr.whisper.cpp.settings

import org.openasr.idiolect.asr.models.ModelInfo

class WhisperCppModelInfo(name: String, lang: String?, size: Int, sizeText: String) : ModelInfo(
    name,
    lang,
    if (lang == "en") "English" else "all",
    size,
    sizeText,
    "$src/$pfx-$name${if (lang == null) "" else ".$lang"}.bin"
)
