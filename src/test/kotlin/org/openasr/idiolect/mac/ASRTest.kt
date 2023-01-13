package org.openasr.idiolect.mac

import org.junit.Test
import org.vosk.*
import java.io.File

class ASRTest {
    fun String.measureWER(voice: String): Float {
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

    val voices = setOf("Eddy", "Flo", "Reed", "Rocko", "Samantha", "Sandy", "Shelley", "Fred")

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
        .flatMap { u -> voices.map { it to u } }
        .map { (voice, utterance) -> voice to utterance.trim().measureWER(voice) }
        .groupBy { it.first }.mapValues { it.value.map { it.second }.average() }
        //Eddy     :: 0.24
        //Flo      :: 0.19
        //Reed     :: 0.22
        //Rocko    :: 0.24
        //Samantha :: 0.29
        //Sandy    :: 0.24
        //Shelley  :: 0.26
        //Fred     :: 0.27
        .let { throw Exception("Average word error rate:\n${it.entries.joinToString("\n") { it.key + " :: " + it.value }}") }
    }
}
