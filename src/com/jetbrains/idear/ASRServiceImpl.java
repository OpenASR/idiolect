package com.jetbrains.idear;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.diagnostic.Logger;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.util.props.ConfigurationManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;

public class ASRServiceImpl implements ASRService {
    private static final Logger logger = Logger.getInstance(ASRServiceImpl.class);

    private final Thread speechThread = new Thread(new ASRControlLoop(), "ARS Thread");

    private static final String ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us";
    private static final String CONFIGURATION_PATH = "resource:/com.jetbrains.idear/co";
    private static final String DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "resource:/com.jetbrains.idear/grammars";

    private ConfigurationManager configurationManager;
    private LiveSpeechRecognizer recognizer;
    private Robot robot;

    private final AtomicReference<Status> status = new AtomicReference<>(Status.INIT);

    public void init() {
        configurationManager = new ConfigurationManager(CONFIGURATION_PATH);
        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(true);

        configuration.setGrammarName("dialog");

        try {
            recognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException e) {
            logger.error("Couldn't initialize speech recognizer:", e);
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
                    logger.debug("Recognized: " + result);
                    applyAction(result);
                }
            }
        }

        private String getResultFromRecognizer() {
            return recognizer.getResult().getHypothesis();
        }

        private void applyAction(String result) {
            if (result.startsWith("open")) {
                if (result.endsWith("settings")) {
                    invokeAction(IdeActions.ACTION_SHOW_SETTINGS);
                } else if (result.endsWith("recent")) {
                    invokeAction(IdeActions.ACTION_RECENT_FILES);
                } else if (result.endsWith("terminal")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_F12);
                }
            } else if (result.startsWith("focus")) {
                if (result.endsWith("editor")) {
                    pressKeystroke(KeyEvent.VK_ESCAPE);
                } else if (result.endsWith("project")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_1);
                }
            } else if (result.endsWith("selection")) {
                if (result.startsWith("expand")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_W);
                } else if (result.startsWith("shrink")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_W);
                }
            } else if (result.startsWith("press")) {
                if (result.endsWith("delete")) {
                    pressKeystroke(KeyEvent.VK_DELETE);
                } else if (result.endsWith("enter")) {
                    pressKeystroke(KeyEvent.VK_ENTER);
                } else if (result.endsWith("escape")) {
                    pressKeystroke(KeyEvent.VK_ESCAPE);
                } else if (result.endsWith("tab")) {
                    pressKeystroke(KeyEvent.VK_TAB);
                } else if (result.endsWith("undo")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_Z);
                }
            } else if (result.startsWith("next")) {
                if (result.endsWith("line")) {
                    pressKeystroke(KeyEvent.VK_DOWN);
                } else if (result.endsWith("page")) {
                    pressKeystroke(KeyEvent.VK_PAGE_DOWN);
                } else if (result.endsWith("method")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_DOWN);
                }
            } else if (result.startsWith("previous")) {
                if (result.endsWith("line")) {
                    pressKeystroke(KeyEvent.VK_UP);
                } else if (result.endsWith("page")) {
                    pressKeystroke(KeyEvent.VK_PAGE_UP);
                } else if (result.endsWith("method")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_UP);
                }
            } else if (result.startsWith("extract this")) {
                if (result.endsWith("method")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_M);
                } else if (result.endsWith("parameter")) {
                    pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_P);
                }
            } else if (result.startsWith("inspect code")) {
                pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_SHIFT, KeyEvent.VK_I);
            } else if (result.startsWith("speech pause")) {
                pauseSpeech();
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
            logger.error("Could not invoke action:", e);
        }
    }
}
