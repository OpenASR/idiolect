package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.openapi.editor.EditorFactory
import org.openasr.idiolect.actions.recognition.*
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver

/** idiolect-specific commands */
class IdiolectNavigationIntentHandler : IntentHandler {
    companion object {
        val INTENT_PREFIX = IntentResolver.INTENT_PREFIX_IDIOLECT_NAVIGATION
        const val SLOT_NAME = "name"
    }

    override fun tryFulfillIntent(nlpResponse: NlpResponse, nlpContext: NlpContext): ActionCallInfo? {
        val intentName = nlpResponse.intentName

        if (!intentName.startsWith(INTENT_PREFIX)) {
            return null
        }

        when (intentName) {
            IdiolectNavigationRecognizer.INTENT_SWITCH_TO_TAB -> switchToTab(nlpContext, nlpResponse.slots?.get(SLOT_NAME)!!)

            else -> return null
        }

        return ActionCallInfo(intentName, true)
    }

    private fun switchToTab(nlpContext: NlpContext, name: String) {
        val editorWindows = EditorFactory.getInstance().allEditors
        val tabNames = editorWindows.map {
            var name = it.virtualFile.name
//            it.component.requestFocus()
        }

    }

}
