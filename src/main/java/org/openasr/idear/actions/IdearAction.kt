package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import org.openasr.idear.ide.IDEService.invokeAction

abstract open class IdearAction : AnAction() {
    fun invoke() = invokeAction(ActionManager.getInstance().getId(this))
}