package org.openasr.idear.nlp.handlers

import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.nlp.Commands
import org.openasr.idear.tts.TTSService

class ChattyHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            utterance == Commands.HI_IDEA -> TTSService.say("Hi!")
            "tell me about yourself" in utterance -> ActionRoutines.routineAbout()
            utterance.startsWith("tell me a joke") -> ActionRoutines.tellJoke()
//            u.startsWith(OKAY_IDEA) -> routineOkIdea()
//            u.startsWith(OKAY_GOOGLE) -> fireGoogleSearch()
            else -> return false
        }

        return true
    }
}
