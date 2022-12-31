package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.logger
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRequest
import org.openasr.idear.nlp.NlpResponse
import org.openasr.idear.nlp.intent.resolvers.IntentResolver
import org.openasr.idear.utils.ActionUtils
import org.openasr.idear.utils.toUpperCamelCase

/**
 * As a last resort, attempt to match a registered Action ID
 *
 * @see com.intellij.openapi.actionSystem.IdeActions
 */
open class RegisteredActionRecognizer : IntentResolver("Idea Native Actions", Int.MAX_VALUE) {
    companion object {
        private val log = logger<RegisteredActionRecognizer>()
    }

    override val grammars by lazy { buildGrammars() }
    private val actionManager by lazy { ActionManager.getInstance() }

    protected open fun buildGrammars() = ActionUtils.buildGrammar()

    override fun tryResolveIntent(nlpRequest: NlpRequest, dataContext: DataContext): NlpResponse? {
        return nlpRequest.alternatives.firstNotNullOfOrNull { utterance ->
            val actionId = getActionIdForUtterance(utterance)
            val action = actionManager.run { if (isGroup(actionId)) null else getAction(actionId) }
            return if (action != null) {
                log.info("Matched actionId for '${utterance}': $actionId")
                NlpResponse(actionId)
            } else null
        }
    }

    protected open fun getActionIdForUtterance(utterance: String): String =
        mapOf(
            "go to" to "goto",
            "git" to "cvs",
            "change" to "diff",
            "look and feel" to "laf"
        ).getOrDefault(utterance, utterance).toUpperCamelCase()
}
