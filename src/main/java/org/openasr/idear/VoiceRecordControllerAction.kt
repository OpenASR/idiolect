package org.openasr.idear

import com.intellij.openapi.actionSystem.AnActionEvent
import org.acejump.config.AceConfig
import org.openasr.idear.actions.IdearAction
import org.openasr.idear.asr.ASRService
import org.openasr.idear.presentation.Icons

object VoiceRecordControllerAction : IdearAction() {
    @Volatile private var isRecording = false
    private var aceJumpDefaults = AceConfig.state.allowedChars

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        if (isRecording) {
            isRecording = false
            templatePresentation.icon = Icons.RECORD_START
            isDefaultIcon = false
            AceConfig.state.allowedChars = aceJumpDefaults
            ASRService.deactivate()
        } else {
            isRecording = true
            templatePresentation.icon = Icons.RECORD_END
            isDefaultIcon = false
            AceConfig.state.allowedChars = "1234567890"
            ASRService.activate()
        }
    }
}