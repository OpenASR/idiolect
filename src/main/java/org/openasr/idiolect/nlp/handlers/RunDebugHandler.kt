package org.openasr.idiolect.nlp.handlers

import com.intellij.openapi.actionSystem.IdeActions.*
import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.Commands

class RunDebugHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            // "toggle|view break point"
            "break point" in utterance -> ActionRoutines.routineHandleBreakpoint(utterance)
            // "execute|debug" -> runs the code
            utterance.startsWith(Commands.EXECUTE) -> IdeService.invokeAction(ACTION_DEFAULT_RUNNER)
            utterance.startsWith(Commands.DEBUG) -> IdeService.invokeAction(ACTION_DEFAULT_DEBUGGER)
                // IDEService.type(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_F9)
            // "step over|into|return"
            utterance.startsWith("step") -> ActionRoutines.routineStep(utterance)
            // "resume"
            utterance.startsWith("resume") -> IdeService.invokeAction("Resume")
            else -> return false
        }

        return true
    }
}
