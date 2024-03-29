package org.openasr.idiolect.ide

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.playback.commands.ActionCommand
import com.intellij.openapi.util.ActionCallback
import org.openasr.idiolect.actions.SpeechEvent
import org.openasr.idiolect.nlp.NlpRequest
import javax.swing.JOptionPane

object IdeService {
    /**
     * @param actionId - see [com.intellij.openapi.actionSystem.IdeActions]
     */
    fun invokeAction(actionId: String): ActionCallback =
        with(ActionManager.getInstance()) {
            var callback: ActionCallback? = null
            ApplicationManager.getApplication().invokeAndWait {
                callback = tryToExecute(getAction(actionId),
                        ActionCommand.getInputEvent(actionId), null, null, true)
            }
            return callback!!
        }

    fun invokeAction(action: AnAction): ActionCallback =
        with(ActionManager.getInstance()) {
            var callback: ActionCallback? = null
            ApplicationManager.getApplication().invokeAndWait {
                callback = tryToExecute(action,
                        ActionCommand.getInputEvent(null), null, null, true)
            }
            return callback!!
        }

    fun invokeAction(action: AnAction, nlpRequest: NlpRequest): ActionCallback {
        with(ActionManager.getInstance()) {
            var callback: ActionCallback? = null
            ApplicationManager.getApplication().invokeAndWait {
                callback = tryToExecute(
                    action,
                    SpeechEvent(JOptionPane.getRootFrame(), 0, System.currentTimeMillis(), nlpRequest),
                    null, "microphone", true
                )
            }
            return callback!!
        }
    }

    fun type(vararg keys: Int) = Keyboard.type(*keys)

    fun pressShift() = Keyboard.pressShift()

    fun releaseShift() = Keyboard.releaseShift()

    fun type(vararg keys: Char) = Keyboard.type(*keys)

    fun type(string: String) = Keyboard.type(string)

    fun getEditor(dataContext: DataContext? = null): Editor? {
        var editor: Editor? = null
        if (dataContext != null) {
            editor = CommonDataKeys.EDITOR.getData(dataContext)
        }

        if (editor == null) {
            editor = FileEditorManager.getInstance(ProjectManager.getInstance().openProjects[0]).run {
                selectedTextEditor ?: allEditors.firstOrNull { it is Editor } as Editor?
            }
        }

        return editor
    }

    fun getProject(dataContext: DataContext? = null) = getEditor(dataContext)?.project
}
