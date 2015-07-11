package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import opennlp.tools.parser.Parse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class LeafActionNode extends ActionNode {
    private final String myActionId;

    public LeafActionNode(@NotNull NodeMatcher matcher, @NotNull String actionId) {
        super(matcher);
        myActionId = actionId;
    }

    @Override
    boolean isLeaf() {
        return true;
    }

    @Nullable
    public AnAction getAction(@NotNull Parse node) {
        if (isMatching(node)) {
            return ActionManager.getInstance().getAction(myActionId);
        }
        return null;
    }
}
