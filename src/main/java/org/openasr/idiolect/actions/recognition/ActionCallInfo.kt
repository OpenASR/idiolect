package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.actionSystem.AnActionEvent

class ActionCallInfo(val actionId: String, var fulfilled: Boolean = false) {
    var typeAfter: String? = null
    var hitTabAfter = false
    var actionEvent: AnActionEvent? = null
}
