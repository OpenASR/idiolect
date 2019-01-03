package com.darkprograms.speech.recognizer.vad

import java.io.*
import java.util.*
import javax.sound.sampled.*

/**
 * Useful for debugging & testing microphone
 */
class RecordingListener : VoiceActivityListener {
    private var nextListener: VoiceActivityListener? = null

    override fun onVoiceActivity(audioInputStream: AudioInputStream) {
        val fileName = Date().toString() + ".wav"
        val out = File("/tmp", fileName)

        try {
            println("Saving recoring to " + out.absolutePath)
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (nextListener != null) {
            nextListener!!.onVoiceActivity(audioInputStream)
        }
    }

    fun withNextListener(nextListener: VoiceActivityListener): RecordingListener {
        this.nextListener = nextListener
        return this
    }

    fun setNextListener(nextListener: VoiceActivityListener) {
        this.nextListener = nextListener
    }
}
