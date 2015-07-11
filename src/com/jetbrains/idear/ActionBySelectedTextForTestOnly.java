package com.jetbrains.idear;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;

public class ActionBySelectedTextForTestOnly extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        String text = "idea extract variable";

        ParsedActionInfoProvider provider = new ParsedActionInfoProvider(text);
        Action action = provider.getAction();
        if (action == null) return;

        if (action.getId().length() > 0) {
            invokeActionById(editor, action.getId());
        }
    }


    private void invokeActionById(Editor editor, String action) {
        AnAction inline = ActionManager.getInstance().getAction(action);

        DataManager manager = DataManager.getInstance();
        if (manager != null) {
            DataContext context = manager.getDataContext(editor.getContentComponent());
            inline.actionPerformed(new AnActionEvent(null, context, "", inline.getTemplatePresentation(), ActionManager.getInstance(), 0));
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
