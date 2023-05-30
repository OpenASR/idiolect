package org.openasr.idiolect.recognizer

import com.intellij.mock.MockApplication
import com.intellij.openapi.Disposable
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.openasr.idiolect.asr.whisper.cpp.settings.WhisperCppConfig
import org.openasr.idiolect.utils.TestAudioUtils
import java.io.ByteArrayOutputStream
import javax.sound.sampled.*

class VoiceActivityStreamTest : Disposable {
    @Before
    fun setup() {
        val application = MockApplication.setUp(this)
        application.registerService(WhisperCppConfig::class.java)
    }

    @Test
    fun testRead() {
        // Given
        val audioStream = TestAudioUtils.getInputStream("test-audio.wav")
        val line = TestDataLine(audioStream)
        val stream = VoiceActivityStream(line,
            threshold = 110,
            maxSilenceMills = 400)
        val buffer = ByteArray(audioStream.available())

        // and an ASR
//        val model = "base.en"
//        val whisperCppAsr = WhisperCppAsr()
//        whisperCppAsr.modelManager = TestModelManager(model, whisperCppAsr)
//        whisperCppAsr.activate()

        try {
            // When
            var nbytes = stream.read(buffer, 0, buffer.size)

            // Then
            println("processing $nbytes bytes of audio")
            assertNotEquals("Should have some audio", 0, nbytes)
            assertNotEquals("Should remove some silence", buffer.size, nbytes)
//            saveToFile(buffer, nbytes, "vad-1.wav")
//            var nlpRequest = whisperCppAsr.processSpeech(buffer, nbytes)
//            assertEquals("create a function to test the audio input", nlpRequest?.utterance)

            // and read again
            nbytes = stream.read(buffer, 0, buffer.size)
            println("processing $nbytes bytes of audio")
            assertNotEquals("Should have some audio", 0, nbytes)
            assertNotEquals("Should remove some silence", buffer.size, nbytes)
//            saveToFile(buffer, nbytes, "vad-2.wav")
//            nlpRequest = whisperCppAsr.processSpeech(buffer, nbytes)
//            assertEquals("save it to", nlpRequest?.utterance)
        } finally {
//            whisperCppAsr.deactivate()
            stream.close()
        }
    }

    override fun dispose() {
    }

    private fun saveToFile(buffer: ByteArray, nbytes: Int, fileName: String) {
        var outputStream = ByteArrayOutputStream(nbytes).apply {
            write(buffer, 0, nbytes)
        }
        TestAudioUtils.saveAudioStream(outputStream, CustomMicrophone.format, fileName)

    }

    private class TestDataLine(private val audioStream: AudioInputStream) : TargetDataLine {
        override fun close() {
            audioStream.close()
        }

        override fun getLineInfo(): Line.Info? {
            return null
        }

        override fun getFormat(): AudioFormat {
            return CustomMicrophone.format
        }

        override fun available(): Int {
            return audioStream.available()
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            return audioStream.read(b, off, len)
        }

        override fun open(format: AudioFormat?, bufferSize: Int) {
        }

        override fun open(format: AudioFormat?) {
        }

        override fun open() {
        }

        override fun isOpen(): Boolean {
            return true
        }

        override fun getControls(): Array<Control> {
            return arrayOf()
        }

        override fun isControlSupported(control: Control.Type?): Boolean {
            return false
        }

        override fun getControl(control: Control.Type?): Control {
            TODO("Not yet implemented")
        }

        override fun addLineListener(listener: LineListener?) {
        }

        override fun removeLineListener(listener: LineListener?) {
        }

        override fun drain() {
        }

        override fun flush() {
        }

        override fun start() {
        }

        override fun stop() {
        }

        override fun isRunning(): Boolean {
            return true
        }

        override fun isActive(): Boolean {
            return true
        }

        override fun getBufferSize(): Int {
            TODO("Not yet implemented")
        }

        override fun getFramePosition(): Int {
            TODO("Not yet implemented")
        }

        override fun getLongFramePosition(): Long {
            TODO("Not yet implemented")
        }

        override fun getMicrosecondPosition(): Long {
            TODO("Not yet implemented")
        }

        override fun getLevel(): Float {
            TODO("Not yet implemented")
        }
    }
}
