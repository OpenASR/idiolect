package org.openasr.idear.nlp

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity


class LoggingNlpResultListener : NlpResultListener {
    private val logger = Logger.getInstance(LoggingNlpResultListener::class.java)

    override fun onFulfilled(intentName: String, slots: MutableMap<String, out String>?, sessionAttributes: MutableMap<String, out String>?) =
            logger.info("Fulfilled: $intentName")

    override fun onFailure(message: String) = logger.warn("Failure: $message")

    override fun onMessage(message: String, verbosity: Verbosity) = logger.info("Message: $message")
}
