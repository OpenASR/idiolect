package org.openasr.idear.nlp.intent.handlers

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.actions.recognition.ActionCallInfo
import org.openasr.idear.nlp.NlpResponse

/**
 * Fulfills an NlpResponse (Intent + slots), performing desired actions
 */
interface IntentHandler {
    fun tryFulfillIntent(response: NlpResponse, dataContext: DataContext): ActionCallInfo?
}
