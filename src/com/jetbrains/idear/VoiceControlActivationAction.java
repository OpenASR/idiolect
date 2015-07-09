package com.jetbrains.idear;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;


public class VoiceControlActivationAction extends AnAction {

    public VoiceControlActivationAction() {
    }

    public VoiceControlActivationAction(Icon icon) {
        super(icon);
    }

    public VoiceControlActivationAction(String text) {
        super(text);
    }

    public VoiceControlActivationAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        ServiceManager.getService(ASRService.class).activate();
    }
}
