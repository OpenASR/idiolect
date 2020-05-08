package org.openasr.idear.asr.picovoice.rhino

import com.darkprograms.speech.microphone.Microphone
import com.intellij.openapi.diagnostic.Logger
import org.openasr.idear.asr.picovoice.AudioConsumer
import org.openasr.idear.recognizer.CustomMicrophone
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Records audio from microphone in a format that can be processed by suite of technologies developed at Picovoice.
 */
class AudioRecorder {
    private val logger = Logger.getInstance(javaClass)

    private var audioConsumer: AudioConsumer? = null
    private val started = AtomicBoolean(false)
    private val stop = AtomicBoolean(false)
    private val stopped = AtomicBoolean(false)

    constructor(audioConsumer: AudioConsumer?) {
        this.audioConsumer = audioConsumer
    }

    /**
     * Starts the recording of audio.
     */
    fun start() {
        if (started.get()) {
            return
        }

        started.set(true)
        Thread { record() }.start()
    }

    /**
     * Stops the recording of audio.
     *
     * @throws InterruptedException On failure.
     */
    @Throws(InterruptedException::class)
    fun stop() {
        if (!started.get()) {
            return
        }

        CustomMicrophone.stopRecording()
        stop.set(true)

        while (!stopped.get()) {
            Thread.sleep(32)
        }
        started.set(false)
    }

    private fun record() {
        /*val minBufferSize: Int = AudioRecord.getMinBufferSize(
                audioConsumer.getSampleRate(),
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT)
        val bufferSize = Math.max(audioConsumer.getSampleRate() / 2, minBufferSize)*/
        val bufferSize = audioConsumer!!.getSampleRate() / 2
        val samplesToRead = audioConsumer!!.getFrameLength()
        val bytesToRead = samplesToRead * 2
        val frame = ByteArray(bytesToRead) // number of (16bit) audio samples per frame
        val shorts = ShortArray(samplesToRead)
        var microphone: Microphone? = null

        try {
//            CustomMicrophone.startRecording()
            microphone = Microphone(bufferSize.toFloat())
            val stream = microphone.captureAudioToStream()

            while (!stop.get()) {
                val numRead: Int = stream.read(frame, 0, bytesToRead)
                if (numRead == bytesToRead) {
                    // adapted from https://github.com/tlaukkan/kotlin-speech-api/blob/master/src/main/kotlin/org/bubblecloud/voice/Listener.kt
                    ByteBuffer.wrap(frame).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts)

                    // TODO: kotlin-speech-api has some RMS speech threshold detection here...
                    //if (stop.get()) break

                    audioConsumer!!.consume(shorts)
                } else {
                    logger.warn("not enough samples for the audio consumer.")
                }
            }
        } finally {
            microphone?.close()
            stopped.set(true)
        }
    }
}