package org.openasr.idear.nlp

import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.actions.recognition.ActionCallInfo
import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity

object LoggingNlpResultListener : NlpResultListener {
    private val logger = Logger.getInstance(javaClass)

    override fun onRecognition(nlpRequest: NlpRequest) {
        val utterance = nlpRequest.utterance
        logger.info("Listener Recognised: $utterance")
        println("Listener Recognized: $utterance")
    }

    override fun onFulfilled(actionCallInfo: ActionCallInfo) =
            logger.info("Fulfilled: ${actionCallInfo.actionId}")

    override fun onFailure(message: String) = logger.warn("Failure: $message")

    override fun onMessage(message: String, verbosity: Verbosity) = logger.info("Message: $message")
}
