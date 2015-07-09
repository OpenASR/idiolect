package com.jetbrains.idear;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;

import javax.swing.*;


public class VoiceControlDeactivationAction extends AnAction {

    public VoiceControlDeactivationAction() {
    }

    public VoiceControlDeactivationAction(Icon icon) {
        super(icon);
    }

    public VoiceControlDeactivationAction(String text) {
        super(text);
    }

    public VoiceControlDeactivationAction(String text, String description, Icon icon) {
        super(text, description, icon);
    }

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        ServiceManager.getService(ASRService.class).deactivate();
    }
}
