package org.openasr.idear.ide

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.util.Consumer
import java.awt.EventQueue

object IDEService {
    val defaultActionFactory = { dataContext: DataContext ->
        AnActionEvent(null, dataContext, ActionPlaces.UNKNOWN, Presentation(), ActionManager.getInstance(), 0)
    }

    fun invokeAction(action: String, actionFactory: (DataContext) -> AnActionEvent = defaultActionFactory) =
            DataManager.getInstance().dataContextFromFocus.doWhenDone(Consumer<DataContext> { dataContext: DataContext ->
                EventQueue.invokeLater {
                    ActionManager.getInstance().getAction(action).actionPerformed(actionFactory.invoke(dataContext))
                } })

    fun type(vararg keys: Int) {
        Keyboard.type(*keys)
    }

    fun pressShift() {
        Keyboard.pressShift()
    }

    fun releaseShift() {
        Keyboard.releaseShift()
    }

    fun type(vararg keys: Char) {
        Keyboard.type(*keys)
    }

    fun type(string: String) {
        Keyboard.type(string)
    }
}
