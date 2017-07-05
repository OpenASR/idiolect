package com.jetbrains.idear.settings;

import javax.swing.*;

public class RecognitionSettingsForm {
    private JRadioButton recCMUSphinx;
    private JRadioButton recAWSLex;
    private JRadioButton recApiAi;
    private JRadioButton ttsMmary;
    private JRadioButton ttsAWSPolly;
    private JRadioButton ttsApiAi;
    private JPanel rootPanel;

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public RecognitionServiceId getRecognitionService() {
        if (recAWSLex.isSelected()) {
            return RecognitionServiceId.AWS_LEX;
        }
        if (recApiAi.isSelected()) {
            return RecognitionServiceId.API_AI;
        }

        return RecognitionServiceId.CMU_SPHINX;
    }

    public TtsServiceId getTtsService() {
        if (ttsAWSPolly.isSelected()) {
            return TtsServiceId.AWS_POLLY;
        }
        if (ttsApiAi.isSelected()) {
            return TtsServiceId.API_AI;
        }

        return TtsServiceId.MARY;
    }

    public void setRecognitionService(RecognitionServiceId value) {
        switch (value) {
            case AWS_LEX:
                recAWSLex.setSelected(true);
                break;
            case API_AI:
                recApiAi.setSelected(true);
                break;
            case CMU_SPHINX:
                recCMUSphinx.setSelected(true);
                break;
        }
    }

    public void setTtsService(TtsServiceId value) {
        switch (value) {
            case AWS_POLLY:
                ttsAWSPolly.setSelected(true);
                break;
            case API_AI:
                ttsApiAi.setSelected(true);
                break;
            case MARY:
                ttsMmary.setSelected(true);
                break;
        }
    }
}
