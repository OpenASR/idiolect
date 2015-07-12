package com.jetbrains.idear.actions.recognition;

import org.jetbrains.annotations.NotNull;


public class TextToActionConverter {

    public ActionCallInfo extractAction(@NotNull String sentence) {
        for (ActionRecognizer recognizer : ActionRecognizer.EP_NAME.getExtensions()) {
            if (recognizer.isMatching(sentence)) {
                return recognizer.getActionInfo(sentence);
            }
        }
        return null;
    }

}


