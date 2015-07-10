package com.jetbrains.idear.recognizer;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.logging.Logger;

public class CustomMicrophone {

    private static final Logger logger = Logger.getLogger(CustomMicrophone.class.getSimpleName());

    private final TargetDataLine line;
    private final AudioInputStreamWithAdjustableGain inputStream;
//    private final FloatControl masterGainControl;

//    private static FloatControl findMGControl(TargetDataLine line) {
//
//        final CompoundControl cc = (CompoundControl) line.getControls()[0];
//        final Control[] controls = cc.getMemberControls();
//
//        for (final Control c : controls)
//            if (c instanceof FloatControl)
//                return ((FloatControl)c);
//
//        return null;
//    }

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

    public InputStream getStream() {
        return inputStream;
    }
}
