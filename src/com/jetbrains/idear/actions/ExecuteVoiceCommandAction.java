package com.jetbrains.idear.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.editor.Editor;
import com.jetbrains.idear.actions.ExecuteActionByCommandText;
import com.jetbrains.idear.actions.recognition.TextToActionConverter;

public class ExecuteVoiceCommandAction extends ExecuteActionByCommandText {

    public final static DataKey<String> KEY = DataKey.create("Idear.VoiceCommand.Text");

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        TextToActionConverter provider = new TextToActionConverter(e.getDataContext());
        invoke(editor, provider.extractAction(e.getData(KEY)));
    }

}
