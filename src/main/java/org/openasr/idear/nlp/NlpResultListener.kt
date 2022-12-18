package org.openasr.idear.nlp

import com.intellij.util.messages.Topic

interface NlpResultListener {
    companion object {
        @Topic.AppLevel
        val NLP_RESULT_TOPIC = Topic.create("NLP Result", NlpResultListener::class.java)

        enum class Verbosity {
            /** Idear will ask further questions to elicit slot/intent at this level */
            INFO,
            /** Chatty, probably appropriate for the visually impaired */
            DEBUG,
            /** Hear a verbal acknowledgment of all commands */
            ALL
        }
    }

    fun onListening(listening: Boolean) {}

    fun onRecognition(utterance: String)

    fun onFulfilled(intentName: String,
                    slots: MutableMap<String, out String>?,
                    sessionAttributes: MutableMap<String, out String>?)

    /**
     * Display/read a failure message to the user.
     */
    fun onFailure(message: String)
    //    fun onIncomplete()

    /**
     * Display/read a message to the user.
     */
    fun onMessage(message: String, verbosity: Verbosity)
}
