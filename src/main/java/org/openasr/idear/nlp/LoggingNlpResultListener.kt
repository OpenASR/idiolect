package org.openasr.idear.nlp

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.actions.recognition.ActionCallInfo
import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity

object LoggingNlpResultListener : NlpResultListener {
    private val logger = Logger.getInstance(javaClass)

    override fun onRecognition(nlpRequest: NlpRequest) {
        val alternatives = nlpRequest.alternatives.joinToString("|")
        logger.info("Listener Recognised: $alternatives")
        println("Listener Recognized: $alternatives")
    }

    override fun onFulfilled(actionCallInfo: ActionCallInfo) =
            logger.info("Fulfilled: ${actionCallInfo.actionId}")

    override fun onFailure(message: String) = logger.warn("Failure: $message")

    override fun onMessage(message: String, verbosity: Verbosity) = logger.info("Message: $message")
}
