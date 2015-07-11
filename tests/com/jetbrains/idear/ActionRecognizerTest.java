package com.jetbrains.idear;

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

        ActionCallInfo info = recognizer.getActionInfo(text);
        assertEquals("IntroduceVariable", info.actionId);
        assertEquals("myTest", info.typeAfter);
    }

    @Test
    public void test_extract_without_name() throws Exception {
        String text = "idea extract to variable";
        ExtractActionRecognizer recognizer = new ExtractActionRecognizer();
        assertTrue(recognizer.isMatching(text));

        ActionCallInfo info = recognizer.getActionInfo(text);
        assertEquals("IntroduceVariable", info.actionId);
    }
}
