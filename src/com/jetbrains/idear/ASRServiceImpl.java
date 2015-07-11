package com.jetbrains.idear;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.Pair;
import com.jetbrains.idear.recognizer.CustomLiveSpeechRecognizer;
import com.jetbrains.idear.recognizer.CustomMicrophone;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ASRServiceImpl implements ASRService {

    public static final double MASTER_GAIN = 0.85;
    public static final double CONFIDENCE_LEVEL_THRESHOLD = 0.5;

    private final Thread speechThread = new Thread(new ASRControlLoop(), "ARS Thread");

    private static final String ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us";
    private static final String DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "resource:/com.jetbrains.idear/grammars";

    private static final Logger logger = Logger.getLogger(ASRServiceImpl.class.getSimpleName());

    private CustomLiveSpeechRecognizer recognizer;
    private ConfigurationManager configurationManager;
    private Robot robot;

    private final AtomicReference<Status> status = new AtomicReference<>(Status.INIT);

    public void init() {
        Configuration configuration = new Configuration();
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(true);

        configuration.setGrammarName("dialog");

        try {
            recognizer = new CustomLiveSpeechRecognizer(configuration);
//            recognizer.setMasterGain(MASTER_GAIN);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't initialize speech recognizer:", e);
        }

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // Fire up control-loop

        speechThread.start();
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

    private Status setStatus(Status s) {
        return status.getAndSet(s);
    }

    @Override
    public Status getStatus() {
        return status.get();
    }

    @Override
    public Status activate() {
        if (getStatus() == Status.ACTIVE)
            return Status.ACTIVE;

        if (getStatus() == Status.INIT) {
            // Cold start prune cache
            recognizer.startRecognition(true);
        }

        return setStatus(Status.ACTIVE);
    }

    @Override
    public Status deactivate() {
        return setStatus(Status.INACTIVE);
    }

    private class ASRControlLoop implements Runnable {

        public static final String FUCK = "fuck";
        public static final String OPEN = "open";
        public static final String SETTINGS = "settings";
        public static final String RECENT = "recent";
        public static final String TERMINAL = "terminal";
        public static final String FOCUS = "focus";
        public static final String EDITOR = "editor";
        public static final String PROJECT = "project";
        public static final String SELECTION = "selection";
        public static final String EXPAND = "expand";
        public static final String SHRINK = "shrink";
        public static final String PRESS = "press";
        public static final String DELETE = "delete";
        public static final String ENTER = "enter";
        public static final String ESCAPE = "escape";
        public static final String TAB = "tab";
        public static final String UNDO = "undo";
        public static final String NEXT = "next";
        public static final String LINE = "line";
        public static final String PAGE = "page";
        public static final String METHOD = "method";
        public static final String PREVIOUS = "previous";
        public static final String INSPECT_CODE = "inspect code";
        public static final String OKAY_GOOGLE = "okay google";
        public static final String OK_GOOGLE = "ok google";
        public static final String OKAY_IDEA = "okay idea";
        public static final String OK_IDEA = "ok idea";

        @Override
        public void run() {
            while (!isTerminated()) {
                String result = null;

                // This blocks on a recognition result
                if (isActive()) {
                    result = getResultFromRecognizer();
                }

                // This may happen 10-15 seconds later
                if (isActive() && result != null) {
                    logger.log(Level.INFO, "Recognized: " + result);
                    applyAction(result);
                }
            }
        }

        private String getResultFromRecognizer() {
            SpeechResult result = recognizer.getResult();

            logger.info("Recognized:    ");
            logger.info("\tTop H:       " + result.getResult() + " / " + result.getResult().getBestToken() + " / " + result.getResult().getBestPronunciationResult());
            logger.info("\tTop 3H:      " + result.getNbest(3));

            return result.getHypothesis();
        }

        private void applyAction(String c) {
            if (c.equals(FUCK)) {
                invokeAction(IdeActions.ACTION_UNDO);
            }

            else if (c.startsWith("open")) {
                if (c.endsWith("settings")) {
                    invokeAction(IdeActions.ACTION_SHOW_SETTINGS);
                } else if (c.endsWith("recent")) {
                    invokeAction(IdeActions.ACTION_RECENT_FILES);
                } else if (c.endsWith("terminal")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_F12);
                }
            }

            else if (c.startsWith("focus")) {
                if (c.endsWith("editor")) {
                    pressKeystroke(KeyEvent.VK_ESCAPE);
                } else if (c.endsWith("project")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_1);
                }
            }

            else if (c.endsWith("selection")) {
                if (c.startsWith("expand")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_W);
                } else if (c.startsWith("shrink")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_W);
                }
            }

            else if (c.startsWith("press")) {
                if (c.endsWith("delete")) {
                    pressKeystroke(KeyEvent.VK_DELETE);
                } else if (c.endsWith("enter")) {
                    pressKeystroke(KeyEvent.VK_ENTER);
                } else if (c.endsWith("escape")) {
                    pressKeystroke(KeyEvent.VK_ESCAPE);
                } else if (c.endsWith("tab")) {
                    pressKeystroke(KeyEvent.VK_TAB);
                } else if (c.endsWith("undo")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_Z);
                }
            }

            else if (c.startsWith("next")) {
                if (c.endsWith("line")) {
                    pressKeystroke(KeyEvent.VK_DOWN);
                } else if (c.endsWith("page")) {
                    pressKeystroke(KeyEvent.VK_PAGE_DOWN);
                } else if (c.endsWith("method")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_DOWN);
                }
            }

            else if (c.startsWith("previous")) {
                if (c.endsWith("line")) {
                    pressKeystroke(KeyEvent.VK_UP);
                } else if (c.endsWith("page")) {
                    pressKeystroke(KeyEvent.VK_PAGE_UP);
                } else if (c.endsWith("method")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_UP);
                }
            }

            else if (c.startsWith("extract this")) {
                if (c.endsWith("method")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_M);
                } else if (c.endsWith("parameter")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_P);
                }
            }

            else if (c.startsWith("inspect code")) {
                pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_SHIFT, KeyEvent.VK_I);
            }

            else if (c.startsWith("speech pause")) {
                pauseSpeech();
            }

            else if (c.startsWith(OK_IDEA) || c.startsWith(OKAY_IDEA)) {
                beep();
                /* ... */
            }

            else if (c.startsWith(OKAY_GOOGLE) || c.startsWith(OK_GOOGLE)) {
                beep();
                fireGoogleSearch();
            }
        }

        public static final int DURATION = 4500;

        private void fireGoogleSearch() {

            try {
                recordFromMic("/tmp/X.wav");

                GoogleService gs = ServiceManager.getService(GoogleService.class);

                Pair<String, Double> searchQueryTuple = gs.getBestTextForUtterance(new File("/tmp/X.wav"));

                if (searchQueryTuple == null || searchQueryTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
                    return;

                ServiceManager
                    .getService(TTSService.class)
                    .say("I think you said " + searchQueryTuple.first + ", searching Google now");

                gs.searchGoogle(searchQueryTuple.first);

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Panic! Failed to dump WAV", e);
            }
        }

        private void recordFromMic(String filepath) throws IOException {
            final CustomMicrophone mic = new CustomMicrophone(16000, 16, true, false);

            new Thread(() -> {
                try {
                    Thread.sleep(DURATION);
                } catch (InterruptedException _) {
                } finally {
                    mic.stopRecording();
                }
            }).start();

            mic.startRecording();

            AudioSystem.write(mic.getStream(), AudioFileFormat.Type.WAVE, new File(filepath));
        }

        private void pauseSpeech() {
            String result;
            while (isActive()) {
                result = getResultFromRecognizer();
                if (result.equals("speech resume")) {
                    break;
                }
            }
        }
    }

    private boolean isTerminated() {
        return getStatus() == Status.TERMINATED;
    }

    private boolean isActive() {
        return getStatus() == Status.ACTIVE;
    }

    private void pressKeystroke(final int... keys) {
        for (int key : keys) {
            robot.keyPress(key);
        }

        for (int key : keys) {
            robot.keyRelease(key);
        }
    }

    private void invokeAction(final String action) {
        try {
            EventQueue.invokeAndWait(() -> {
                AnAction anAction = ActionManager.getInstance().getAction(action);
                anAction.actionPerformed(new AnActionEvent(null,
                        DataManager.getInstance().getDataContext(),
                        ActionPlaces.UNKNOWN, anAction.getTemplatePresentation(),
                    ActionManager.getInstance(), 0));
            });
        } catch (InterruptedException | InvocationTargetException e) {
            logger.log(Level.SEVERE, "Could not invoke action:", e);
        }
    }

    // Helpers

    public static synchronized void beep() {
        Thread t = new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                    ASRServiceImpl.class.getResourceAsStream("/com.jetbrains.idear/sounds/beep.wav"));
                clip.open(inputStream);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException _) {}
    }

    // This is for testing purposes solely
    public static void main(String[] args) {
        ASRServiceImpl asrService = new ASRServiceImpl();
        asrService.init();
        asrService.activate();
    }
}
