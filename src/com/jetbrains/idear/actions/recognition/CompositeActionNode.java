package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.util.containers.ContainerUtil;
import opennlp.tools.parser.Parse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CompositeActionNode extends ActionNode {
    private List<ActionNode> children = ContainerUtil.newArrayList();

    public CompositeActionNode(@NotNull NodeMatcher matcher) {
        super(matcher);
    }

    public void addChild(@NotNull ActionNode node) {
        children.add(node);
    }

    @Override
    boolean isLeaf() {
        return false;
    }

    @Nullable
    public AnAction getAction(@NotNull Parse root) {
        Optional<ActionNode> matched = children
                .stream()
                .filter(child -> child.isMatching(root))
                .findFirst();

        return matched.isPresent() ? matched.get().getAction(root) : null;
    }
}
