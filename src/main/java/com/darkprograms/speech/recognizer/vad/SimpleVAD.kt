package com.darkprograms.speech.recognizer.vad

import com.darkprograms.speech.microphone.MicrophoneAnalyzer

/**
 * Adapted from https://stackoverflow.com/questions/18815235/can-i-use-google-speech-recognition-api-in-my-desktop-application
 */
class SimpleVAD : AbstractVAD() {
    var threshold = 10
    private var ambientVolume: Int = 0
    private var speakingVolume: Int = 0
    private var speaking: Boolean = false

    override fun run() {
        speakingVolume = -2
        speaking = false
        ambientVolume = mic!!.audioVolume
        super.run()
    }

    override fun sampleForSpeech(audioData: ByteArray): Boolean {
        val volume = MicrophoneAnalyzer.calculateRMSLevel(audioData)
        //System.out.println(volume);
        if (volume > ambientVolume + threshold) {
            speakingVolume = volume
            speaking = true
        }
        if (speaking && volume + threshold < speakingVolume) {
            speaking = false
        }
        return speaking
    }
}
