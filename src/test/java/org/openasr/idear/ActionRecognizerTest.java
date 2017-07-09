package org.openasr.idear;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import org.junit.Test;
import org.openasr.idear.actions.recognition.*;

import static org.junit.Assert.*;

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
