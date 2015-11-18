package com.jetbrains.idear.nlp;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class NlpParserService extends ParserService {
    private String path;
    private Parser parser;

    @TestOnly
    public NlpParserService(String testPath) {
        this.path = testPath;
    }

    public NlpParserService() {
    }

    @Nullable
    public Parse parseSentence(String sentence) {
        if (parser == null) {
            throw new IllegalStateException();
        }
        Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
        assert topParses.length == 1;
        return topParses[0];
    }

    public void init() {
        ParserModel model = readParserModel();
        if (model == null) return;
        parser = ParserFactory.create(model);
    }

    @Nullable
    private ParserModel readParserModel() {
        ParserModel model = null;
        try {
            model = new ParserModel(getModelInputStream());
            getModelInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return model;
    }

    private InputStream getModelInputStream() throws FileNotFoundException {
        if (path != null) {
            return new FileInputStream(path);
        }

        PluginId id = PluginId.getId("com.jetbrains.idear");
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(id);

        assert plugin != null;

        ClassLoader classLoader = plugin.getPluginClassLoader();
        return classLoader.getResourceAsStream("en-parser-chunking.bin");
    }

}




