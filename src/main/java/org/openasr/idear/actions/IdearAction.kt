package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.*
import org.openasr.idear.ide.IDEService.invokeAction

abstract open class IdearAction : AnAction() {
    fun invoke() = invokeAction(ActionManager.getInstance().getId(this))
}