package org.openasr.idiolect.nlp.handlers

import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.Commands

class CodeHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            // "extract this method|parameter"
            utterance.startsWith("extract this") -> ActionRoutines.routineExtract(utterance)
            // opens "Problems" panel and shows inspection results
            utterance.startsWith("inspect code") -> IdeService.invokeAction("CodeInspection.OnEditor")
            // "show usages" of the function
            utterance == Commands.SHOW_USAGES -> IdeService.invokeAction("ShowUsages")

            // "surround with not null check"
//            "check" in utterance -> ActionRoutines.routineCheck(utterance)
            else -> return false
        }

        return true
    }
}
