package org.openasr.idear.nlp

class NlpResponse(
    val intentName: String,
    val slots: Map<String, String>? = null,
    val sessionAttributes: MutableMap<String, out String>? = null)
