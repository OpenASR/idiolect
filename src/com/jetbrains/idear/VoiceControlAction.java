package com.jetbrains.idear;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Created by breandan on 6/23/2015.
 */
public class VoiceControlAction extends AnAction {
    private final Thread speechThread = new Thread(new ControlLoop());

    private static final String ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us";
    private static final String DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "resource:/com.jetbrains.idear/dialog/";
    private Keymap keymap;

    private Configuration configuration;
    private LiveSpeechRecognizer speechRecognizer;
    private Robot robot;
    private boolean stopped = false;

    public VoiceControlAction() {
        init();
    }

    public VoiceControlAction(Icon icon) {
        super(icon);
        init();
    }

    public VoiceControlAction(String text) {
        super(text);
        init();
    }

    public VoiceControlAction(String text, String description, Icon icon) {
        super(text, description, icon);
        init();
    }

    public void init() {
        configuration = new Configuration();
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(true);

        keymap = KeymapManager.getInstance().getActiveKeymap();
        configuration.setGrammarName("dialog");

        try {
            speechRecognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException e) {
            System.err.println("Couldn't initialize speech recognizer.");
        }

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        if (!speechThread.isAlive()) {
            stopped = false;
            speechRecognizer.startRecognition(true);
            speechThread.start();
        } else {
            speechRecognizer.stopRecognition();
            stopped = true;
        }
    }

    private class ControlLoop implements Runnable {
        @Override
        public void run() {
            while (!stopped) {
                String result = speechRecognizer.getResult().getHypothesis();
                System.out.println("User said: " + result);

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
                } else if(result.startsWith("extract this")) {
                    if (result.endsWith("method")) {
                        pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_M);
                    } else if (result.endsWith("parameter")) {
                        pressKeystroke(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_P);
                    }
                } else if (result.startsWith("following")) {
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
                } else if (result.startsWith("inspect code")) {
                    pressKeystroke(KeyEvent.VK_ALT, KeyEvent.VK_SHIFT, KeyEvent.VK_I);
                } if (result.startsWith("speech pause")) {
                    while (true) {
                        result = speechRecognizer.getResult().getHypothesis();
                        if (result.equals("speech resume")) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private void pressKeystroke(final int... keys) {
        for (int key : keys) {
            robot.keyPress(key);
        }

        for (int key : keys) {
            robot.keyRelease(key);
        }
    }

    private void invokeAction(final String actionShowSettings) {
        try {
            EventQueue.invokeAndWait(() -> {
                AnAction anAction = ActionManager.getInstance().getAction(actionShowSettings);
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
