package com.jetbrains.idear.actions.recognition;

import com.intellij.util.containers.ContainerUtil;
import opennlp.tools.parser.Parse;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface NodeMatcher {
    boolean isMatching(@NotNull Parse parse);

    static List<Parse> collectAllNodesOfType(Parse root, String type) {
        List<Parse> matching = ContainerUtil.newSmartList();
        for (Parse node : root.getChildren()) {
            String nodeType = node.getType();
            if (nodeType != null && nodeType.startsWith(type)) {
                matching.add(node);
            }
            else {
                matching.addAll(collectAllNodesOfType(node, type));
            }
        }
        return matching;
    }

    static NodeMatcher newHeadMatcher(@NotNull String head) {
        return (parse) -> {
            String parsedHead = parse.getChildren()[0].getHead().getCoveredText();
            return head.equals(parsedHead);
        };
    }

    static NodeMatcher newNounInPPMatcher(@NotNull String noun) {
        return (root) -> {
            root = root.getChildren()[0];

            List<Parse> matchingNouns = collectAllNodesOfType(root, "PP")
                    .stream()
                    .map((node) -> collectAllNodesOfType(node, "N"))
                    .collect(Collectors.reducing(ContainerUtil.newSmartList(), (List<Parse> all, List<Parse> current) -> {
                        all.addAll(current);
                        return all;
                    }));

            return matchingNouns.stream()
                    .filter((p) -> noun.equals(p.getCoveredText()))
                    .findFirst()
                    .isPresent();
        };
    }

    static NodeMatcher matchAll() {
        return (root) -> true;
    }
}
