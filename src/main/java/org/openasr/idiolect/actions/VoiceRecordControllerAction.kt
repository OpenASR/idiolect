package org.openasr.idiolect.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import org.openasr.idiolect.asr.AsrService
import org.openasr.idiolect.nlp.NlpResultListener
import org.openasr.idiolect.presentation.Icons

/**
 * Toggles the listening status of the ASR Service
 */
object VoiceRecordControllerAction : IdiolectAction() {
    private val asrService = service<AsrService>()
    @Volatile private var isRecording = false
    private val messageBus = ApplicationManager.getApplication().messageBus

    override fun actionPerformed(anActionEvent: AnActionEvent) {
        if (isRecording) {
            isRecording = false
            templatePresentation.icon = Icons.RECORD_START
            isDefaultIcon = false
            asrService.deactivate()
        } else {
            isRecording = true
            templatePresentation.icon = Icons.RECORD_END
            isDefaultIcon = false
            asrService.activate()
        }

        messageBus.syncPublisher(NlpResultListener.NLP_RESULT_TOPIC).onListening(isRecording)
    }
}
