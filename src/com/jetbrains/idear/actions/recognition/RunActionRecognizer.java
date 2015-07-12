package com.jetbrains.idear.actions.recognition;

import org.jetbrains.annotations.NotNull;

//runs only selected configuration
public class RunActionRecognizer implements ActionRecognizer {

    @Override
    public boolean isMatching(@NotNull String sentence) {
        return sentence.contains("run");
    }

    @Override
    public ActionCallInfo getActionInfo(@NotNull String sentence) {
        return new ActionCallInfo("Run", null);
    }
}
