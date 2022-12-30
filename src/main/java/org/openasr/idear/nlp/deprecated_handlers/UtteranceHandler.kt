package org.openasr.idear.nlp.deprecated_handlers

interface UtteranceHandler {
    fun processUtterance(utterance: String): Boolean
}
