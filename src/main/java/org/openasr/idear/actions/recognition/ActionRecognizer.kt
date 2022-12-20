package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import org.openasr.idear.nlp.NlpGrammar
import org.openasr.idear.nlp.NlpRequest
import org.openasr.idear.nlp.NlpResponse
import java.awt.Component

abstract class ActionRecognizer(open val displayName: String, open val order: Int = Int.MAX_VALUE / 2) {
    abstract val grammars: List<NlpGrammar>

    /**
     * dataContext.getData(PlatformCoreDataKeys.FILE_EDITOR).file.fileType
     *
     * @param dataContext could be used to determine code language etc
     * @param component is TerminalDisplay | EditorComponentImpl | ProjectViewPanel | ChangesViewPanel
     */
    open fun isSupported(dataContext: DataContext, component: Component?) = true

//    /**
//     * Subclasses must implement `isMatching()` to return true, and `getActionInfo()`
//     * _or_ `getHandler(utterance)`
//     */
//    fun isMatching(utterance: String): Boolean
//    fun getActionInfo(utterance: String, dataContext: DataContext): ActionCallInfo?

//    /**
//     * A MultiSentenceActionRecognizer subclass may implement support for multiple phrases
//     */
//    fun getHandler(utterance: String): SpeechActionHandler? =
//        if (isMatching(utterance))
//            ::getActionInfo
//            else null

//    /** Called by tryResolveIntent() */
//    open fun getGrammars(): List<NlpGrammar> = grammars

    open fun tryResolveIntent(nlpRequest: NlpRequest, dataContext: DataContext): ActionCallInfo? {
        for (grammar in grammars) {
            val info = grammar.tryMatchRequest(nlpRequest, dataContext)
            if (info != null) {
                return info
            }
        }

        return null
    }

//    /** Called by tryResolveIntent() */
//    fun fulfillIntent(nlpResponse: NlpResponse)
}

///**
// * Classes implementing this interface must implement `getHandler(utterance: String): SpeechActionHandler?`
// */
//interface MultiSentenceActionRecognizer : ActionRecognizer {
//    override fun isMatching(utterance: String) = false
////    override fun getActionInfo(utterance: String, dataContext: DataContext): ActionCallInfo? = null
//}
//
//typealias SpeechActionHandler = (utterance: String, context: DataContext) -> ActionCallInfo?

typealias NlpHandler = (nlpRequest: NlpRequest, context: DataContext) -> NlpResponse
