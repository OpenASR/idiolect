package org.openasr.idiolect.nlp.handlers

interface UtteranceHandler {
    fun processUtterance(utterance: String): Boolean
}
