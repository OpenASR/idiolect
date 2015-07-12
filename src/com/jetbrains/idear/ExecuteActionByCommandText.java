package com.jetbrains.idear;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.editor.actionSystem.TypedAction;
import com.jetbrains.idear.actions.recognition.ActionCallInfo;

public abstract class ExecuteActionByCommandText extends AnAction {

    protected void invoke(Editor editor, ActionCallInfo info) {
        AnAction action = ActionManager.getInstance().getAction(info.actionId);
        String type = info.typeAfter;

        DataManager manager = DataManager.getInstance();
        if (manager != null) {
            DataContext context = manager.getDataContext(editor.getContentComponent());
            action.actionPerformed(new AnActionEvent(null, context, "", action.getTemplatePresentation(), ActionManager.getInstance(), 0));

            TypedAction typing = EditorActionManager.getInstance().getTypedAction();
            for (char c : type.toCharArray()) {
                typing.actionPerformed(editor, c, context);
            }

            action = ActionManager.getInstance().getAction("NextTemplateVariable");
            action.actionPerformed(new AnActionEvent(null, context, "", action.getTemplatePresentation(), ActionManager.getInstance(), 0));
        }
    }

    @Override
    public void update(AnActionEvent event) {
        DataContext dataContext = event.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        if (editor != null) {
            Presentation presentation = event.getPresentation();
            presentation.setEnabled(true);
        }
    }

}
