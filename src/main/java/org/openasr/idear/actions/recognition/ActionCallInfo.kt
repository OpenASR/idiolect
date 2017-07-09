package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.AnActionEvent

class ActionCallInfo(val actionId: String) {
    var typeAfter: String? = null
    var hitTabAfter: Boolean = false
    var actionEvent: AnActionEvent? = null
}
