package com.jetbrains.idear.actions.recognition;

import com.intellij.openapi.actionSystem.AnAction;
import com.jetbrains.idear.ParserService;
import opennlp.tools.parser.Parse;
import org.jetbrains.annotations.NotNull;

import static com.jetbrains.idear.actions.recognition.NodeMatcher.*;

public class TextToActionConverter {
    private final ParserService myService;

    private static final CompositeActionNode myActionsTreeRoot = new CompositeActionNode(matchAll());

    static {
        LeafActionNode inline = new LeafActionNode(newHeadMatcher("inline"), "Inline");
        myActionsTreeRoot.addChild(inline);

        CompositeActionNode extract = new CompositeActionNode(newHeadMatcher("extract"));
        extract.addChild(new LeafActionNode(newNounInPPMatcher("variable"), "IntroduceVariable"));
        extract.addChild(new LeafActionNode(newNounInPPMatcher("field"), "IntroduceField"));

        myActionsTreeRoot.addChild(extract);
    }

    public TextToActionConverter() {
        myService = ParserService.getInstance();
    }

    public AnAction extractAction(@NotNull String sentence) {
        Parse root = myService.parseSentence(sentence);
        return root != null ? myActionsTreeRoot.getAction(root) : null;
    }

}


