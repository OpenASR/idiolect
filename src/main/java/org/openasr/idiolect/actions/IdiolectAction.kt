package org.openasr.idiolect.actions

import com.intellij.openapi.actionSystem.AnAction
import org.openasr.idiolect.ide.IdeService.invokeAction

abstract class IdiolectAction : AnAction() {
    operator fun invoke() = invokeAction(this)
}
