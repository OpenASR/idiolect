package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.utils.toUpperCamelCase

/**
 * As a last resort, attempt to match a registered Action ID
 *
 * @see com.intellij.openapi.actionSystem.IdeActions
 */
open class RegisteredActionRecognizer : MultiSentenceActionRecognizer {

    override fun getHandler(utterance: String): SpeechActionHandler? {
        val actionId = getActionIdForUtterance(utterance)
        val action = if (ActionManager.getInstance().isGroup(actionId)) null
                        else ActionManager.getInstance().getAction(actionId)

        if (action != null) {
            println("exectuting registered action $actionId")
            return getHandlerForActionId(actionId)
        }

        return null
    }

    protected open fun getActionIdForUtterance(utterance: String): String {
        return mapOf(
                "go to" to "goto",
                "git" to "cvs",
                "change" to "diff"
        ).getOrDefault(utterance, utterance).toUpperCamelCase()
    }

    protected open fun getHandlerForActionId(actionId: String): SpeechActionHandler {
        val actionCall = ActionCallInfo(actionId)
        return { _: String, _: DataContext -> actionCall }
    }
}
