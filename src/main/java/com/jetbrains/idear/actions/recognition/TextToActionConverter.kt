package com.jetbrains.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.extensions.ExtensionPointName


class TextToActionConverter(private val dataContext: DataContext) {
    internal var EP_NAME = ExtensionPointName<ActionRecognizer>("com.jetbrains.idear.actionRecognizer")

    fun extractAction(sentence: String): ActionCallInfo? {
        for (recognizer in EP_NAME.extensions) {
            if (recognizer.isMatching(sentence))
                return recognizer.getActionInfo(sentence, dataContext)
        }
        return null
    }
}


