package org.openasr.idiolect.mac

import org.junit.Assert.assertEquals
import org.junit.Test
import org.openasr.idiolect.asr.vosk.VoskAsr
import org.vosk.*
import java.io.File

class ASRTest {
    @Test
    fun testTTSToASR() {
        // Run say command
        ProcessBuilder("say", "hello idea", "-o", "/tmp/hello.aiff").start()

        // Read AIFF file
        val testUtterance = File("/tmp/hello.aiff").readBytes()
        val rec = Recognizer(Model("/Users/breandan/.idiolect/vosk-model-en-us-0.22-lgraph"), 16000f)
        rec.acceptWaveForm(testUtterance, testUtterance.size)
        assertEquals("hello idea", rec.result)
    }
}
