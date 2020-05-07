package com.darkprograms.speech.recognizer.vad

import com.darkprograms.speech.microphone.*
import java.io.*
import javax.sound.sampled.AudioInputStream


abstract class AbstractVAD : VoiceActivityDetector, Runnable {

    protected lateinit var audio: AudioInputStream
    var mic: MicrophoneAnalyzer? = null
    private var listener: VoiceActivityListener? = null
    private var state: VoiceActivityDetector.VadState = VoiceActivityDetector.VadState.INITIALISING
    private var thread: Thread? = null

    private var maxSpeechMs: Int = 0
    private var maxSpeechWindows: Int = 0
    var silenceCount: Int = 0
    var speechCount: Int = 0

    private var offset: Int = 0
    private var bufferSize: Int = 0
    private var outBuffer: ByteArrayOutputStream? = null

    // TODO: optionally provide PipedInputStream to support streaming recogntion on Google
    override fun detectVoiceActivity(mic: MicrophoneAnalyzer, listener: VoiceActivityListener) {
        detectVoiceActivity(mic, MAX_SPEECH_MILLIS, listener)
    }

    /** Initialise the VAD and start a thread  */
    override fun detectVoiceActivity(mic: MicrophoneAnalyzer, maxSpeechMs: Int, listener: VoiceActivityListener) {
        this.listener = listener
        this.maxSpeechMs = maxSpeechMs
        maxSpeechWindows = maxSpeechMs / WINDOW_MILLIS
        state = VoiceActivityDetector.VadState.LISTENING

        if (this.mic != null) {
            if (this.mic === mic) {
                // re-open the same mic
                if (mic.state == Microphone.CaptureState.CLOSED) {
                    mic.open()
                }
                return
            } else {
                // swap mics
                this.audio = mic.captureAudioToStream()
                this.mic!!.close()
            }
        } else {
            this.audio = mic.captureAudioToStream()
        }

        this.mic = mic

        if (thread == null || !thread!!.isAlive) {
            thread = Thread(this, "JARVIS-VAD")
            thread!!.start()
        }
    }

    override fun pause() {
        state = VoiceActivityDetector.VadState.PAUSED
        mic!!.close()
    }

    override fun setVoiceActivityListener(listener: VoiceActivityListener) {
        this.listener = listener
    }

    override fun terminate() {
        state = VoiceActivityDetector.VadState.CLOSED
        mic!!.close()
        thread!!.interrupt()
    }

    /**
     * Continuously reads "windows" of audio into a buffer and delegates to [.sampleForSpeech]
     * and [.incrementSpeechCounter].
     * [.emitVoiceActivity] will be called when an utterance has been captured.
     */
    override fun run() {
        val bytesToRead = mic!!.getNumOfBytes(WINDOW_SECONDS)
        val audioData = ByteArray(bytesToRead)
        bufferSize = maxSpeechMs * this.mic!!.getNumOfBytes(0.001)
        silenceCount = 0
        speechCount = 0
        offset = 0
        outBuffer = ByteArrayOutputStream(bufferSize)

        state = VoiceActivityDetector.VadState.LISTENING

        while (state !== VoiceActivityDetector.VadState.CLOSED) {
            try {
                val bytesRead = this.audio.read(audioData, 0, bytesToRead)
                if (bytesRead > 0) {
                    val speechDetected = sampleForSpeech(audioData)
                    incrementSpeechCounter(speechDetected, bytesRead, audioData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                state = VoiceActivityDetector.VadState.CLOSED
                return
            }

        }
    }

    /**
     * Executed from within the VAD thread
     * @param audioData
     * @return
     */
    protected abstract fun sampleForSpeech(audioData: ByteArray): Boolean

    protected fun incrementSpeechCounter(speechDetected: Boolean, bytesRead: Int, audioData: ByteArray) {
        var updatedBytesRead = bytesRead
        if (speechDetected) {
            speechCount++
            // Ignore speech runs less than 5 successive frames.
            if (state !== VoiceActivityDetector.VadState.DETECTED_SPEECH && speechCount >= IGNORE_SPEECH_WINDOWS) {
                state = VoiceActivityDetector.VadState.DETECTED_SPEECH
                silenceCount = 0
            }

            if (offset + updatedBytesRead < bufferSize) {
                outBuffer!!.write(audioData, 0, updatedBytesRead)
                offset += updatedBytesRead

                if (speechCount >= maxSpeechWindows) {
                    println("in theory, this should be handled by the following end of buffer handler")
                    emitVoiceActivity(outBuffer)
                }
            } else {
                println("Reached the end of the buffer! Send what we've captured so far")
                updatedBytesRead = bufferSize - offset
                outBuffer!!.write(audioData, 0, updatedBytesRead)
                emitVoiceActivity(outBuffer)
            }
        } else {
            // silence
            silenceCount++

            //   Ignore silence runs less than 10 successive frames.
            if (state === VoiceActivityDetector.VadState.DETECTED_SPEECH && silenceCount >= IGNORE_SILENCE_WINDOWS) {
                if (silenceCount >= MAX_SILENCE_WINDOWS && speechCount >= MIN_SPEECH_WINDOWS) {
                    println("We have silence after a chunk of speech worth processing")
                    emitVoiceActivity(outBuffer)
                } else {
                    state = VoiceActivityDetector.VadState.DETECTED_SILENCE_AFTER_SPEECH
                }

                speechCount = 0
            }
        }
    }

    protected fun emitVoiceActivity(outBuffer: ByteArrayOutputStream?) {
        listener!!.onVoiceActivity(createVoiceActivityStream(outBuffer))
        outBuffer!!.reset()
        offset = 0
        state = VoiceActivityDetector.VadState.LISTENING
    }

    protected fun createVoiceActivityStream(outBuffer: ByteArrayOutputStream?): AudioInputStream {
        println("speech: " + mic!!.audioFormat.frameSize * mic!!.getNumOfFrames(outBuffer!!.size()))
        return AudioInputStream(ByteArrayInputStream(outBuffer.toByteArray()), audio.format, mic!!.getNumOfFrames(outBuffer.size()).toLong())
    }

    companion object {
        private val WINDOW_MILLIS = 16
        private val IGNORE_SILENCE_WINDOWS = 10
        private val IGNORE_SPEECH_WINDOWS = 5
        /** maximum ms between words  */
        private val MAX_SILENCE_MILLIS = 4
        /** minimum duration of speech to recognise  */
        private val MIN_SPEECH_MILLIS = 200
        private val WINDOW_SECONDS = WINDOW_MILLIS.toDouble() / 1000
        /** Google does not allow recordings over 1 minute, but 10 seconds should be ample  */
        private val MAX_SPEECH_MILLIS = 10000
        private val MAX_SILENCE_WINDOWS = MAX_SILENCE_MILLIS / WINDOW_MILLIS
        private val MIN_SPEECH_WINDOWS = MIN_SPEECH_MILLIS / WINDOW_MILLIS
    }
}
