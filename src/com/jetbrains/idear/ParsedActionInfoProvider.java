package com.jetbrains.idear;

import com.intellij.util.containers.ContainerUtil;
import opennlp.tools.parser.Parse;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Figure out what action to invoke by given text
 */
public class ParsedActionInfoProvider {
    private final ParserService myService;
    private final Parse myTreeRoot;

    public ParsedActionInfoProvider(String text) {
        myService = ParserService.getInstance();
        myTreeRoot = myService.parseSentence(text);
    }

    @Nullable
    public Action getAction() {
        Parse parse = myTreeRoot.getChildren()[0];
        String headText = parse.getHead().getCoveredText();
        for (Action action : Action.values()) {
            if (action.getName().equals(headText)) return action;
        }
        return null;
    }


    public static List<Parse> collectAllNodesOfType(Parse root, String type) {
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


}


enum Action {

    EXTRACT_VARIABLE("extract", "IntroduceVariable"),
    FIND("find", ""),
    INLINE("inline", "Inline"),
    GOTO("goto", "");

    private final String myActionId;
    private final String myName;

    Action(String name, String actionId) {
        myName = name;
        myActionId = actionId;
    }

    String getId() {
        return myActionId;
    }

    public String getName() {
        return myName;
    }
}
