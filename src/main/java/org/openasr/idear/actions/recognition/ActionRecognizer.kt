package com.jetbrains.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.extensions.ExtensionPointName

interface ActionRecognizer {
    fun isMatching(sentence: String): Boolean
    fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo?
}
