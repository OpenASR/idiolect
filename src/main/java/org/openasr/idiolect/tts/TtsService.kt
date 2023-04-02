package org.openasr.idiolect.tts

import com.intellij.openapi.application.ApplicationManager
import org.openasr.idiolect.nlp.NlpResultListener
import org.openasr.idiolect.settings.IdiolectConfig

object TtsService {
    private val messageBus = ApplicationManager.getApplication().messageBus

    fun say(text: String) {
        messageBus.syncPublisher(NlpResultListener.NLP_RESULT_TOPIC).onMessage(text, NlpResultListener.Companion.Verbosity.INFO)
        IdiolectConfig.getTtsProvider().say(text)
    }

    fun dispose() = IdiolectConfig.getTtsProvider().dispose()
}
