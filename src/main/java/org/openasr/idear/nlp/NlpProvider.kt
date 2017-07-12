package org.openasr.idear.nlp

interface NlpProvider {
    fun processUtterance(utterance: String,
                         sessionAttributes: Map<String, String>? = null)
}