package org.openasr.idear.nlp.handlers

import com.intellij.openapi.actionSystem.IdeActions
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.Commands

class KeyboardHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            // "press delete|return|enter|escape|tab|undo|shift"
            utterance.startsWith(Commands.PRESS) -> ActionRoutines.routinePress(utterance)
//            "enter " in u -> routineEnter(u)

            // "release shift"
            utterance.startsWith("release") -> ActionRoutines.routineReleaseKey(utterance)

            // "following line|page|method|tab|word"
            utterance.startsWith("following") -> ActionRoutines.routineFollowing(utterance)
            // "grow|shrink" - manipulates selection
            utterance.startsWith(Commands.GROW) -> IdeService.invokeAction(IdeActions.ACTION_EDITOR_SELECT_WORD_AT_CARET)
            utterance.startsWith(Commands.SHRINK) -> IdeService.invokeAction(IdeActions.ACTION_EDITOR_UNSELECT_WORD_AT_CARET)

            else -> return false
        }

        return true
    }
}
