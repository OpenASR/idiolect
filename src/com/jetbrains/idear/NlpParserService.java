package com.jetbrains.idear;

import com.intellij.util.SmartList;
import com.intellij.util.containers.ContainerUtil;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class NlpParserService {
    private Parser parser;

    public NlpParserService() {
        initParser();
    }

    @Nullable
    public Parse parseSentence(String sentence) throws IOException {
        if (parser == null) {
            throw new IllegalStateException();
        }
        Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
        assert topParses.length == 1;
        return topParses[0];
    }

    private void initParser() {
        ParserModel model = readParserModel();
        if (model == null) return;
        parser = ParserFactory.create(model);
    }

    @Nullable
    private ParserModel readParserModel() {
        ParserModel model = null;
        try {
            InputStream is = new FileInputStream("en-parser-chunking.bin");
            model = new ParserModel(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    public List<Parse> findLeafVerbs(@NotNull Parse root) {
        if (root.getChildCount() == 0) {
            if (root.getParent().getType().startsWith("VB")) {
                return ContainerUtil.newSmartList(root.getParent());
            }
            return ContainerUtil.emptyList();
        }

        List<Parse> verbs = new SmartList<>();
        for (Parse child : root.getChildren()) {
            verbs.addAll(findLeafVerbs(child));
        }

        return verbs;
    }


    public static void main(String[] args) {
        try {
            NlpParserService nlpParserService = new NlpParserService();

            Parse parse = nlpParserService.parseSentence("idea find usages of class X");
            System.out.println(parse);


            List<Parse> leafVerbs = nlpParserService.findLeafVerbs(parse);

            System.out.println();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}




