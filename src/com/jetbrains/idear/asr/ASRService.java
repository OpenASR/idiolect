package com.jetbrains.idear.asr;

import com.jetbrains.idear.recognizer.CustomLiveSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ASRService {

    public static final double MASTER_GAIN = 0.85;
    public static final double CONFIDENCE_LEVEL_THRESHOLD = 0.5;

    private Thread speechThread;

    private static final String ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us";
    private static final String DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "resource:/com.jetbrains.idear/grammars";

    private static final Logger logger = Logger.getLogger(ASRService.class.getSimpleName());

    private CustomLiveSpeechRecognizer recognizer;

    public void init() {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(true);

        configuration.setGrammarName("command");

        try {
            recognizer = new CustomLiveSpeechRecognizer(configuration);
//            recognizer.setMasterGain(MASTER_GAIN);
            speechThread = new Thread(new ASRControlLoop(recognizer), "ASR Thread");
            recognizer.startRecognition(true);
            // Fire up control-loop
            speechThread.start();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't initialize speech recognizer:", e);
        }
    }

    public boolean activate() {
//        if (getStatus() == Status.INIT) {
//            // Cold start prune cache
//            recognizer.startRecognition(true);
//        }

        return ListeningState.activate();
    }

    public boolean deactivate() {
        return ListeningState.standBy();
    }

    public void dispose() {
        // Deactivate in the first place, therefore actually
        // prevent activation upon the user-input
        deactivate();
        terminate();
    }

    private void terminate() {
        recognizer.stopRecognition();
    }

    // This is for testing purposes solely
    public static void main(String[] args) {
        ASRService asrService = new ASRService();
        asrService.init();
        ListeningState.activate();
    }
}
