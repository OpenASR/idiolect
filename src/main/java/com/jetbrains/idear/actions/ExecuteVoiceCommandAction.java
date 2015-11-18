package com.jetbrains.idear.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.editor.Editor;
import com.jetbrains.idear.actions.recognition.ActionCallInfo;
import com.jetbrains.idear.actions.recognition.TextToActionConverter;

import java.util.logging.Logger;

public class ExecuteVoiceCommandAction extends ExecuteActionByCommandText {
    private static final Logger logger = Logger.getLogger(ExecuteVoiceCommandAction.class.getSimpleName());
    public final static DataKey<String> KEY = DataKey.create("Idear.VoiceCommand.Text");

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

        TextToActionConverter provider = new TextToActionConverter(e.getDataContext());
        ActionCallInfo info = provider.extractAction(e.getData(KEY));
        if (info != null) {
            invoke(editor, info);
        }
        else {
            logger.info("Command not recognized");
        }
    }

}
