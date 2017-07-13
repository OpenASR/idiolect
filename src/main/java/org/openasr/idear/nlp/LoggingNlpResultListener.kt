package org.openasr.idear.nlp

import java.util.logging.Level
import java.util.logging.Logger

class LoggingNlpResultListener : NlpResultListener {
    override fun onFulfilled(intentName: String, slots: Map<String, out String>?) {
        logger.log(Level.INFO, "Fulfilled: ", intentName)
    }

    override fun onFailure(message: String) {
        logger.log(Level.WARNING, "Failure: " + message);
    }

    override fun onMessage() {
        logger.log(Level.INFO, "Message: ")
    }

    companion object {
        private val logger = Logger.getLogger("NlpResultListener")
    }
}