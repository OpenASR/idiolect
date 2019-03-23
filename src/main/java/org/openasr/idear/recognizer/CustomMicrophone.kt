package org.openasr.idear.recognizer

import com.intellij.openapi.diagnostic.Logger
import java.io.*
import javax.sound.sampled.*
import javax.sound.sampled.AudioFileFormat.Type.WAVE
import javax.sound.sampled.FloatControl.Type.MASTER_GAIN

object CustomMicrophone {
    private const val sampleRate = 16000f
    private const val sampleSize = 16
    private const val signed = true
    private const val bigEndian = false
    private val logger = Logger.getInstance(javaClass)

    private const val DURATION = 4500

    private val line: TargetDataLine
    //    /* package */ void setMasterGain(double mg) {
    //        double pmg = inputStream.setMasterGain(mg);
    //
    //        logger.info("Microphone: LINE_IN VOL = " + pmg);
    //        logger.info("Microphone: LINE_IN VOL = " + mg);
    //    }
    //
    //    /* package */ void setNoiseLevel(double mg) {
    //        double pmg = inputStream.setNoiseLevel(mg);
    //
    //        logger.info("Microphone: LINE_IN VOL = " + pmg);
    //        logger.info("Microphone: LINE_IN VOL = " + mg);
    //    }

    val stream: AudioInputStream

    init {
        val format = AudioFormat(sampleRate, sampleSize, 1, signed, bigEndian)

        line = AudioSystem.getTargetDataLine(format)
        line.open()

        if (line.isControlSupported(MASTER_GAIN))
            logger.info("Microphone: MASTER_GAIN supported")
        else logger.warn("Microphone: MASTER_GAIN NOT supported")

        //masterGainControl = findMGControl(line);

        stream = AudioInputStreamWithAdjustableGain(line)
    }

    fun startRecording() = line.start()
    fun stopRecording() = line.stop()

    private const val TEMP_FILE = "/tmp/X.wav"
    //TODO Refactor this API into a CustomMicrophone instance
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