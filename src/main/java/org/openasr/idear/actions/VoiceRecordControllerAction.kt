package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.openasr.idear.asr.AsrService
import org.openasr.idear.nlp.NlpResultListener
import org.openasr.idear.presentation.Icons

object VoiceRecordControllerAction : IdearAction() {
    @Volatile private var isRecording = false
    private val messageBus = ApplicationManager.getApplication().messageBus

    override fun actionPerformed(anActionEvent: AnActionEvent) {
//      val settings = ApplicationManager.getApplication().getService(AceConfig::class.java).state
//      val aceJumpDefaults = settings.allowedChars
        if (isRecording) {
            isRecording = false
            templatePresentation.icon = Icons.RECORD_START
            isDefaultIcon = false
//            settings.allowedChars = aceJumpDefaults
            AsrService.deactivate()
        } else {
            isRecording = true
            templatePresentation.icon = Icons.RECORD_END
            isDefaultIcon = false
//            settings.allowedChars = "1234567890"
            AsrService.activate()
        }

        messageBus.syncPublisher(NlpResultListener.NLP_RESULT_TOPIC).onListening(isRecording)
    }
}
