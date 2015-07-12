package com.jetbrains.idear;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.jetbrains.idear.actions.recognition.ActionCallInfo;
import com.jetbrains.idear.actions.recognition.ExtractActionRecognizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ActionRecognizerTest {

    @Test
    public void test_extract_with_name() throws Exception {
        String text = "idea extract to variable myTest";

        ExtractActionRecognizer recognizer = new ExtractActionRecognizer();
        assertTrue(recognizer.isMatching(text));

        ActionCallInfo info = recognizer.getActionInfo(text, getEmptyDataContext());
        assertEquals("IntroduceVariable", info.getActionId());
        assertEquals("myTest", info.getTypeAfter());
    }

    @Test
    public void test_extract_without_name() throws Exception {
        String text = "idea extract to variable";
        ExtractActionRecognizer recognizer = new ExtractActionRecognizer();
        assertTrue(recognizer.isMatching(text));

        ActionCallInfo info = recognizer.getActionInfo(text, getEmptyDataContext());
        assertEquals("IntroduceVariable", info.getActionId());
    }

    private static DataContext getEmptyDataContext() {
        return SimpleDataContext.getSimpleContext("", null);
    }
}
