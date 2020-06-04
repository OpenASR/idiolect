package org.openasr.idear.asr.deepspeech

import com.intellij.execution.Platform
import com.intellij.openapi.diagnostic.Logger
import org.mozilla.deepspeech.libraryloader.DeepSpeechLibraryConfig
import org.mozilla.deepspeech.recognition.DeepSpeechModel
import org.mozilla.deepspeech.recognition.stream.StreamingState
import org.mozilla.deepspeech.recognition.SpeechRecognitionResult
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

/**
 * @see https://github.com/GommeAntiLegit/DeepSpeech-Java-Bindings/tree/master/libdeepspeech
 */
class DeepSpeechASR {
    private val logger = Logger.getInstance(javaClass)
    val model: DeepSpeechModel? = null;

    fun configureDeepSpeech() {
        val config: DeepSpeechLibraryConfig = DeepSpeechLibraryConfig.Desktop.CPU(
                Companion.urlForLibrary("libdeepspeech_cpu"),
                Companion.urlForLibrary("libdeepspeech-jni_cpu")
        )
        try {
            config.loadDeepSpeech()
        } catch (e: IOException) {
            logger.error("Failed to load DeepSpeech!")
        }
    }

    fun loadModel() {
        val model = DeepSpeechModel(File("model/output_graph.pb"))
//        model.setBeamWidth(42)
//        model.enableLMLanguageModel(new File("model/scorer"), LM_ALPHA, LM_BETA);
    }

    fun audioTranscription() {
        val audioIn: AudioInputStream = AudioSystem.getAudioInputStream(File("testInput.wav"))

        val sampleRate = audioIn.format.sampleRate.toLong()
        val numSamples = audioIn.frameLength
        val frameSize = audioIn.format.frameSize.toLong()

        val audioBuffer = ByteBuffer.allocateDirect((numSamples * 2).toInt()) // 2 bytes for each sample --> 16 bit audio

        val transcription: String? = model?.doSpeechToText(audioBuffer, numSamples, sampleRate)

        val streamingState = StreamingState.setupStream(model)
        streamingState.feedAudioContent(audioBuffer, numSamples)
        val intermediateResult = streamingState.intermediateDecode()
        val utterance = streamingState.finishStream()

//        model.enableLMLanguageModel(File alphabetFile, @NotNull File lmBinaryFile, @NotNull File trieFile, float lmAlpha, float lmBeta)

        // SpeechRecognitionResult
//        val result: SpeechRecognitionResult? = model?.doSpeechRecognitionWithMeta(audioBuffer, numSamples, sampleRate)
//        result.
    }

    companion object {
        private fun urlForLibrary(libName: String): URL {
            val os = Platform.current().toString();
            val extension = if (os == "WINDOWS") "dll" else ".so"
            return File("natives/$os/$libName$extension").toURI().toURL()
        }
    }
}
