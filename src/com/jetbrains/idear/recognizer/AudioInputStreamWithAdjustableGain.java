package com.jetbrains.idear.recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.TargetDataLine;
import java.io.IOException;
import java.io.InputStream;

public class AudioInputStreamWithAdjustableGain extends AudioInputStream {

    private static final double DEFAULT_MASTER_GAIN = 1.0;

    private double masterGain;

    public AudioInputStreamWithAdjustableGain(InputStream stream, AudioFormat format, long length) {
        super(stream, format, length);

        masterGain = DEFAULT_MASTER_GAIN;
    }

    public AudioInputStreamWithAdjustableGain(TargetDataLine line) {
        super(line);

        masterGain = DEFAULT_MASTER_GAIN;
    }

    @Override
    public int read() throws IOException {
        return super.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = super.read(b);
        for (int i = 0; i < read; ++i) {
            b[i] = adjust(b[i]);
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = super.read(b, off, len);
        for (int i = off; i < off + read; ++i) {
            b[i] = adjust(b[i]);
        }
        return read;
    }

    public double setMasterGain(double mg) {
        double pmg = masterGain;
        masterGain = mg;
        return pmg;
    }

    private byte adjust(byte b) {
        return (byte) Math.min(b * masterGain, Byte.MAX_VALUE);
    }
}
