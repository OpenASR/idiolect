package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.DataContext;
import org.jetbrains.annotations.NotNull;


public class TextToActionConverter {

    private final DataContext dataContext;

    public TextToActionConverter(DataContext dataContext) {
        this.dataContext = dataContext;
    }

    public ActionCallInfo extractAction(@NotNull String sentence) {
        for (ActionRecognizer recognizer : ActionRecognizer.EP_NAME.getExtensions()) {
            if (recognizer.isMatching(sentence)) {
                return recognizer.getActionInfo(sentence, dataContext);
            }
        }
        return null;
    }

}


