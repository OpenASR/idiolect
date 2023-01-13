package org.openasr.idiolect.mac

import org.junit.Test
import org.vosk.*
import java.io.File

class ASRTest {
    fun String.measureWER(): Float {
        // Run say command
        val outputFile = "/tmp/test"

        ("say -o $outputFile.aif".split(" ") + "\"$this\"")
          .let { ProcessBuilder(it).start().waitFor() }

        ("ffmpeg -y -i $outputFile.aif $outputFile.wav".split(" ")) // ffmpeg defaults to pcm_s16le for WAV
          .let { ProcessBuilder(it).start().waitFor() }

        // Read AIFF file
        val recognizedUtterance = File("$outputFile.wav").transcribeWavFile()
//        throw Exception("Expected $this, but was $recognizedUtterance")
        val werEval = WordSequenceAligner()

        return werEval.align(split(" ").toTypedArray(), recognizedUtterance.split(" ").toTypedArray())
          .let { (1 - it.numCorrect / it.referenceLength.toFloat()) }
    }

  val modelName =
    "vosk-model-en-us-0.22"
//  "vosk-model-en-us-0.22-lgraph"
//  "vosk-model-small-en-us-0.15"
    val rec = Recognizer(Model("${System.getProperty("user.home")}/.idiolect/$modelName"), 16000f)

    fun File.transcribeWavFile(): String {
        // Stream wav
        val bytes = readBytes()

        rec.acceptWaveForm(bytes, bytes.size)
        return rec.result.drop(14).dropLast(3)
    }

    @Test
    fun testUtterances() {
      File("/tmp/utterances.txt").readLines().filter { it.isNotBlank() }
        .shuffled().take(100) // Sample 100 utterances
        .map { it.trim().measureWER() }.average()
        .let { throw Exception("Average word error rate: $it") }
    }
}
