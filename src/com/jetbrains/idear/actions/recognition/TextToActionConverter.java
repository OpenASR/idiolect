package com.jetbrains.idear.actions.recognition;

import org.jetbrains.annotations.NotNull;


public class TextToActionConverter {

    public ActionCallInfo extractAction(@NotNull String sentence) {
        ExtractActionRecognizer recognizer = new ExtractActionRecognizer();
        return recognizer.getActionInfo(sentence);
    }

}


