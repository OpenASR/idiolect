package org.openasr.idiolect.mac

import ai.hypergraph.kaliningraph.types.*
import org.junit.Test
import org.openasr.idiolect.utils.WordSequenceAligner
import org.openasr.idiolect.utils.WordSequenceAligner.Alignment
import org.vosk.*
import java.io.File
import kotlin.math.floor
import kotlin.time.*

class ASRTest {
  @OptIn(ExperimentalTime::class)
  fun String.alignCommandAndRecognition(voice: String, model: Recognizer, name: String): Alignment {
    // Run say command
    val outputFile = "/tmp/test"

    ("say -o $outputFile.aif -v $voice".split(" ") + "\"$this\"")
        .let { ProcessBuilder(it).start().waitFor() }

    ("ffmpeg -y -i $outputFile.aif $outputFile.wav".split(" ")) // ffmpeg defaults to pcm_s16le for WAV
        .let { ProcessBuilder(it).start().waitFor() }

    // Read AIFF file
    val timeNow = TimeSource.Monotonic.markNow()
    val recognizedUtterance = File("$outputFile.wav").transcribeWavFile(model)
        .split(" ").filter { it.isNotBlank() }.toTypedArray()
    val recognitionTime = timeNow.elapsedNow().inWholeMilliseconds
    val originalSentence = split(" ").filter { it.isNotBlank() }.toTypedArray()

    // Get length of AIF file in milliseconds
    val aifLength =
      (("ffprobe -v error -show_entries format=duration " +
          "-of default=noprint_wrappers=1:nokey=1 $outputFile.aif").split(" "))
      .let { (ProcessBuilder(it).start().inputStream.bufferedReader().readLine().toFloatOrNull() ?: Float.NaN) * 1000 }
          .let { floor(it) }.toInt()

    return WordSequenceAligner().align(originalSentence, recognizedUtterance)
        .also { println("$name recognized ${aifLength}ms utterance spoken by $voice in ${recognitionTime}ms " +
            "(${(aifLength.toDouble() / recognitionTime).toString().take(5)} x real-time):\n$it\n") }
  }

  val voices = setOf("Eddy", "Flo", "Reed", "Rocko", "Ralph", "Samantha", "Sandy", "Shelley")

  val models = setOf("vosk-model-en-us-0.22", "vosk-model-en-us-0.22-lgraph", "vosk-model-small-en-us-0.15")
    .map { it to Recognizer(Model("${System.getProperty("user.home")}/.idiolect/$it"), 16000f) }

  // Stream wav
  private fun File.transcribeWavFile(rec: Recognizer): String =
    readBytes().let { rec.apply { acceptWaveForm(it, it.size) }.result.drop(14).dropLast(3) }

/*
./gradlew test --tests "org.openasr.idiolect.mac.ASRTest.testUtterances"
*/
  @Test
  fun testUtterances() {
    // Reads vocab.txt from home directory
    val vocabPath = "${System.getProperty("user.home")}/.idiolect/vosk-model-en-us-0.22/graph/words.txt"
    val vocabulary = File(vocabPath).readLines().asSequence()
        .map { it.substringBefore(" ") }
        .filter { it.all(Char::isLetter) }.toSet()

    // Commands
    File("docs/example-phrases.md").readLines()
        .filter { "-" in it }.map { it.substringAfter("-").split(" ") }
        .also { println("Before filtering: ${it.size} commands") }
        // Take only commands containing words that are all in the vocabulary
        .filter { words -> words.filter { it.isNotBlank() }.all { (it in vocabulary) } }
        .also { println("After filtering: ${it.size} commands") }
        .map { it.joinToString(" ") }.shuffled().take(100).toSet()
        .also { println("Total length of all utterances: ${it.joinToString(" ").split(" ").filter(String::isNotBlank).size}") }
        .let { models.toSet() * voices * it }
        .map { (name, model, voice, utterance) ->
            Triple(name, voice, utterance.trim().alignCommandAndRecognition(voice, model, name))
        }.groupBy { it.first + "/" + it.second }
        .mapValues { WordSequenceAligner.SummaryStatistics(it.value.map { it.third }) }
        // Eddy     :: 0.24
        // Flo      :: 0.19
        // Reed     :: 0.22
        // Rocko    :: 0.24
        // Samantha :: 0.29
        // Sandy    :: 0.24
        // Shelley  :: 0.26
        // Fred     :: 0.27
        .let { println("Average word error rate:\n${it.entries.joinToString("\n") { it.key + " :: " + it.value.wordErrorRate}}") }
  }
}
