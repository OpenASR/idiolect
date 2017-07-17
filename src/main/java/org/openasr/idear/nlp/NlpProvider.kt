package org.openasr.idear.nlp

interface NlpProvider {
    fun processUtterance(utterance: String)
}
