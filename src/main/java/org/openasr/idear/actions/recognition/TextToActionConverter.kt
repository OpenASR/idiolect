package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.extensions.ExtensionPointName


class TextToActionConverter(private val dataContext: DataContext) {
    internal var EP_NAME = ExtensionPointName<ActionRecognizer>("org.openasr.idear.actionRecognizer")

    fun extractAction(sentence: String) =
        EP_NAME.extensions
            .firstOrNull { it.isMatching(sentence) }
            ?.getActionInfo(sentence, dataContext)
}


