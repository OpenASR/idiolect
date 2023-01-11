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
        "say -o /tmp/hello.wav --channels=1 --data-format=LEF32@16000 --rate=60 the"
            .let { ProcessBuilder(it.split(" ")).start().waitFor() }

        // Read AIFF file
        val testUtterance = File("/tmp/hello.wav").readBytes()
        val rec = Recognizer(Model("/Users/breandan/.idiolect/vosk-model-en-us-0.22-lgraph"), 16000f)
        rec.acceptWaveForm(testUtterance, testUtterance.size)
        assertEquals("the", rec.result.drop(14).dropLast(3))
    }
}
