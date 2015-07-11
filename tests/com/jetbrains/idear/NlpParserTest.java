package com.jetbrains.idear;

import com.intellij.util.containers.ContainerUtil;
import opennlp.tools.parser.Parse;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NlpParserTest {
    private static NlpParserService myParser;

    @BeforeClass
    public static void setUp() throws Exception {
        myParser = new NlpParserService("en-parser-chunking.bin");
        myParser.init();
    }

    @Test
    public void testExtractFind_Class() throws Exception {
        doTestExtractAction("Idea find usage of class action", "find");
    }

    @Test
    public void testExtractFind_Method() throws Exception {
        doTestExtractAction("Idea find usage of method run", "find");
    }

    @Test
    public void testExtractGoTo_Class() throws Exception {
        doTestExtractAction("Idea go to class CodeStyleSettings", "go");
    }

    @Test
    public void testExtractGoTo_Method() throws Exception {
        doTestExtractAction("Idea go to method apply", "go");
    }

    @Test
    public void testExtractExtract_Variable() throws Exception {
        doTestExtractAction("Idea extract to variable x", "extract");
    }

    @Test
    public void testExtractExtract_Variable_WithoutName() throws Exception {
        doTestExtractAction("Idea extract to variable", "extract");
    }

    @Test
    public void testExtractExtract_Field_WithoutName() throws Exception {
        doTestExtractAction("Idea extract to field", "extract");
    }

    @Test
    public void testExtractExtract_Field() throws Exception {
        doTestExtractAction("Idea extract to field x", "extract");
    }

    @Test
    public void testInline_Field() throws Exception {
        doTestExtractAction("Idea inline", "inline");
    }

    private void doTestExtractAction(String sentence, String action) throws IOException {
        Parse root = myParser.parseSentence(sentence);
        assertNotNull(root);
        Parse head = root.getChildren()[0].getHead();

        List<Parse> a = ContainerUtil.newArrayList();
        for (Parse node : ParsedActionInfoProvider.collectAllNodesOfType(root, "PP")) {
            a.addAll(ParsedActionInfoProvider.collectAllNodesOfType(node, "N"));
        }

        assertEquals(head.getCoveredText(), action);
    }

}
