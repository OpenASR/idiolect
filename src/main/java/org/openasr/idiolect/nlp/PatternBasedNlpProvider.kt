package org.openasr.idiolect.nlp

import com.intellij.openapi.actionSystem.ActionManager
import org.openasr.idiolect.actions.ExecuteVoiceCommandAction
import org.openasr.idiolect.ide.IdeService

class PatternBasedNlpProvider : NlpProvider {
    override fun displayName() = "Pattern"


    init {
        ActionManager.getInstance().registerAction("Idiolect.VoiceAction-registeredInCode", ExecuteVoiceCommandAction)
    }

    override fun activate() {
    }

    /**
     * @param utterance - the command as spoken
     */
    override fun processNlpRequest(nlpRequest: NlpRequest) {
        IdeService.invokeAction(ExecuteVoiceCommandAction, nlpRequest)
    }
}
