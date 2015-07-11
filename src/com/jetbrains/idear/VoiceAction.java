package com.jetbrains.idear;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.jetbrains.idear.actions.recognition.TextToActionConverter;

public class VoiceAction extends AnAction {

    /* package */ final static DataKey<String> KEY = DataKey.create("Idear.VoiceCommand.Text");

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        TextToActionConverter provider = new TextToActionConverter();
        AnAction anAction = provider.extractAction(e.getData(KEY));
        invoke(editor, anAction);
    }

    private void invoke(Editor editor, AnAction action) {
        DataManager manager = DataManager.getInstance();
        if (manager != null) {
            DataContext context = manager.getDataContext(editor.getContentComponent());
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
