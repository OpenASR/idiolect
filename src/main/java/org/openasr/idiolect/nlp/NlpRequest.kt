package org.openasr.idiolect.nlp

class NlpRequest(val alternatives: List<String>) {
    val utterance: String
        get() = alternatives[0]

    override fun toString(): String {
        return alternatives.toString()
    }
}
