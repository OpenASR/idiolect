package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ExtractActionRecognizer implements ActionRecognizer {

    private Set<String> actions = ContainerUtil.newHashSet("extract");

    @Override
    public boolean isMatching(@NotNull String sentence) {
        return actions.stream()
                .filter(sentence::contains)
                .findFirst()
                .isPresent();
    }

    @Override
    @Nullable
    public ActionCallInfo getActionInfo(@NotNull String sentence, DataContext dataContext) {
        if (!isMatching(sentence)) return null;

        String[] words = sentence.split(" ");
        Pair<String, Integer> data = getActionId(words);

        if (data == null) return null;

        int index = data.second;

        String next = null;
        if (index + 1 < words.length) {
            next = words[index + 1];
            if (next.equals("to") && index + 2 < words.length) {
                next = words[index + 2];
            }
        }

        String actionId = data.first;

        ActionCallInfo info = new ActionCallInfo(actionId);
        info.setTypeAfter(next);
        info.setHitTabAfter(true);
        return info;
    }

    private Pair<String, Integer> getActionId(@NotNull String[] words) {
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (word.contains("variable")) {
                return Pair.create("IntroduceVariable", i);
            }
            else if (word.contains("field")) {
                return Pair.create("IntroduceField", i);
            }
        }
        return null;
    }

}
