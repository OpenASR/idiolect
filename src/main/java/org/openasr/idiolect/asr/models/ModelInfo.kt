package org.openasr.idiolect.asr.models

/**
 * @param size - just used to sort the models
 */
open class ModelInfo(val name: String, val lang: String?, val langText: String, val size: Int, val sizeText: String, val url: String) {
    override fun toString(): String {
        return "$name ($sizeText)"
    }
}
