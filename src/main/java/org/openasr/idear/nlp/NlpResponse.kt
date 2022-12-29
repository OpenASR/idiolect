package org.openasr.idear.nlp

class NlpResponse(val intentName: String,
                  val slots: Map<String, String>?,
                  val sessionAttributes: MutableMap<String, out String>?)
