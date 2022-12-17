package org.openasr.idear.nlp

import com.intellij.openapi.actionSystem.ActionManager
import org.openasr.idear.actions.ActionRoutines.pauseSpeech
import org.openasr.idear.actions.ExecuteVoiceCommandAction
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.handlers.*

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
        ActionManager.getInstance().registerAction("Idear.VoiceAction-registeredInCode", ExecuteVoiceCommandAction)
    }

    override fun activate() {
    }

    /**
     * @param utterance - the command as spoken
     */
    override fun processUtterance(utterance: String) {
        when {
            utterance.startsWith("speech pause") -> pauseSpeech()
            else -> {
                for (handler in handlers) {
                    if (handler.processUtterance(utterance)) {
                        return
                    }
                }

                IdeService.invokeAction(ExecuteVoiceCommandAction, utterance)
            }
        }
    }
}
