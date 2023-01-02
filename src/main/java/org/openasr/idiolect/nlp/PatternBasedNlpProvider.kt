package org.openasr.idiolect.nlp

import com.intellij.openapi.actionSystem.ActionManager
import org.openasr.idiolect.actions.ExecuteVoiceCommandAction
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.handlers.*

class PatternBasedNlpProvider : NlpProvider {
    override fun displayName() = "Pattern"

    private val handlers = arrayOf(
            IdeaNavigationHandler(),
            FileNavigationHandler(),
            KeyboardHandler(),
            CodeHandler(),
//            JavaHandler(), // moved to JavaActionRecognizer
            RunDebugHandler(),
            ChattyHandler()
    )

    init {
        ActionManager.getInstance().registerAction("Idiolect.VoiceAction-registeredInCode", ExecuteVoiceCommandAction)
    }

    override fun activate() {
    }

    /**
     * @param utterance - the command as spoken
     */
    override fun processNlpRequest(nlpRequest: NlpRequest) {
//        for (handler in handlers) {
//            if (handler.processUtterance(utterance)) {
//                return
//            }
//        }

        IdeService.invokeAction(ExecuteVoiceCommandAction, nlpRequest)
    }
}
