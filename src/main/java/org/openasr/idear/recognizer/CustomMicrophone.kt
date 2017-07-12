package org.openasr.idear.recognizer

import com.intellij.openapi.diagnostic.Logger
import java.io.File
import java.io.IOException
import javax.sound.sampled.*

class CustomMicrophone(sampleRate: Float,
                       sampleSize: Int,
                       signed: Boolean,
                       bigEndian: Boolean) {
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

        try {

            line = AudioSystem.getTargetDataLine(format)
            line.open()

            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN))
                logger.info("Microphone: MASTER_GAIN supported")
            else
                logger.warn("Microphone: MASTER_GAIN NOT supported")

            //masterGainControl = findMGControl(line);
        } catch (e: LineUnavailableException) {
            throw IllegalStateException(e)
        }

        stream = AudioInputStreamWithAdjustableGain(line)
    }


    fun startRecording() {
        line.start()
    }

    fun stopRecording() {
        line.stop()
    }

    companion object {
        private val logger = Logger.getInstance(CustomMicrophone::class.java)

        private val DURATION = 4500
        private val TEMP_FILE = "/tmp/X.wav"


        //TODO Refactor this API into a CustomMicrophone instance
        @Throws(IOException::class)
        fun recordFromMic(duration: Long): File {
            val mic = CustomMicrophone(16000f, 16, true, false)

            //Why is this in a thread?
            Thread {
                try {
                    Thread.sleep(duration)
                } catch (ignored: InterruptedException) {
                } finally {
                    mic.stopRecording()
                }
            }.start()

            mic.startRecording()

            val out = File(TEMP_FILE)

            AudioSystem.write(mic.stream, AudioFileFormat.Type.WAVE, out)

            return out
        }
    }
}