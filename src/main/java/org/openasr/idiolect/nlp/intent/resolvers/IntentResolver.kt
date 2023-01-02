package org.openasr.idiolect.nlp.intent.resolvers

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResponse
import java.awt.Component

/**
 * Processes an NlpRequest and resolves an Intent (+slots in NlpResponse) from NlpRequest (utterance/alternatives)
 */
abstract class IntentResolver(open val displayName: String, open val order: Int = Int.MAX_VALUE / 2) {
    abstract val grammars: List<NlpGrammar>

    companion object {
        /** idiolect handlers that augment IntelliJ Actions */
        val INTENT_PREFIX_IDIOLECT_ACTION = "idiolect.Action."
        /** idiolect-specific commands */
        val INTENT_PREFIX_IDIOLECT_COMMAND = "idiolect.Command."
    }

    /**
     * dataContext.getData(PlatformCoreDataKeys.FILE_EDITOR).file.fileType
     * Wrong context / file type, will not see the phrase
     * @param dataContext could be used to determine code language etc
     * @param component is TerminalDisplay | EditorComponentImpl | ProjectViewPanel | ChangesViewPanel
     */
    open fun isSupported(dataContext: DataContext, component: Component?) = true

    open fun tryResolveIntent(nlpRequest: NlpRequest, dataContext: DataContext): NlpResponse? =
        grammars.firstNotNullOfOrNull { it.tryMatchRequest(nlpRequest, dataContext) }
}
