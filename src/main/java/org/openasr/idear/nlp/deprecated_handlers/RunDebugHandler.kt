package org.openasr.idear.nlp.deprecated_handlers

import com.intellij.openapi.actionSystem.IdeActions.*
import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.Commands

class RunDebugHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            // "toggle|view break point"
            "break point" in utterance -> ActionRoutines.routineHandleBreakpoint(utterance)
                // IDEService.type(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_F9)
            // "resume"
            utterance.startsWith("resume") -> IdeService.invokeAction("Resume")
            else -> return false
        }

        return true
    }
}
