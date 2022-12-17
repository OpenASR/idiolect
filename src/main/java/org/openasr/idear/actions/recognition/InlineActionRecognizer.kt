package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import java.awt.Component

class InlineActionRecognizer : ActionRecognizer {
    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
    override fun isMatching(utterance: String) = "inline" in utterance

    override fun getActionInfo(utterance: String, dataContext: DataContext) =
            if (!isMatching(utterance)) null else ActionCallInfo("Inline")
}
