package org.openasr.idiolect.nlp.handlers

import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.nlp.Commands
import org.openasr.idiolect.tts.TtsService

class ChattyHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            utterance == Commands.HI_IDEA -> TtsService.say("Hi!")
            "tell me about yourself" in utterance -> ActionRoutines.routineAbout()
            utterance.startsWith("tell me a joke") -> ActionRoutines.tellJoke()
//            u.startsWith(OKAY_IDEA) -> routineOkIdea()
//            u.startsWith(OKAY_GOOGLE) -> fireGoogleSearch()
            else -> return false
        }

        return true
    }
}
