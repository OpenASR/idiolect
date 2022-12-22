package org.openasr.idear.nlp.handlers

import org.openasr.idear.actions.ActionRoutines

class JavaHandler : UtteranceHandler {
    override fun processUtterance(utterance: String): Boolean {
        when {
            "public static void main" in utterance -> ActionRoutines.routinePsvm()
            "print line" in utterance -> ActionRoutines.routinePrintln()
            // "create new class (optional name)"
            "new class" in utterance -> ActionRoutines.routineAddNewClass(utterance)
            else -> return false
        }

        return true
    }
}
