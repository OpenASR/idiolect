package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import java.awt.Component

//runs only selected configuration
class RunActionRecognizer : ActionRecognizer {
    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
    override fun isMatching(utterance: String) = "run" in utterance
    override fun getActionInfo(utterance: String, dataContext: DataContext) = ActionCallInfo("Run")
}
