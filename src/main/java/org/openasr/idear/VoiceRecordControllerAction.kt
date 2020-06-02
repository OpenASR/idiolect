package org.openasr.idear

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.ServiceManager
import org.acejump.config.AceConfig
import org.acejump.config.AceSettings
import org.openasr.idear.actions.IdearAction
import org.openasr.idear.asr.ASRService
import org.openasr.idear.presentation.Icons

object VoiceRecordControllerAction : IdearAction() {
    @Volatile private var isRecording = false

    override fun actionPerformed(anActionEvent: AnActionEvent) {
      val settings = ServiceManager.getService(AceConfig::class.java).state
      val aceJumpDefaults =settings.allowedChars
        if (isRecording) {
            isRecording = false
            templatePresentation.icon = Icons.RECORD_START
            isDefaultIcon = false
            settings.allowedChars = aceJumpDefaults
            ASRService.deactivate()
        } else {
            isRecording = true
            templatePresentation.icon = Icons.RECORD_END
            isDefaultIcon = false
            settings.allowedChars = "1234567890"
            ASRService.activate()
        }
    }
}