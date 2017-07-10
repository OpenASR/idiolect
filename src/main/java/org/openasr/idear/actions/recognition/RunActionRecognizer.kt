package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext

//runs only selected configuration
class RunActionRecognizer : ActionRecognizer {
    override fun isMatching(sentence: String): Boolean {
        return sentence.contains("run")
    }

    override fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo {
        return ActionCallInfo("Run")
    }
}
