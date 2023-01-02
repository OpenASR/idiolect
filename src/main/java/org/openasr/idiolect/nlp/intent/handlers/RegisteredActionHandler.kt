package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.nlp.NlpResponse

class RegisteredActionHandler : IntentHandler {
    override fun tryFulfillIntent(response: NlpResponse, dataContext: DataContext): ActionCallInfo? {
        return ActionCallInfo(response.intentName)
    }
}
