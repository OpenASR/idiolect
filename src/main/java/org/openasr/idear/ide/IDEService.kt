package org.openasr.idear.ide

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.playback.commands.ActionCommand
import com.intellij.openapi.util.ActionCallback

object IDEService {
    /**
     * @param action - see [com.intellij.openapi.actionSystem.IdeActions]
     */
    fun invokeAction(actionId: String): ActionCallback =
            with(ActionManager.getInstance()) {
                var t: ActionCallback? = null
                ApplicationManager.getApplication().invokeAndWait {
                    t = tryToExecute(getAction(actionId), ActionCommand.getInputEvent(actionId), null, null, true)
                }
                return t!!
            }

    fun type(vararg keys: Int) = Keyboard.type(*keys)

    fun pressShift() = Keyboard.pressShift()

    fun releaseShift() = Keyboard.releaseShift()

    fun type(vararg keys: Char) = Keyboard.type(*keys)

    fun type(string: String) = Keyboard.type(string)
}
