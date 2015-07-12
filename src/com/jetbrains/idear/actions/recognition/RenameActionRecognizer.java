package com.jetbrains.idear.actions.recognition;

import org.jetbrains.annotations.NotNull;

//not implmented yet
public class RenameActionRecognizer implements ActionRecognizer {

    @Override
    public boolean isMatching(@NotNull String sentence) {
        return sentence.contains("rename");
    }

    @Override
    public ActionCallInfo getActionInfo(@NotNull String sentence) {
        if (!isMatching(sentence)) return null;
        return null;
    }
}
