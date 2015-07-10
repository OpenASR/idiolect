package com.jetbrains.idear.recognizer;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.logging.Logger;

public class CustomMicrophone {

    private static final Logger logger = Logger.getLogger(CustomMicrophone.class.getSimpleName());

    private final TargetDataLine line;
    private final AudioInputStreamWithAdjustableGain inputStream;

    public CustomMicrophone(
        float sampleRate,
        int sampleSize,
        boolean signed,
        boolean bigEndian) {

        AudioFormat format =
            new AudioFormat(sampleRate, sampleSize, 1, signed, bigEndian);

        try {

            line = AudioSystem.getTargetDataLine(format);
            line.open();

            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN))
                logger.info("Microphone: MASTER_GAIN supported");
            else
                logger.warning("Microphone: MASTER_GAIN NOT supported");

            //masterGainControl = findMGControl(line);

        } catch (LineUnavailableException e) {
            throw new IllegalStateException(e);
        }

        inputStream = new AudioInputStreamWithAdjustableGain(line);
    }



    public void startRecording() {
        line.start();
    }

    public void stopRecording() {
        line.stop();
    }

    /* package */ void setMasterGain(double mg) {
        double pmg = inputStream.setMasterGain(mg);

        logger.info("Microphone: LINE_IN VOL = " + pmg);
        logger.info("Microphone: LINE_IN VOL = " + mg);
    }

    /* package */ void setNoiseLevel(double mg) {
        double pmg = inputStream.setNoiseLevel(mg);

        logger.info("Microphone: LINE_IN VOL = " + pmg);
        logger.info("Microphone: LINE_IN VOL = " + mg);
    }

    public InputStream getStream() {
        return inputStream;
    }
}
