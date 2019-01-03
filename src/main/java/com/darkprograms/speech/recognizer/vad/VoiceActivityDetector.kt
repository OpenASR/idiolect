package com.darkprograms.speech.recognizer.vad

import com.darkprograms.speech.microphone.MicrophoneAnalyzer

/**
 * @see [https://github.com/Sciss/SpeechRecognitionHMM/blob/master/src/main/java/org/ioe/tprsa/audio/preProcessings/EndPointDetection.java]
 */
interface VoiceActivityDetector {
    enum class VadState {
        INITIALISING,
        PAUSED,
        LISTENING,
        DETECTED_SPEECH,
        DETECTED_SILENCE_AFTER_SPEECH,
        CLOSED
    }

    // TODO: optionally provide PipedInputStream to support streaming recognition on Google
    fun detectVoiceActivity(mic: MicrophoneAnalyzer, listener: VoiceActivityListener)

    fun detectVoiceActivity(mic: MicrophoneAnalyzer, maxSpeechMs: Int, listener: VoiceActivityListener)
    fun setVoiceActivityListener(listener: VoiceActivityListener)

    fun pause()

    fun terminate()
}
