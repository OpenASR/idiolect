package com.jetbrains.idear.asr;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.util.Pair;
import com.intellij.util.Consumer;
import com.jetbrains.idear.GoogleHelper;
import com.jetbrains.idear.WordToNumberConverter;
import com.jetbrains.idear.actions.ExecuteVoiceCommandAction;
import com.jetbrains.idear.actions.recognition.SurroundWithNoNullCheckRecognizer;
import com.jetbrains.idear.ide.IDEService;
import com.jetbrains.idear.recognizer.CustomLiveSpeechRecognizer;
import com.jetbrains.idear.recognizer.CustomMicrophone;
import com.jetbrains.idear.tts.TTSService;
import edu.cmu.sphinx.api.SpeechResult;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.event.KeyEvent.*;

/**
 * Created by breandan on 10/23/2015.
 */
public class ASRControlLoop implements Runnable {
    private final IDEService ideService;
    private CustomLiveSpeechRecognizer recognizer;

    public ASRControlLoop(CustomLiveSpeechRecognizer recognizer) {
        // Start-up recognition facilities
        this.recognizer = recognizer;

        this.ideService = ServiceManager.getService(IDEService.class);
    }


    private static final Logger logger = Logger.getLogger(ASRControlLoop.class.getSimpleName());

    public static final String OPEN = "open";
    public static final String SETTINGS = "settings";
    public static final String RECENT = "recent";
    public static final String TERMINAL = "terminal";
    public static final String FOCUS = "focus";
    public static final String EDITOR = "editor";
    public static final String PROJECT = "project";
    public static final String SELECTION = "selection";
    public static final String EXPAND = "grow";
    public static final String SHRINK = "shrink";
    public static final String PRESS = "press";
    public static final String DELETE = "delete";
    public static final String DEBUG = "debug";
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
    public static final String WHERE_AM_I = "where am i";
    public static final String NAVIGATE = "navigate";
    public static final String EXECUTE = "execute";
    public static final String GOTO = "goto line";

    @Override
    public void run() {
        while (!ListeningState.isTerminated()) {
            // This blocks on a recognition result
            String result = getResultFromRecognizer();

            if (ListeningState.isInit()) {
                if (result.equals(HI_IDEA)) {
                    // Greet invoker
                    say("Hi");
                    ideService.invokeAction("Idear.Start");
                }
            } else if (ListeningState.isActive()) {
                logger.log(Level.INFO, "Recognized: " + result);

                applyAction(result);
            }
        }
    }

    private String getResultFromRecognizer() {
        SpeechResult result = recognizer.getResult();

        System.out.println("Recognized: ");
        System.out.println("\tTop H:       " + result.getResult() + " / " + result.getResult().getBestToken() + " / " + result.getResult().getBestPronunciationResult());
        System.out.println("\tTop 3H:      " + result.getNbest(3));

        logger.info("Recognized:    ");
        logger.info("\tTop H:       " + result.getResult() + " / " + result.getResult().getBestToken() + " / " + result.getResult().getBestPronunciationResult());
        logger.info("\tTop 3H:      " + result.getNbest(3));

        Collection<String> c = result.getNbest(3);

        c.stream()
                .filter(s -> s.contains("something"));

        return result.getHypothesis();
    }

    private void applyAction(String c) {

        if (c.equals(HI_IDEA)) {
            // Greet some more
            say("Hi, again!");
        } else if (c.startsWith(OPEN)) {
            if (c.endsWith(SETTINGS)) {
                ideService.invokeAction(IdeActions.ACTION_SHOW_SETTINGS);
            } else if (c.endsWith(RECENT)) {
                ideService.invokeAction(IdeActions.ACTION_RECENT_FILES);
            } else if (c.endsWith(TERMINAL)) {
                pressKeystroke(VK_ALT, VK_F12);
            }
        } else if (c.startsWith(NAVIGATE)) {
            ideService.invokeAction("GotoDeclaration");
        } else if (c.startsWith(EXECUTE)) {
            ideService.invokeAction("Run");
        } else if (c.equals(WHERE_AM_I)) {
            // TODO(kudinkin): extract to action
            ideService.invokeAction("Idear.WhereAmI");
        } else if (c.startsWith("focus")) {
            if (c.endsWith(EDITOR)) {
                pressKeystroke(VK_ESCAPE);
            } else if (c.endsWith(PROJECT)) {
                pressKeystroke(VK_ALT, VK_1);
            } else if (c.endsWith("symbols")) {
                ideService.invokeAction("AceJumpAction");
                ideService.type(VK_SPACE);
                ideService.type(("" + recognizeNumber()).toCharArray());
            }
        } else if (c.startsWith(GOTO)) {

        } else if (c.startsWith(EXPAND)) {
//            ActionManager instance = ActionManager.getInstance();
//            AnAction a = instance.getAction("EditorSelectWord");
//            AnActionEvent event = new AnActionEvent(null, DataManager.getInstance().getDataContext(),
//                    ActionPlaces.UNKNOWN, a.getTemplatePresentation(), instance, 0);
//            a.actionPerformed(event);
            ideService.invokeAction("EditorSelectWord");
        } else if (c.startsWith(SHRINK)) {
            pressKeystroke(VK_CONTROL, VK_SHIFT, VK_W);
        } else if (c.startsWith("press")) {
            if (c.contains("delete")) {
                pressKeystroke(VK_DELETE);
            } else if (c.contains("return") || c.contains("enter")) {
                pressKeystroke(VK_ENTER);
            } else if (c.contains(ESCAPE)) {
                pressKeystroke(VK_ESCAPE);
            } else if (c.contains(TAB)) {
                pressKeystroke(VK_TAB);
            } else if (c.contains(UNDO)) {
                pressKeystroke(VK_CONTROL, VK_Z);
            }
        } else if (c.startsWith("following")) {
            if (c.endsWith("line")) {
                pressKeystroke(VK_DOWN);
            } else if (c.endsWith("page")) {
                pressKeystroke(VK_PAGE_DOWN);
            } else if (c.endsWith("method")) {
                pressKeystroke(VK_ALT, VK_DOWN);
            } else if (c.endsWith("tab")) {
                pressKeystroke(VK_CONTROL, VK_TAB);
            } else if (c.endsWith("page")) {
                pressKeystroke(VK_PAGE_DOWN);
            }
        } else if (c.startsWith("previous")) {
            if (c.endsWith("line")) {
                pressKeystroke(VK_UP);
            } else if (c.endsWith("page")) {
                pressKeystroke(VK_PAGE_UP);
            } else if (c.endsWith("method")) {
                pressKeystroke(VK_ALT, VK_UP);
            } else if (c.endsWith("page")) {
                pressKeystroke(VK_PAGE_UP);
            }
        } else if (c.startsWith("extract this")) {
            if (c.endsWith("method")) {
                pressKeystroke(VK_CONTROL, VK_ALT, VK_M);
            } else if (c.endsWith("parameter")) {
                pressKeystroke(VK_CONTROL, VK_ALT, VK_P);
            }
        } else if (c.startsWith("inspect code")) {
            pressKeystroke(VK_ALT, VK_SHIFT, VK_I);
        } else if (c.startsWith("speech pause")) {
            pauseSpeech();
        } else if (c.startsWith(OK_IDEA) || c.startsWith(OKAY_IDEA)) {
            beep();
            fireVoiceCommand();
        } else if (c.startsWith(OKAY_GOOGLE) || c.startsWith(OK_GOOGLE)) {
            beep();
            fireGoogleSearch();
        } else if (c.contains("break point")) {
            if (c.startsWith("toggle")) {
                pressKeystroke(VK_CONTROL, VK_F8);
            } else if (c.startsWith("view")) {
                pressKeystroke(VK_CONTROL, VK_SHIFT, VK_F8);
            }
        } else if (c.startsWith("debug")) {
            pressKeystroke(VK_SHIFT, VK_F9);
        } else if (c.startsWith("step")) {
            if (c.endsWith("over")) {
                pressKeystroke(VK_F8);
            } else if (c.endsWith("into")) {
                pressKeystroke(VK_F7);
            } else if (c.endsWith("return")) {
                pressKeystroke(VK_SHIFT, VK_F8);
            }
        } else if (c.startsWith("resume")) {
            pressKeystroke(VK_F9);
        } else if (c.startsWith("tell me a joke")) {
            tellJoke();
        } else if (c.contains("check")) {
            SurroundWithNoNullCheckRecognizer nullCheckRecognizer = new SurroundWithNoNullCheckRecognizer();
            if (nullCheckRecognizer.isMatching(c)) {
                DataManager.getInstance()
                        .getDataContextFromFocus()
                        .doWhenDone((Consumer<DataContext>) dataContext -> run(nullCheckRecognizer, c, dataContext));
            }
        }
    }

    private void pressKeystroke(final int... keys) {
        ServiceManager.getService(IDEService.class)
                .type(keys);
    }

    private void run(SurroundWithNoNullCheckRecognizer rec, String c, DataContext dataContext) {
        EventQueue.invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                rec.getActionInfo(c, dataContext);
            });
        });
    }

    private void tellJoke() {
        ServiceManager
                .getService(TTSService.class)
                .say("knock, knock, knock, knock, knock");

        String result = null;
        while (!"who is there".equals(result)) {
            result = getResultFromRecognizer();
        }

        ServiceManager
                .getService(TTSService.class)
                .say("Hang on, I will be right back");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ServiceManager
                .getService(TTSService.class)
                .say("Jah, jah, jav, jav, jav, a, a, a, va, va, va, va, va");

        while (!result.contains("wait who") &&
                !result.contains("who are you")) {
            result = getResultFromRecognizer();
        }

        ServiceManager
                .getService(TTSService.class)
                .say("Did I stutter? It is me, Jah java va va, va, va. Open up already!");
    }

    private static final long COMMAND_DURATION = 3500;

    private void fireVoiceCommand() {
        try {
            Pair<String, Double> commandTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic(COMMAND_DURATION));

            if (commandTuple == null || commandTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
                return;

            // Notify of successful proceed
            beep();

            ideService.invokeAction(
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

    private static final long GOOGLE_QUERY_DURATION = 3000;

    private void fireGoogleSearch() {

        try {
            Pair<String, Double> searchQueryTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic(GOOGLE_QUERY_DURATION));

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
        while (ListeningState.isActive()) {
            result = getResultFromRecognizer();
            if (result.equals("speech resume")) {
                break;
            }
        }
    }

    private int recognizeNumber() {
        String result;
        while (true) {
            result = getResultFromRecognizer();
            if (result.startsWith("jump "))
                return WordToNumberConverter.getNumber(result.substring(5));
        }
    }

    // Helpers

    public synchronized void say(String something) {
        ServiceManager.getService(TTSService.class)
                .say(something);
    }

    public static synchronized void beep() {
        Thread t = new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                        ASRService.class.getResourceAsStream("/com.jetbrains.idear/sounds/beep.wav"));
                clip.open(inputStream);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException ignored) {
        }
    }
}
