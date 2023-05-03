package org.openasr.idiolect.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import org.openasr.idiolect.actions.recognition.ActionRecognizerManager
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpResultListener.Companion.NLP_RESULT_TOPIC

/**
 * Similar to ExecuteActionFromPredefinedText but uses the `VoiceCommand.Text` data attached to the invoking `AnActionEvent`
 */
object ExecuteVoiceCommandAction : ExecuteActionByCommandText() {
    private val log = logger<ExecuteVoiceCommandAction>()
    private val messageBus = ApplicationManager.getApplication().messageBus

    override fun actionPerformed(e: AnActionEvent) {
        val manager = ActionRecognizerManager(NlpContext(e.dataContext))
        val nlpRequest = (e.inputEvent as SpeechEvent).nlpRequest
        val info = manager.handleNlpRequest(nlpRequest)

        if (info != null) {
            if (!info.fulfilled) {
                try {
                    val editor = IdeService.getEditor(e.dataContext)
                    if (editor == null) {
                        log.info("Invoking outside of editor: ${info.actionId}")
                        IdeService.invokeAction(info.actionId)
                    } else runInEditor(editor, info)
                } catch (err: Exception) {
                    log.error("Failed to execute ${info.actionId}", err)
                    messageBus.syncPublisher(NLP_RESULT_TOPIC).onFailure("Failed to execute '${info.actionId}'")
                    return
                }
            }

            messageBus.syncPublisher(NLP_RESULT_TOPIC).onFulfilled(info)
        } else {
            log.info("Command not recognized")
//            messageBus.syncPublisher(NLP_RESULT_TOPIC).onNoMatch(nlpRequest)
        }
    }
}
