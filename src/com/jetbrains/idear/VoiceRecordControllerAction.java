package com.jetbrains.idear;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;

public class VoiceRecordControllerAction extends AnAction {
    private volatile boolean isRecording = false;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        ASRService recognitionService = ServiceManager.getService(ASRService.class);
        if (isRecording) {
            isRecording = false;
            getTemplatePresentation().setIcon(Icons.RECORD_START);
            setDefaultIcon(false);
            recognitionService.deactivate();
        }
        else {
            isRecording = true;
            getTemplatePresentation().setIcon(Icons.RECORD_END);
            setDefaultIcon(false);
            recognitionService.activate();
        }
    }

}
