package org.openasr.idiolect.asr.vosk

/** "small" and "big-lgraph" support grammar, "big" doesn't */
class ModelComparator : Comparator<VoskModelInfo> {
    override fun compare(a: VoskModelInfo, b: VoskModelInfo): Int {
        val aIsBig = a.type == "big"
        val bIsBig = b.type == "big"
        return if (aIsBig && !bIsBig)
            1
        else if (bIsBig && !aIsBig)
            -1
        else b.size - a.size
    }
}
