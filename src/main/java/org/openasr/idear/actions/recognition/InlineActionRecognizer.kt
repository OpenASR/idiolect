package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext

class InlineActionRecognizer : ActionRecognizer {

    override fun isMatching(sentence: String): Boolean {
        return sentence.contains("inline")
    }

    override fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo? {
        if (!isMatching(sentence)) return null
        return ActionCallInfo("Inline")
    }
}
