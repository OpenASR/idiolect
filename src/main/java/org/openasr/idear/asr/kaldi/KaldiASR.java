package org.openasr.idear.asr.kaldi;

public class KaldiASR {

    static {
        System.loadLibrary("vosk_jni");
    }

    public void finalize() {
        recognizer.finalize();
    }

    public void bla(String modelPath, String speakerModelPath) {
        Vosk.SetLogLevel(0);

        Model model = new Model(modelPath);
        SpkModel speakerModel = new SpkModel(speakerModelPath);
        float sampleRate;

        KaldiRecognizer recognizer = new KaldiRecognizer(model, sampleRate);
        KaldiRecognizer recognizer = new KaldiRecognizer(model, speakerModel, sampleRate);
//        String grammar; // space-separated list of words
//        KaldiRecognizer recognizer = new KaldiRecognizer(model, sampleRate, grammar);

        byte[] data;
        int len;
        boolean accepted = recognizer.AcceptWaveform(data, len);
        String result = recognizer.Result();
        result = recognizer.PartialResult();
        result = recognizer.FinalResult;
    }
}
