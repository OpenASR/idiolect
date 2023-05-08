package org.openasr.idiolect.nlp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.nlp.NlpResultListener.Companion.Verbosity

object LoggingNlpResultListener : NlpResultListener {
    private val logger = logger<LoggingNlpResultListener>()

    override fun onRecognition(nlpRequest: NlpRequest) {
        val alternatives = nlpRequest.alternatives.joinToString("|")
        logger.info("Listener Recognised: $alternatives")
    }

    override fun onFulfilled(actionCallInfo: ActionCallInfo) =
            logger.info("Fulfilled: ${actionCallInfo.actionId}")

    override fun onFailure(message: String) = logger.warn("Failure: $message")

    override fun onMessage(message: String, verbosity: Verbosity) = logger.info("Message: $message")
}
