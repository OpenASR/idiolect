package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.IdeActions.ACTION_EDITOR_NEXT_TEMPLATE_VARIABLE
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedAction
import org.openasr.idear.actions.recognition.*
import org.openasr.idear.ide.*

abstract class ExecuteActionByCommandText : IdearAction() {
    private val log = logger<ExecuteActionByCommandText>()

    override fun update(e: AnActionEvent) =
            IdeService.getEditor(e.dataContext)?.run { e.presentation.isEnabled = true } ?: Unit

    protected open fun runInEditor(editor: Editor, info: ActionCallInfo) {
        log.info("Invoking in editor: ${info.actionId} ${if (info.actionEvent != null) "with" else "without"} actionEvent")

        editor.dataContext.let { context ->
            ActionManager.getInstance().getAction(info.actionId).run {
                actionPerformed(buildActionEvent(info, this, context))
            }

            info.typeAfter?.let { typeText(editor, it, context) }

            if (info.hitTabAfter) hitTab(context)
        }
    }

    private fun hitTab(context: DataContext) {
        val action = ActionManager.getInstance().getAction(ACTION_EDITOR_NEXT_TEMPLATE_VARIABLE)
        action.actionPerformed(AnActionEvent(null, context, "", action.templatePresentation, ActionManager.getInstance(), 0))
    }

    private fun typeText(editor: Editor, type: String, context: DataContext) {
        type.forEach { TypedAction.getInstance().actionPerformed(editor, it, context) }
    }

    private fun buildActionEvent(info: ActionCallInfo, action: AnAction, context: DataContext): AnActionEvent =
            if (info.actionEvent != null) info.actionEvent as AnActionEvent
            else AnActionEvent(null, context, "", action.templatePresentation, ActionManager.getInstance(), 0)
}
