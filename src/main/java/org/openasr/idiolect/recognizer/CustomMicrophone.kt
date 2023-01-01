package org.openasr.idiolect.recognizer

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.diagnostic.logger
import java.io.*
import javax.sound.sampled.*
import javax.sound.sampled.AudioFileFormat.Type.WAVE
import javax.sound.sampled.FloatControl.Type.MASTER_GAIN

@Service
class CustomMicrophone : Closeable, Disposable {
    companion object {
        private val log = logger<CustomMicrophone>()

        private const val sampleRate = 16000f
        private const val sampleSize = 16
        private const val signed = true
        private const val bigEndian = false
        private const val DURATION = 4500

        private const val TEMP_FILE = "/tmp/X.wav"
    }

    private var line: TargetDataLine? = null
    lateinit var stream: AudioInputStream

    fun open() {
        if (line == null) {
            val format =
                AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, sampleSize, 1, 2, sampleRate, bigEndian)
            val line = AudioSystem.getTargetDataLine(format)
            line.open()

            if (line.isControlSupported(MASTER_GAIN))
                log.info("Microphone: MASTER_GAIN supported")
            else log.warn("Microphone: MASTER_GAIN NOT supported")

            //masterGainControl = findMGControl(line);

            stream = AudioInputStreamWithAdjustableGain(line)
            this.line = line
        }
    }

    override fun close() = dispose()

    override fun dispose() {
        stopRecording()
        line?.close()
        stream.close()
        line = null
    }

    fun startRecording() {
        line?.start()
    }
    fun stopRecording() {
        line?.stop()
    }

    @Throws(IOException::class)
    fun recordFromMic(duration: Long): File {
        //Why is this in a thread?
        Thread {
            try {
                Thread.sleep(duration)
            } catch (ignored: InterruptedException) {
            } finally {
                stopRecording()
            }
        }.start()

        startRecording()

        return File(TEMP_FILE).apply { AudioSystem.write(stream, WAVE, this) }
    }
}
