package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.AnActionEvent

class ActionCallInfo(val actionId: String) {
    var typeAfter: String? = null
    var hitTabAfter = false
    var actionEvent: AnActionEvent? = null

    companion object {
        /**
         * Used to indicate that the action has been performed,
         * eg by ActionRoutines.
         */
        val RoutineActioned = ActionCallInfo("RoutineActioned")
    }
}
