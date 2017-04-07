package com.jetbrains.idear

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import com.jetbrains.idear.asr.ASRService
import com.jetbrains.idear.presentation.Icons

class VoiceRecordControllerAction : AnAction() {
    @Volatile private var isRecording = false

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        val recognitionService = ServiceManager.getService(ASRService::class.java)
        if (isRecording) {
            isRecording = false
            templatePresentation.icon = Icons.RECORD_START
            isDefaultIcon = false
            recognitionService.deactivate()
        } else {
            isRecording = true
            templatePresentation.icon = Icons.RECORD_END
            isDefaultIcon = false
            recognitionService.activate()
        }
    }

}
