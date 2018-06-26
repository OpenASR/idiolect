package org.openasr.idear

import org.junit.*
import org.junit.Assert.*
import org.openasr.idear.nlp.NlpParserService

class NlpParserTest {
    @Test
    fun testExtractFind_Class() = doTestExtractAction("Idea find usage of class action", "find")

    @Test
    fun testExtractFind_Method() = doTestExtractAction("Idea find usage of method run", "find")

    @Test
    fun testExtractGoTo_Class() = doTestExtractAction("Idea go to class CodeStyleSettings", "go")

    @Test
    fun testExtractGoTo_Method() = doTestExtractAction("Idea go to method apply", "go")

    @Test
    fun testExtractExtract_Variable() = doTestExtractAction("Idea extract to variable x", "extract")

    @Test
    fun testExtractExtract_Variable_WithoutName() = doTestExtractAction("Idea extract to variable", "extract")

    @Test
    fun testExtractExtract_Field_WithoutName() = doTestExtractAction("Idea extract to field", "extract")

    @Test
    fun testExtractExtract_Field() = doTestExtractAction("Idea extract to field x", "extract")

    @Test
    fun testExtractIntroduce_Field() = doTestExtractAction("Idea introduce field", "introduce")

    @Test
    fun testInline_Field() = doTestExtractAction("Idea inline", "inline")

    private fun doTestExtractAction(sentence: String, action: String) {
        val root = myParser!!.parseSentence(sentence)
        assertNotNull(root)
        val head = root!!.children[0].head
        assertEquals(head.coveredText, action)
    }

    companion object {
        private var myParser: NlpParserService? = null

        @BeforeClass
        @JvmStatic
        fun setUp() {
            myParser = NlpParserService(null) //"en-parser-chunking.bin")
            myParser!!.init()
        }
    }

}
