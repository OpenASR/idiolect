package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.AnAction;
import opennlp.tools.parser.Parse;
import org.jetbrains.annotations.NotNull;

public abstract class ActionNode {
    private final NodeMatcher myMatcher;

    public ActionNode(@NotNull NodeMatcher matcher) {
        myMatcher = matcher;
    }

    abstract boolean isLeaf();

    public boolean isMatching(@NotNull Parse node) {
        return myMatcher.isMatching(node);
    }

    public abstract AnAction getAction(@NotNull Parse node);
}
