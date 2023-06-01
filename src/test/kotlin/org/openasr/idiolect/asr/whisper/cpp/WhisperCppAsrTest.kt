package org.openasr.idiolect.asr.whisper.cpp

import com.intellij.mock.MockApplication
import com.intellij.openapi.Disposable
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.openasr.idiolect.asr.models.ModelInfo
import org.openasr.idiolect.asr.models.ModelManager
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppConfig
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppConfigurable
import org.openasr.idiolect.utils.TestAudioUtils
import java.util.function.Consumer
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue


class WhisperCppAsrTest : Disposable {
    @Before
    fun setup() {
        val application = MockApplication.setUp(this)
        application.registerService(WhisperCppConfig::class.java)
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun testSpeechRecognition() {
        // Given
        val model = "base.en"
        val whisperCppAsr = WhisperCppAsr()
        whisperCppAsr.modelManager = TestModelManager(model, whisperCppAsr)
        whisperCppAsr.activate()

        // and test audio
        val audioStream = TestAudioUtils.getInputStream("test-audio.wav")
        val nbytes = audioStream.available()
        val audioData = ByteArray(nbytes)
        audioStream.read(audioData)

        // When
        val (nlpRequest, time) = measureTimedValue {
            whisperCppAsr.processSpeech(audioData, nbytes)
        }

        // Then the speech should be recognised.
        // Note: On Windows this takes about 2.8-3.5 seconds. Running whisper_full_parallel actually makes it significantly slower & less accurate
        // Using CUBLAS brings this down to 1.6-2 seconds
        println("'${nlpRequest?.utterance}' in $time")
        assertNotNull(nlpRequest)
        assertEquals("create a function to test the audio input. save it to", nlpRequest?.utterance)
    }

    override fun dispose() {
    }
}

class TestModelManager(private val model: String, private val whisperCppAsr: WhisperCppAsr) : ModelManager<WhisperCppConfigurable>(model, "", WhisperCppConfigurable::class.java) {
    override fun configuredModelPath(): String {
        return model
    }

    override fun listModels(): List<ModelInfo> {
        return listOf()
    }

    override fun initialiseModel(notificationContent: String, setModel: Consumer<String>) {
        runBlocking {
            whisperCppAsr.setModel(model)
        }
    }
}
