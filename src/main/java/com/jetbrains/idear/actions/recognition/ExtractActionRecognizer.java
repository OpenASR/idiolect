package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import freemarker.template.utility.StringUtil;
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

        ActionCallInfo info = new ActionCallInfo(data.first);
        int index = data.second;

        StringBuilder newName = new StringBuilder();
        int newNameStartIndex = index + 1 < words.length && words[index + 1].equals("to")
                ? index + 2
                : index + 1;

        boolean first = true;
        for (int i = newNameStartIndex; i < words.length; i++) {
            String word = first ? words[i] : StringUtil.capitalize(words[i]);
            newName.append(word);
            first = false;
        }

        if (newName.length() > 0) {
            info.setTypeAfter(newName.toString());
            info.setHitTabAfter(true);
        }

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
