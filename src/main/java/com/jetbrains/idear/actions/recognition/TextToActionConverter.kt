package com.jetbrains.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext


class TextToActionConverter(private val dataContext: DataContext) {
    fun extractAction(sentence: String): ActionCallInfo? {
        for (recognizer in ActionRecognizer.EP_NAME.extensions) {
            if (recognizer.isMatching(sentence))
                return recognizer.getActionInfo(sentence, dataContext)
        }
        return null
    }
}


