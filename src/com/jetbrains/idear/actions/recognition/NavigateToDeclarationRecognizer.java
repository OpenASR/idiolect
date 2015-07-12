package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.DataContext;
import org.jetbrains.annotations.NotNull;

public class NavigateToDeclarationRecognizer implements ActionRecognizer {

    @Override
    public boolean isMatching(@NotNull String sentence) {
        return sentence.contains("navigate");
    }

    @Override
    public ActionCallInfo getActionInfo(@NotNull String sentence, DataContext dataContext) {
        return new ActionCallInfo("GotoDeclaration");
    }
}
