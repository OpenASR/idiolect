package com.darkprograms.speech.recognizer.vad

import javax.sound.sampled.AudioInputStream

interface VoiceActivityListener {
    fun onVoiceActivity(audioInputStream: AudioInputStream)
}
