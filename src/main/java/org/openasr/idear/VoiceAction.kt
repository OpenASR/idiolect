package com.jetbrains.idear

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.jetbrains.idear.actions.recognition.ActionCallInfo
import com.jetbrains.idear.actions.recognition.TextToActionConverter

class VoiceAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = CommonDataKeys.EDITOR.getData(dataContext)

        val provider = TextToActionConverter(e.dataContext)
        val callInfo = provider.extractAction(e.getData(KEY)!!)!!
        invoke(editor!!, callInfo)
    }

    private operator fun invoke(editor: Editor, info: ActionCallInfo) {
        val action = ActionManager.getInstance().getAction(info.actionId)

        val type = info.typeAfter
        val isHitTabAfter = info.hitTabAfter

        val manager = DataManager.getInstance()
        if (manager != null) {
            val context = manager.getDataContext(editor.contentComponent)
            action.actionPerformed(AnActionEvent(null, context, "", action.templatePresentation, ActionManager.getInstance(), 0))

            if (type != null) {
                typeText(editor, type, context)
            }

            if (isHitTabAfter) {
                hitTab(context)
            }
        }
    }

    private fun hitTab(context: DataContext) {
        val action = ActionManager.getInstance().getAction("NextTemplateVariable")
        action.actionPerformed(AnActionEvent(null, context, "", action.templatePresentation, ActionManager.getInstance(), 0))
    }

    private fun typeText(editor: Editor, type: String, context: DataContext) {
        val typing = EditorActionManager.getInstance().typedAction
        for (c in type.toCharArray()) {
            typing.actionPerformed(editor, c, context)
        }
    }

    override fun update(event: AnActionEvent?) {
        val dataContext = event!!.dataContext
        val editor = CommonDataKeys.EDITOR.getData(dataContext)

        if (editor != null) {
            val presentation = event.presentation
            presentation.isEnabled = true
        }
    }

    companion object {
        /* package */ private val KEY = DataKey.create<String>("Idear.VoiceCommand.Text")
    }
}
