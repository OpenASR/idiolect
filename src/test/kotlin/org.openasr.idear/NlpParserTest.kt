package org.openasr.idear

import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.BeforeClass
import org.junit.Test
import org.openasr.idear.nlp.NlpParserService
import org.vosk.LibVosk
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption

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
            myParser = NlpParserService("src/main/resources/en-parser-chunking.bin")
            myParser!!.init()
        }
    }

    @Test
    fun shouldRegisterDlls () {
        // To get a tmp folder we unpack small library and mark it for deletion
        val tmpFile = Native.extractFromResourcePath("/win32-x86-64/empty")
        val tmpDir = tmpFile.parentFile
        File(tmpDir, tmpFile.name + ".x").createNewFile()

//        System.setProperty("jna.library.path", )
        //NativeLibrary.addSearchPath()

        // Now unpack dependencies
        unpackDll(tmpDir, "libwinpthread-1")
        unpackDll(tmpDir, "libgcc_s_seh-1")
        unpackDll(tmpDir, "libstdc++-6")

//        Native.register(this.javaClass, "C:/my-project/build/idea-sandbox/system/tmp/jna16768340012506130845 - Copy.dll")
//        Native.register(this.javaClass, "libwinpthread-1")  // works
//        Native.register(this.javaClass, "libgcc_s_seh-1")
//        Native.register(this.javaClass, "libstdc++-6")
        Native.register(this.javaClass, "libvosk")
    }

    @Throws(IOException::class)
    private fun unpackDll(targetDir: File, lib: String) {
        val source = LibVosk::class.java.getResourceAsStream("/win32-x86-64/$lib.dll")
        Files.copy(source, File(targetDir, "$lib.dll").toPath(), StandardCopyOption.REPLACE_EXISTING)
//        NativeLibrary.addSearchPath(lib, targetDir.absolutePath);
    }
}
