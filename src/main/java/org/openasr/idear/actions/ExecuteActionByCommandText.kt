package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.IdeActions.ACTION_EDITOR_NEXT_TEMPLATE_VARIABLE
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedAction
import org.openasr.idear.actions.recognition.*
import org.openasr.idear.ide.*

abstract class ExecuteActionByCommandText : IdearAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor = IDEService.getEditor(e.dataContext)!!

        //        String text = "idea extract to field";
        //        String text = "idea extract to variable name";
        //        String text = "idea inline";

        val text = "surround with not null check"

        val provider = TextToActionConverter(e.dataContext)
        val info = provider.extractAction(text)
        if (null != info) runInEditor(editor, info)
    }

    protected open fun runInEditor(editor: Editor, info: ActionCallInfo) =
            editor.dataContext.let { context ->
                ActionManager.getInstance().getAction(info.actionId).run {
                    actionPerformed(buildActionEvent(info, this, context))
                }

                info.typeAfter?.let { typeText(editor, it, context) }

                if (info.hitTabAfter) hitTab(context)
            }

    private fun hitTab(context: DataContext) {
        val action = ActionManager.getInstance().getAction(ACTION_EDITOR_NEXT_TEMPLATE_VARIABLE)
        action.actionPerformed(AnActionEvent(null, context, "", action.templatePresentation, ActionManager.getInstance(), 0))
    }

    private fun typeText(editor: Editor, type: String, context: DataContext) {
        // TODO: getTypedAction() is deprecated, but this alternative is only supported in #193 (2019.3)
        //       Currently we're supporting <idea-version since-build="131"/> which is pre-2016
//        val typedAction = TypedAction.getInstance();
        @Suppress("DEPRECATION")
        val typedAction = EditorActionManager.getInstance().typedAction
        for (c in type.toCharArray()) typedAction.actionPerformed(editor, c, context)
    }

    override fun update(e: AnActionEvent) =
            IDEService.getEditor(e.dataContext)?.run { e.presentation.isEnabled = true } ?: Unit

    private fun buildActionEvent(info: ActionCallInfo, action: AnAction, context: DataContext): AnActionEvent =
            if (info.actionEvent != null) info.actionEvent as AnActionEvent
            else AnActionEvent(null, context, "", action.templatePresentation, ActionManager.getInstance(), 0)
}
