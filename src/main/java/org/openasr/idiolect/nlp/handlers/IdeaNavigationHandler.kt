package org.openasr.idiolect.nlp.handlers

import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.nlp.Commands


class IdeaNavigationHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            // "open settings|recent|terminal"
            utterance.startsWith(Commands.OPEN) -> ActionRoutines.routineOpen(utterance)
//            // "navigate" -> goes to declaration of highlighted field/function
//            utterance.startsWith(Commands.NAVIGATE) -> invokeAction(IdeActions.ACTION_GOTO_DECLARATION)
//            // "focus editor|project"
//            // "focus symbols" activates AceJump and waits for "jump $number"
//            utterance.startsWith(Commands.FOCUS) -> ActionRoutines.routineFocus(utterance)
            else -> return false
        }

        return true
    }
}
