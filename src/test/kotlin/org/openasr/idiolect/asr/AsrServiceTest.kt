package org.openasr.idiolect.asr

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.messages.MessageBus
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.openasr.idiolect.nlp.NlpProvider
import org.openasr.idiolect.nlp.NlpResultListener
import org.openasr.idiolect.recognizer.CustomMicrophone

private fun AsrService.initialiseListeningState(listening: Boolean) {
    javaClass.getDeclaredField("isListening").let {
        it.isAccessible = true
        it.set(this, listening)
    }
}

class AsrServiceTest : Disposable {
    companion object {
        private val asrService = AsrService() //  mockk<AsrService>()
        private val microphone = spyk(CustomMicrophone())
        private val asrProvider = mockk<AsrProvider>()
        private val messageBus = mockk<MessageBus>()

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            every { asrProvider.startRecognition() } answers { microphone.startRecording() }
            every { asrProvider.stopRecognition() } answers { microphone.stopRecording() }
        }
    }

    private lateinit var asrSystem: AsrSystem

    @Before
    fun setup() {
//        val application = MockApplication(this)
        val application = mockk<Application>()
        every { application.messageBus } returns messageBus
        ApplicationManager.setApplication(application, this)

        asrSystem = AsrControlLoop()
        asrSystem.initialise(asrProvider, mockk<NlpProvider>())
        asrService.setAsrSystem(asrSystem)
    }

    @Test
    fun `start recording`() {
        // Given not listening
        asrService.initialiseListeningState(false)

        val nlpListener = mockk<NlpResultListener>(relaxed = true)
        every { messageBus.syncPublisher(NlpResultListener.NLP_RESULT_TOPIC) } returns nlpListener

        // When
        asrService.toggleListening()

        // Then
        verify { nlpListener.onListening(true) }
        verify { microphone.startRecording() }
    }

    @Test
    fun `stop recording`() {
        // Given is listening
        asrService.initialiseListeningState(true)

        val nlpListener = mockk<NlpResultListener>(relaxed = true)
        every { messageBus.syncPublisher(NlpResultListener.NLP_RESULT_TOPIC) } returns nlpListener

        // When
        asrService.toggleListening()

        // Then
        verify { nlpListener.onListening(false) }
        verify { microphone.stopRecording() }
    }

    override fun dispose() {
    }
}
