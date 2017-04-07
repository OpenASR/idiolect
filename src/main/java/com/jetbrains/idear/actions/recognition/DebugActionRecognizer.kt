package com.jetbrains.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext

//only selected configuration
class DebugActionRecognizer : ActionRecognizer {
    override fun isMatching(sentence: String) = sentence.contains("debug")
    override fun getActionInfo(sentence: String, dataContext: DataContext) = ActionCallInfo("Debug")
}
