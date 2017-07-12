package org.openasr.idear

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.johnlindquist.acejump.ui.AceUI
import org.openasr.idear.asr.ASRService
import org.openasr.idear.presentation.Icons


class VoiceRecordControllerAction : AnAction() {
    @Volatile private var isRecording = false
    lateinit var aceJumpDefaults: AceUI.UserSettings

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        aceJumpDefaults = AceUI.settings.copy()
        if (isRecording) {
            isRecording = false
            templatePresentation.icon = Icons.RECORD_START
            isDefaultIcon = false
            AceUI.settings = aceJumpDefaults
            ASRService.deactivate()
        } else {
            isRecording = true
            templatePresentation.icon = Icons.RECORD_END
            isDefaultIcon = false
            AceUI.settings.allowedChars = "1234567890".toList()
            ASRService.activate()
        }
    }
}
