package org.openasr.idear.nlp

// TODO: these methods will take parameters
interface NlpResultListener {
    companion object {
        enum class Verbosity {
            /** Idear will ask further questions to elicit slot/intent at this level */
            INFO,
            /** Chatty, probably appropriate for the visually impaired */
            DEBUG,
            /** Hear a verbal acknowledgment of all commands */
            ALL
        }
    }

    fun onFulfilled(intentName: String, slots: MutableMap<String, out String>?, sessionAttributes: MutableMap<String, out String>?)
    fun onFailure(message: String)
    //    fun onIncomplete()
    fun onMessage(message: String, verbosity: Verbosity)
}