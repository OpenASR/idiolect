package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.DataContext;
import org.jetbrains.annotations.NotNull;

public class RenameActionRecognizer implements ActionRecognizer {

    @Override
    public boolean isMatching(@NotNull String sentence) {
        return sentence.contains("rename");
    }

    @Override
    public ActionCallInfo getActionInfo(@NotNull String sentence, DataContext dataContext) {
        if (!isMatching(sentence)) return null;

        String[] words = sentence.split(" ");
        int renameIndex = 0;
        for (int i = 0; i < words.length; i++) {
            if (words[i].contains("rename")) {
                renameIndex = i;
                break;
            }
        }

        StringBuilder newName = new StringBuilder();
        boolean first = true;
        for (int i = renameIndex + 2; i < words.length; i++) {
            String word = first ? words[i] : words[i].toUpperCase();
            newName.append(word);
            first = false;
        }


        ActionCallInfo info = new ActionCallInfo("RenameElement");
        if (newName.length() > 0) {
            info.setTypeAfter(newName.toString());
            info.setHitTabAfter(true);
        }

        return info;
    }
}
