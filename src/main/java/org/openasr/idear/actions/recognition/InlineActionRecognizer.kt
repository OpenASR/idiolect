package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext

class InlineActionRecognizer : ActionRecognizer {
    override fun isMatching(sentence: String) = "inline" in sentence

    override fun getActionInfo(sentence: String, dataContext: DataContext) =
            if (!isMatching(sentence)) null else ActionCallInfo("Inline")
}
