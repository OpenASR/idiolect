package org.openasr.idear.nlp

// TODO: these methods will take parameters
interface NlpResultListener {
    fun onFulfilled()
    fun onFailure()
    fun onIncomplete()
}