package com.jetbrains.idear.recognizer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.util.logging.Logger;

public class AudioInputStreamWithAdjustableGain extends AudioInputStream {
    private static final double DEFAULT_MASTER_GAIN = 1.0;
    private static final double DEFAULT_NOISE_LEVEL = 0.0;

    private double masterGain;
    private double noiseLevel;

    private static final Logger logger = Logger.getLogger(AudioInputStreamWithAdjustableGain.class.getSimpleName());

    AudioInputStreamWithAdjustableGain(TargetDataLine line) {
        super(line);

        masterGain = DEFAULT_MASTER_GAIN;
        noiseLevel = DEFAULT_NOISE_LEVEL;
    }

    @Override
    public int read() throws IOException {
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = super.read(b);

        dump(b, 0, b.length);

        for (int i = 0; i < read; ++i) {
            b[i] = adjust(b[i]);
        }

        dump(b, 0, b.length);

        return read;
    }

    private void dump(byte[] b, int off, int len) {
        StringBuilder sb = new StringBuilder();
        for (int i = off; i < off + len - 1; ++i) {
            sb.append(b[i]);
            if (i != off + len - 1)
                sb.append(", ");
        }
        logger.info(sb.toString());
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);

        //dump(b, off, read);

        for (int i = off; i < off + read; ++i) {
            b[i] = adjust(b[i]);
        }

        //dump(b, off, read);

        return read;
    }

    public double setMasterGain(double mg) {
        double pmg = masterGain;
        masterGain = mg;
        return pmg;
    }

    public double setNoiseLevel(double nl) {
        double pnl = noiseLevel;
        noiseLevel = nl;
        return pnl;
    }

    private byte adjust(byte b) {
        return cut((byte) (b * masterGain));
    }

    private byte cut(byte b) {
        return b < Byte.MAX_VALUE * noiseLevel && b > Byte.MIN_VALUE * noiseLevel ? 0 : b;
    }
}
