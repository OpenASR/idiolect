package org.openasr.idiolect.nlp.handlers

import org.openasr.idiolect.actions.ActionRoutines

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
