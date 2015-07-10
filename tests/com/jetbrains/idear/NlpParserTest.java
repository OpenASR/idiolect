package com.jetbrains.idear;

import opennlp.tools.parser.Parse;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NlpParserTest {
    private static NlpParserService myParser;

    @BeforeClass
    public static void setUp() throws Exception {
        myParser = new NlpParserService();
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
        assertEquals(head.getCoveredText(), action);
    }

}
