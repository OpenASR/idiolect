package org.openasr.idiolect.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.openasr.idiolect.asr.AsrService
import org.openasr.idiolect.nlp.NlpResultListener
import org.openasr.idiolect.presentation.Icons

object VoiceRecordControllerAction : IdiolectAction() {
    @Volatile private var isRecording = false
    private val messageBus = ApplicationManager.getApplication().messageBus

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        if (isRecording) {
            isRecording = false
            templatePresentation.icon = Icons.RECORD_START
            isDefaultIcon = false
            AsrService.deactivate()
        } else {
            isRecording = true
            templatePresentation.icon = Icons.RECORD_END
            isDefaultIcon = false
            AsrService.activate()
        }

        messageBus.syncPublisher(NlpResultListener.NLP_RESULT_TOPIC).onListening(isRecording)
    }
}
