package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext

interface ActionRecognizer {
    fun isMatching(sentence: String): Boolean
    fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo?
}
