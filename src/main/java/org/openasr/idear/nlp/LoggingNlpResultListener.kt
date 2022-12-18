package org.openasr.idear.nlp

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity

object LoggingNlpResultListener : NlpResultListener {
    private val logger = Logger.getInstance(javaClass)

    override fun onRecognition(utterance: String) {
        logger.info("Listener Recognised: $utterance")
        println("Listener Recognized: $utterance")
    }

    override fun onFulfilled(intentName: String, slots: MutableMap<String, out String>?, sessionAttributes: MutableMap<String, out String>?) =
            logger.info("Fulfilled: $intentName")

    override fun onFailure(message: String) = logger.warn("Failure: $message")

    override fun onMessage(message: String, verbosity: Verbosity) = logger.info("Message: $message")
}
