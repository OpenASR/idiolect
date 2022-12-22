package org.openasr.idear.nlp.handlers

import com.intellij.openapi.actionSystem.IdeActions
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.Commands

class FileNavigationHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            // "beginning|end of line"
            utterance.endsWith("of line") -> ActionRoutines.routineOfLine(utterance)
            // "go to line $number"
            utterance.startsWith(Commands.GOTO) -> ActionRoutines.routineGoto(utterance)
            // "navigate" -> goes to declaration of highlighted field/function
            utterance.startsWith(Commands.NAVIGATE) -> IdeService.invokeAction(IdeActions.ACTION_GOTO_DECLARATION)
            // "focus editor|project"
            // "focus symbols" activates AceJump and waits for "jump $number"
            utterance.startsWith(Commands.FOCUS) -> ActionRoutines.routineFocus(utterance)

            // "find in file|path"
            utterance.startsWith("find in") -> ActionRoutines.routineFind(utterance)
            else -> return false
        }

        return true
    }
}
