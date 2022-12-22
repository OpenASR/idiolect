package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.AnActionEvent

class ActionCallInfo(val actionId: String, var fullfilled: Boolean = false) {
    var typeAfter: String? = null
    var hitTabAfter = false
    var actionEvent: AnActionEvent? = null
}
