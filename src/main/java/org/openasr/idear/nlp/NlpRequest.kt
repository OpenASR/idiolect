package org.openasr.idear.nlp

class NlpRequest(val alternatives: List<String>) {
    val utterance: String
        get() = alternatives[0]
}
