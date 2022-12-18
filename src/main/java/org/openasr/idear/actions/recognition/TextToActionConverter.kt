package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.extensions.ExtensionPointName


class TextToActionConverter(private val dataContext: DataContext) {
    private var EP_NAME = ExtensionPointName<ActionRecognizer>("org.openasr.idear.actionRecognizer")

    /**
     * Find the first SpeechActionHandler/ActionRecognizer which supports the current context _and_ utterance
     */
    fun extractAction(utterance: String) =
            EP_NAME.extensions
                    .filter { it.isSupported(dataContext, dataContext.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT)) }
                    .firstNotNullOfOrNull { it.getHandler(utterance) }
                    ?.invoke(utterance, dataContext)
}
