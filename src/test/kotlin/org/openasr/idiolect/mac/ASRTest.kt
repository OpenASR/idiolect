package org.openasr.idiolect.mac

import org.junit.Assert.assertEquals
import org.junit.Test
import org.vosk.*
import java.io.*

class ASRTest {
    @Test
    fun testTTSToASR() {
        // Run say command
        val utterance = "the quick brown fox jumped over the lazy dog"
        val outputFile = "/tmp/test"

        ("say -o $outputFile.aif".split(" ") + "\"$utterance\"")
          .let { ProcessBuilder(it).start().waitFor() }

        ("ffmpeg -y -i $outputFile.aif $outputFile.wav".split(" ")) // ffmpeg defaults to pcm_s16le for WAV
          .let { ProcessBuilder(it).start().waitFor() }

        // Read AIFF file
        val testUtterance = File("$outputFile.wav").transcribeWavFile()
        assertEquals(utterance, testUtterance)
    }

    fun File.transcribeWavFile(): String {
        // Stream wav
        val rec = Recognizer(Model("${System.getProperty("user.home")}/.idiolect/vosk-model-en-us-0.22"), 16000f)

        val bytes = readBytes()

        rec.acceptWaveForm(bytes, bytes.size)
        return rec.result.drop(14).dropLast(3)
    }

}
