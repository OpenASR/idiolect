package org.openasr.idiolect.nlp

class NlpResponse(
    val intentName: String,
    val slots: Map<String, String>? = null,
    val sessionAttributes: MutableMap<String, out String>? = null)
