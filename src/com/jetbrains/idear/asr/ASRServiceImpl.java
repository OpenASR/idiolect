package com.jetbrains.idear.asr;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.Pair;
import com.intellij.util.Consumer;
import com.jetbrains.idear.GoogleHelper;
import com.jetbrains.idear.actions.ExecuteVoiceCommandAction;
import com.jetbrains.idear.recognizer.CustomLiveSpeechRecognizer;
import com.jetbrains.idear.recognizer.CustomMicrophone;
import com.jetbrains.idear.tts.TTSService;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ASRServiceImpl implements ASRService {

    public static final double MASTER_GAIN = 0.85;
    public static final double CONFIDENCE_LEVEL_THRESHOLD = 0.5;

    private final Thread speechThread = new Thread(new ASRControlLoop(), "ASR Thread");

    private static final String ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us";
    private static final String DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "resource:/com.jetbrains.idear/grammars";

    private static final Logger logger = Logger.getLogger(ASRServiceImpl.class.getSimpleName());

    private CustomLiveSpeechRecognizer recognizer;
    private Robot robot;

    private final AtomicReference<Status> status = new AtomicReference<>(Status.INIT);

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
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't initialize speech recognizer:", e);
        }

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // Start-up recognition facilities
        recognizer.startRecognition(true);

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

//        if (getStatus() == Status.INIT) {
//            // Cold start prune cache
//            recognizer.startRecognition(true);
//        }

        return setStatus(Status.ACTIVE);
    }

    @Override
    public Status deactivate() {
        return setStatus(Status.STANDBY);
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
        public static final String HI_IDEA = "hi idea";

        @Override
        public void run() {
            while (!isTerminated()) {
                // This blocks on a recognition result
                String result = getResultFromRecognizer();

                if (isInit()) {
                    if (result.equals(HI_IDEA)) {
                        // Greet invoker
                        say("Hi, Alexey");
                        invokeAction("Idear.Start");
                    }
                } else if (isActive()) {
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

            if (c.equals(HI_IDEA)) {
                // Greet some more
                say("Hi, again!");
            }

            else if (c.equals(FUCK)) {
                pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_Z);
                //invokeAction(IdeActions.ACTION_UNDO);
            }

            else if (c.startsWith("open")) {
                if (c.endsWith("settings")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_S);
                    //invokeAction(IdeActions.ACTION_SHOW_SETTINGS);
                } else if (c.endsWith("recent")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_E);
                    //invokeAction(IdeActions.ACTION_RECENT_FILES);
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
                if (c.contains("delete")) {
                    pressKeystroke(KeyEvent.VK_DELETE);
                } else if (c.contains("return")) {
                    pressKeystroke(KeyEvent.VK_ENTER);
                } else if (c.contains("escape")) {
                    pressKeystroke(KeyEvent.VK_ESCAPE);
                } else if (c.contains("tab")) {
                    pressKeystroke(KeyEvent.VK_TAB);
                } else if (c.contains("undo")) {
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
                fireVoiceCommand();
            }

            else if (c.startsWith(OKAY_GOOGLE) || c.startsWith(OK_GOOGLE)) {
                beep();
                fireGoogleSearch();
            }

            else if(c.contains("break point")) {
                if (c.startsWith("toggle")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_F8);
                } else if(c.startsWith("view")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_F8);
                }
            }

            else if (c.startsWith("step")) {
                if (c.endsWith("over")) {
                    pressKeystroke(KeyEvent.VK_F8);
                } else if (c.endsWith("into")) {
                    pressKeystroke(KeyEvent.VK_F7);
                }
            }
        }

        private void fireVoiceCommand() {
            try {
                Pair<String, Double> commandTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic());

                if (commandTuple == null || commandTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
                    return;

                // Notify of successful proceed
                beep();

                invokeAction(
                    "Idear.VoiceAction",
                    dataContext -> new AnActionEvent(
                                            null,
                                            SimpleDataContext.getSimpleContext(ExecuteVoiceCommandAction.KEY.getName(), commandTuple.first, dataContext),
                                            ActionPlaces.UNKNOWN,
                                            new Presentation(),
                                            ActionManager.getInstance(),
                                            0
                                        )
                );

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Panic! Failed to dump WAV", e);
            }

        }

        private void fireGoogleSearch() {

            try {
                Pair<String, Double> searchQueryTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic());

                if (searchQueryTuple == null || searchQueryTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
                    return;

                ServiceManager
                    .getService(TTSService.class)
                    .say("I think you said " + searchQueryTuple.first + ", searching Google now");

                GoogleHelper.searchGoogle(searchQueryTuple.first);

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Panic! Failed to dump WAV", e);
            }
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

    public boolean isInit() {
        return getStatus() == Status.INIT;
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
        invokeAction(
            action,
            dataContext ->
                new AnActionEvent(null,
                    dataContext,
                    ActionPlaces.UNKNOWN,
                    new Presentation(),
                    ActionManager.getInstance(),
                    0
                )
        );
    }

    private void invokeAction(String action, Function<DataContext, AnActionEvent> actionFactory) {
        DataManager.getInstance().getDataContextFromFocus().doWhenDone(
            (Consumer<DataContext>) dataContext -> EventQueue.invokeLater(() -> {
                AnAction anAction = ActionManager.getInstance().getAction(action);
                anAction.actionPerformed(actionFactory.apply(dataContext));
            })
        );
    }

    // Helpers

    public static synchronized void say(String something) {
        ServiceManager  .getService(TTSService.class)
                        .say(something);
    }

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
