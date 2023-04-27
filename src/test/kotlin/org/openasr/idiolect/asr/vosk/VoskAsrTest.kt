package org.openasr.idiolect.asr.vosk

//import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openasr.idiolect.utils.TestAudioUtils
import java.io.InputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem


class VoskAsrTest {
    private val vosk = VoskAsr()

    @Before //Class
    fun init() {
        var model = System.getProperty("user.home") + "/.idiolect/vosk-model-en-us-daanzu-20200905-lgraph"
        VoskAsr.initialiseRecogniserForModel(model)
    }

//    @ParameterizedTest(name = "test Recognition of {fileName}")
//    @ValueSource(strings = ["headphones-neck", "racecar", "Malayalam", ""])
//    fun testProcessAudioInputStream(fileName: String) {
//
//        vosk.processAudioInputStream()
//    }

    @Test
    fun `test recognition of headphones-neck`() {
        val inputStream = TestAudioUtils.getInputStream("headphones-neck")
        // When
        val recognition = vosk.processAudioInputStream(inputStream)
        // Then
        assertThat(recognition.alternatives).contains("i'm wearing my headphones around my neck")
    }

    @Test
    fun `test recognition of headphones-ears`() {
        val inputStream = TestAudioUtils.getInputStream("headphones-ears")
        // When
        val recognition = vosk.processAudioInputStream(inputStream)
        // Then
        assertThat(recognition.alternatives).contains("i'm wearing my headphones on my ears")
    }

    @Test
    fun `test recognition of laptop-noisy-fan`() {
        val inputStream = TestAudioUtils.getInputStream("laptop-noisy-fan")
        // When
        val recognition = vosk.processAudioInputStream(inputStream)
        // Then
        assertThat(recognition.alternatives).contains("i'm talking to my laptop microphone array but its fan is noisy")
    }

    @Test
    fun `test recognition of create-class`() {
        val inputStream = TestAudioUtils.getInputStream("create-class")
        // When
        val recognition = vosk.processAudioInputStream(inputStream)
        // Then
        assertThat(recognition.alternatives).contains("create new class call it authorisation service")
    }


}
