package com.jetbrains.idear;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ApplicationComponent;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;

/**
 * Created by breandan on 6/22/2015.
 */
public class Idear implements ApplicationComponent {

    private static final String ACOUSTIC_MODEL = "resource:/edu.cmu.sphinx.models.en-us/en-us";
    private static final String DICTIONARY_PATH = "resource:/edu.cmu.sphinx.models.en-us/cmudict-en-us.dict";
    private static final String GRAMMAR_PATH = "resource:/com.jetbrains.idear/dialog/";

    private Configuration configuration;
    private LiveSpeechRecognizer speechRecognizer;

    public Idear() {
        configuration = new Configuration();
        configuration.setAcousticModelPath(ACOUSTIC_MODEL);
        configuration.setDictionaryPath(DICTIONARY_PATH);
        configuration.setGrammarPath(GRAMMAR_PATH);
        configuration.setUseGrammar(true);

        configuration.setGrammarName("dialog");
        try {
            speechRecognizer = new LiveSpeechRecognizer(configuration);
        } catch (IOException e) {
            System.err.println("Couldn't initialize speech recognizer.");
        }
    }

    @Override
    public void initComponent() {
        speechRecognizer.startRecognition(true);
        new Thread(() -> {
            while (true) {
                String utterance = speechRecognizer.getResult().getHypothesis();

                System.out.println("User said: " + utterance);

                if (utterance.startsWith("open")) {
                    if (utterance.endsWith("settings")) {
                            invokeAction(IdeActions.ACTION_SHOW_SETTINGS);
                    }
                }


                if (utterance.startsWith("focus")) {
                    if (utterance.endsWith("editor")) {
                        invokeAction(IdeActions.ACTION_EDITOR_ESCAPE);
                    } else if (utterance.endsWith("project")) {
                        invokeAction(IdeActions.GROUP_PROJECT_VIEW_POPUP);
                    }
                }

                if (utterance.endsWith("selection")) {
                    if (utterance.startsWith("expand")) {
                        invokeAction(IdeActions.ACTION_EXPAND_REGION_RECURSIVELY);
                    } else if (utterance.startsWith("shrink")) {
                        invokeAction(IdeActions.ACTION_COLLAPSE_REGION_RECURSIVELY);
                    }
                }

                if (utterance.startsWith("speech pause")) {
                    while (true) {
                        utterance = speechRecognizer.getResult().getHypothesis();
                        if (utterance.equals("speech resume")) {
                            break;
                        }
                    }
                }
            }
        }).start();
    }

    private void invokeAction(final String actionShowSettings) {
        try {
            EventQueue.invokeAndWait(() -> {
                AnAction aa = ActionManager.getInstance().getAction(actionShowSettings);
                aa.actionPerformed(new AnActionEvent(null,
                        DataManager.getInstance().getDataContext(),
                        ActionPlaces.UNKNOWN, aa.getTemplatePresentation(),
                        ActionManager.getInstance(), 0));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disposeComponent() {
        speechRecognizer.stopRecognition();
    }

    @Override
    @NotNull
    public String getComponentName() {
        return "Idear";
    }
}
