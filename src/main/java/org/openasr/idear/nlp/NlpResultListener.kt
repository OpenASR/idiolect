package org.openasr.idear.nlp

// TODO: these methods will take parameters
interface NlpResultListener {

    fun onFulfilled(intentName: String, params: Map<String, out String>?)
    fun onFailure(message: String)
//    fun onIncomplete()
    fun onMessage()
}