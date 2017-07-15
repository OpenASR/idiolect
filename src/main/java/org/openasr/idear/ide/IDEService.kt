package org.openasr.idear.ide

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.ui.playback.commands.ActionCommand

object IDEService {
    /**
     * @param action - see [com.intellij.openapi.actionSystem.IdeActions]
     */
    fun invokeAction(actionId: String) =
        with(ActionManager.getInstance()) {
            tryToExecute(getAction(actionId), ActionCommand.getInputEvent(actionId), null, null, true)
        }

    fun type(vararg keys: Int) = Keyboard.type(*keys)

    fun pressShift() = Keyboard.pressShift()

    fun releaseShift() = Keyboard.releaseShift()

    fun type(vararg keys: Char) = Keyboard.type(*keys)

    fun type(string: String) = Keyboard.type(string)
}
