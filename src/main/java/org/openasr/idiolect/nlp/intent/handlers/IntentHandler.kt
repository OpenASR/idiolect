package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.nlp.NlpResponse

/**
 * Fulfills an NlpResponse (Intent + slots), performing desired actions
 */
interface IntentHandler {
    fun tryFulfillIntent(response: NlpResponse, dataContext: DataContext): ActionCallInfo?
}
