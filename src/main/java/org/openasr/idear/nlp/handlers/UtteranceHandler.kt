package org.openasr.idear.nlp.handlers

interface UtteranceHandler {
    fun processUtterance(utterance: String): Boolean
}
