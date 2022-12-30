package org.openasr.idear.nlp

import com.intellij.openapi.actionSystem.ActionManager
import org.openasr.idear.actions.ExecuteVoiceCommandAction
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.deprecated_handlers.*

class PatternBasedNlpProvider : NlpProvider {
    override fun displayName() = "Pattern"


    init {
        ActionManager.getInstance().registerAction("Idear.VoiceAction-registeredInCode", ExecuteVoiceCommandAction)
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
