package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext

//only selected configuration
class DebugActionRecognizer : ActionRecognizer {
    override fun isMatching(sentence: String) = "debug" in sentence
    override fun getActionInfo(sentence: String, dataContext: DataContext) = ActionCallInfo("Debug")
}
