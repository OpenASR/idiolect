package com.jetbrains.idear.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.jetbrains.idear.actions.recognition.ActionCallInfo;
import com.jetbrains.idear.actions.recognition.TextToActionConverter;

public class ExecuteActionFromPredefinedText extends ExecuteActionByCommandText {

    @Override
    public void actionPerformed(AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);

//        String text = "idea extract to field";
        String text = "idea rename to my super test";
//        String text = "idea inline";

        TextToActionConverter provider = new TextToActionConverter(e.getDataContext());
        ActionCallInfo info = provider.extractAction(text);
        invoke(editor, info);
    }
}
