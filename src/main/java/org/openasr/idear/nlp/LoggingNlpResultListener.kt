package org.openasr.idear.nlp

import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity
import java.util.logging.Level
import java.util.logging.Logger


class LoggingNlpResultListener : NlpResultListener {
    private val logger = Logger.getLogger("NlpResultListener")

    override fun onFulfilled(intentName: String, slots: MutableMap<String, out String>?, sessionAttributes: MutableMap<String, out String>?) =
            logger.log(Level.INFO, "Fulfilled: ", intentName)

    override fun onFailure(message: String) =
            logger.log(Level.WARNING, "Failure: " + message)

    override fun onMessage(message: String, verbosity: Verbosity) =
            logger.log(Level.INFO, "Message: " + message)
}
