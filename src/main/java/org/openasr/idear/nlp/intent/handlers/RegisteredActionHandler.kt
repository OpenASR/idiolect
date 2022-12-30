package org.openasr.idear.nlp.intent.handlers

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.recognition.ActionCallInfo
import org.openasr.idear.nlp.NlpResponse

class RegisteredActionHandler : IntentHandler {
    override fun tryFulfillIntent(response: NlpResponse, dataContext: DataContext): ActionCallInfo? {
        return ActionCallInfo(response.intentName)
    }
}
