package com.jetbrains.idear.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.jetbrains.idear.actions.recognition.ActionCallInfo;
import com.jetbrains.idear.actions.recognition.TextToActionConverter;

public class ExecuteActionFromPredefinedText extends ExecuteActionByCommandText {

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

//        String text = "idea extract to field";
        String text = "idea extract to variable name";
//        String text = "idea inline";

        TextToActionConverter provider = new TextToActionConverter();
        ActionCallInfo info = provider.extractAction(text);
        invoke(editor, info);
    }
}
