package com.jetbrains.idear;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class ASRServiceImpl implements ASRService {

    private final Thread speechThread = new Thread(new ASRControlLoop(), "ARS Thread");

    private static final String ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us";
    private static final String DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "resource:/com.jetbrains.idear/dialog/";

    private static final Logger logger = Logger.getInstance(ASRServiceImpl.class);

    private LiveSpeechRecognizer recognizer;
    private Robot robot;

    private AtomicReference<Status> status = new AtomicReference<>(Status.INACTIVE);

    /* package */ void init() {
        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(true);

        configuration.setGrammarName("dialog");

        try {

            recognizer = new LiveSpeechRecognizer(configuration);

        } catch (IOException e) {
            System.err.println("Couldn't initialize speech recognizer.");
        }

        try {

            robot = new Robot();

        } catch (AWTException e) {
            e.printStackTrace();
        }

        // Fire up control-loop

        speechThread.start();
    }

    /* package */ void dispose() {

        // Deactivate in the first place, therefore actually
        // prevent activation upon the user-input

        deactivate();

        terminate();
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

        recognizer.startRecognition(true);

        return setStatus(Status.ACTIVE);
    }

    @Override
    public Status deactivate() {
        if (getStatus() == Status.INACTIVE)
            return Status.INACTIVE;

        Status s;

        try {

            recognizer.stopRecognition();

        } finally {
            s = setStatus(Status.INACTIVE);
        }

        return s;
    }

    private void terminate() {
        setStatus(Status.TERMINATED);
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

        @Override
        public void run() {
            while (!isTerminated()) {
                if (isActive()) {
                    String result = recognizer.getResult().getHypothesis();

                    //logger.info("Recognized: " + result);
                    System.out.println("Recognized: " + result);

                    // Ok, something screwed up

                    if (result.equals(FUCK))
                        invokeAction(IdeActions.ACTION_UNDO);

                    if (result.startsWith(OPEN)) {
                        if (result.endsWith(SETTINGS)) {
                            invokeAction(IdeActions.ACTION_SHOW_SETTINGS);
                        } else if (result.endsWith(RECENT)) {
                            invokeAction(IdeActions.ACTION_RECENT_FILES);
                        } else if (result.endsWith(TERMINAL)) {
                            pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_F12);
                        }
                    } else if (result.startsWith(FOCUS)) {
                        if (result.endsWith(EDITOR)) {
                            pressKeystroke(KeyEvent.VK_ESCAPE);
                        } else if (result.endsWith(PROJECT)) {
                            pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_1);
                        }
                    } else if (result.endsWith(SELECTION)) {
                        if (result.startsWith(EXPAND)) {
                            pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_W);
                        } else if (result.startsWith(SHRINK)) {
                            pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_W);
                        }
                    } else if (result.startsWith(PRESS)) {
                        if (result.endsWith(DELETE)) {
                            pressKeystroke(KeyEvent.VK_DELETE);
                        } else if (result.endsWith(ENTER)) {
                            pressKeystroke(KeyEvent.VK_ENTER);
                        } else if (result.endsWith(ESCAPE)) {
                            pressKeystroke(KeyEvent.VK_ESCAPE);
                        } else if (result.endsWith(TAB)) {
                            pressKeystroke(KeyEvent.VK_TAB);
                        } else if (result.endsWith(UNDO)) {
                            pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_Z);
                        }
                    } else if (result.startsWith(NEXT)) {
                        if (result.endsWith(LINE)) {
                            pressKeystroke(KeyEvent.VK_DOWN);
                        } else if (result.endsWith(PAGE)) {
                            pressKeystroke(KeyEvent.VK_PAGE_DOWN);
                        } else if (result.endsWith(METHOD)) {
                            pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_DOWN);
                        }
                    } else if (result.startsWith(PREVIOUS)) {
                        if (result.endsWith(LINE)) {
                            pressKeystroke(KeyEvent.VK_UP);
                        } else if (result.endsWith(PAGE)) {
                            pressKeystroke(KeyEvent.VK_PAGE_UP);
                        } else if (result.endsWith(METHOD)) {
                            pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_UP);
                        }
                    } else if (result.startsWith(INSPECT_CODE)) {
                        pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_SHIFT, KeyEvent.VK_I);
                    }

                    if (result.startsWith("speech pause")) {
                        while (true) {
                            result = recognizer.getResult().getHypothesis();
                            if (result.equals("speech resume")) {
                                break;
                            }
                        }
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
