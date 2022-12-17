package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import java.awt.Component

interface ActionRecognizer {
    /**
     * dataContext.getData(PlatformCoreDataKeys.FILE_EDITOR).file.fileType
     *
     * @param dataContext could be used to determine code language etc
     * @param component is TerminalDisplay | EditorComponentImpl | ProjectViewPanel | ChangesViewPanel
     */
    fun isSupported(dataContext: DataContext, component: Component?) = true

    /**
     * Subclasses must implement `isMatching()` to return true, and `getActionInfo()`
     * _or_ `getHandler(utterance)`
     */
    fun isMatching(utterance: String): Boolean
    fun getActionInfo(utterance: String, dataContext: DataContext): ActionCallInfo?

    /**
     * A MultiSentenceActionRecognizer subclass may implement support for multiple phrases
     */
    fun getHandler(utterance: String): SpeechActionHandler? =
        if (isMatching(utterance))
            ::getActionInfo
            else null
}

interface MultiSentenceActionRecognizer : ActionRecognizer {
    override fun isMatching(utterance: String) = false
    override fun getActionInfo(utterance: String, dataContext: DataContext): ActionCallInfo? = null
}

typealias SpeechActionHandler = (utterance: String, context: DataContext) -> ActionCallInfo?
