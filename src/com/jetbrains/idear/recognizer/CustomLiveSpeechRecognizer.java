package com.jetbrains.idear.recognizer;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.decoder.ResultListener;
import edu.cmu.sphinx.frontend.endpoint.SpeechClassifier;
import edu.cmu.sphinx.frontend.util.StreamDataSource;

import java.io.IOException;

/**
 * High-level class for live speech recognition.
 */
public class CustomLiveSpeechRecognizer extends AbstractSpeechRecognizer {

    public static final int SPEECH_SENSITIVITY = 20;

    private final CustomMicrophone microphone;

    /**
     * Constructs new live recognition object.
     *
     * @param configuration common configuration
     * @throws IOException if model IO went wrong
     */
    public CustomLiveSpeechRecognizer(Configuration configuration) throws IOException
    {
        super(configuration);
        microphone = new CustomMicrophone(16000, 16, true, false);

        context .getInstance(StreamDataSource.class)
                .setInputStream(microphone.getStream());

        context.setLocalProperty(String.format("speechClassifier->%s", SpeechClassifier.PROP_THRESHOLD), SPEECH_SENSITIVITY);
    }

    /**
     * Starts recognition process.
     *
     * @param clear clear cached microphone data
     * @see         CustomLiveSpeechRecognizer#stopRecognition()
     */
    public void startRecognition(boolean clear) {
        recognizer.allocate();
        microphone.startRecording();
    }

    /**
     * Stops recognition process.
     *
     * Recognition process is paused until the next call to startRecognition.
     *
     * @see CustomLiveSpeechRecognizer#startRecognition(boolean)
     */
    public void stopRecognition() {
        microphone.stopRecording();
        recognizer.deallocate();
    }

    public void addResultListener(ResultListener listener) {
        recognizer.addResultListener(listener);
    }

    public void removeResultListener(ResultListener listener) {
        recognizer.removeResultListener(listener);
    }

//    public void setMasterGain(double mg) {
//        microphone.setMasterGain(mg);
//    }
//
//    public void setNoiseLevel(double mg) {
//        microphone.setNoiseLevel(mg);
//    }
}
