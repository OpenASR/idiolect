package org.openasr.idear.asr.vosk

/** "small" and "big-lgraph" support grammar, "big" doesn't */
class ModelComparator : Comparator<ModelInfo> {
    override fun compare(a: ModelInfo, b: ModelInfo): Int {
        val aIsBig = a.type == "big"
        val bIsBig = b.type == "big"
        return if (aIsBig && !bIsBig)
            1
        else if (bIsBig && !aIsBig)
            -1
        else b.size - a.size
    }
}
