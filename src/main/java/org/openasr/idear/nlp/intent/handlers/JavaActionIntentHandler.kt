package org.openasr.idear.nlp.intent.handlers

import com.intellij.openapi.actionSystem.*
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.actions.recognition.ActionCallInfo
import org.openasr.idear.actions.recognition.JavaActionRecognizer
import org.openasr.idear.nlp.NlpResponse

class JavaActionIntentHandler : IntentHandler {
    companion object {
        val INTENT_PREFIX = "Java."
    }

    override fun tryFulfillIntent(nlpResponse: NlpResponse, dataContext: DataContext): ActionCallInfo? {
//        if (!nlpResponse.intentName.startsWith(INTENT_PREFIX)) {
//            return null
//        }

        return when (nlpResponse.intentName) {
            JavaActionRecognizer.INTENT_NEW_CLASS -> newClass(nlpResponse)
            else -> null
        }
    }

    private fun newClass(nlpResponse: NlpResponse): ActionCallInfo {
        val className = nlpResponse.slots!!["className"]!!

        ActionRoutines.routineAddNewClass(className)

        return ActionCallInfo(nlpResponse.intentName, true)
    }
}
