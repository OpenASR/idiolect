package org.openasr.idear

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import org.junit.Assert.*
import org.junit.Test
import org.openasr.idear.actions.recognition.ExtractActionRecognizer

class ActionRecognizerTest {
    private val emptyDataContext: DataContext
        get() = SimpleDataContext.getSimpleContext(DataKey.create(""), Any())

    @Test
    fun test_extract_with_name() {
        val text = "idea extract to variable myTest"

        val recognizer = ExtractActionRecognizer()
        assertTrue(recognizer.isMatching(text))

        val info = recognizer.getActionInfo(text, emptyDataContext)
        assertEquals("IntroduceVariable", info!!.actionId)
        assertEquals("myTest", info.typeAfter)
    }

    @Test
    fun test_extract_without_name() {
        val text = "idea extract to variable"
        val recognizer = ExtractActionRecognizer()
        assertTrue(recognizer.isMatching(text))

        val info = recognizer.getActionInfo(text, emptyDataContext)
        assertEquals("IntroduceVariable", info!!.actionId)
    }
}
