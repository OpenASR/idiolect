package org.openasr.idiolect.nlp.intent.handlers

import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.actions.recognition.JavaActionRecognizer
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpResponse

class JavaActionIntentHandler : IntentHandler {
    companion object {
        val INTENT_PREFIX = "Java."
    }

    override fun tryFulfillIntent(response: NlpResponse, nlpContext: NlpContext): ActionCallInfo? {
//        if (!nlpResponse.intentName.startsWith(INTENT_PREFIX)) {
//            return null
//        }

        return when (response.intentName) {
            JavaActionRecognizer.INTENT_NEW_CLASS -> newClass(response)
            else -> {
                if (response.intentName.startsWith(JavaActionRecognizer.INTENT_TEMPLATE_PREFIX)) {
                    ActionCallInfo("Template.Java." +
                        response.intentName.substring(JavaActionRecognizer.INTENT_TEMPLATE_PREFIX.length))
                }
                null
            }
        }
    }

    private fun newClass(nlpResponse: NlpResponse): ActionCallInfo {
        val className = nlpResponse.slots!!["className"]!!

        ActionRoutines.routineAddNewClass(className)

        return ActionCallInfo(nlpResponse.intentName, true)
    }
}
