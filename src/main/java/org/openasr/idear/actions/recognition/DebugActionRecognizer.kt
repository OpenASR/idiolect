package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.impl.EditorComponentImpl
import java.awt.Component

class DebugActionRecognizer : ActionRecognizer {
    override fun isSupported(dataContext: DataContext, component: Component?)= component is EditorComponentImpl
    override fun isMatching(utterance: String) = "debug" in utterance
    override fun getActionInfo(utterance: String, dataContext: DataContext) = ActionCallInfo(IdeActions.ACTION_DEFAULT_DEBUGGER)
}
