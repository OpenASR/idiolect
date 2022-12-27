package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.logger
import org.openasr.idear.actions.recognition.ActionRecognizerManager
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.NlpResultListener.Companion.NLP_RESULT_TOPIC

/**
 * Similar to ExecuteActionFromPredefinedText but uses the `VoiceCommand.Text` data attached to the invoking `AnActionEvent`
 */
object ExecuteVoiceCommandAction : ExecuteActionByCommandText() {
    private val log = logger<ExecuteVoiceCommandAction>()
    private val messageBus = ApplicationManager.getApplication().messageBus

    override fun actionPerformed(e: AnActionEvent) {
//        log.info("project: ${e.project}")
//        e.dataContext.getData()
//        val component = e.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT)
//        log.info("component: $component")

        val provider = ActionRecognizerManager(e.dataContext)
        val info = provider.handleNlpRequest((e.inputEvent as SpeechEvent).nlpRequest)

        if (info != null) {
            messageBus.syncPublisher(NLP_RESULT_TOPIC).onFulfilled(info)

            if (!info.fullfilled) {
                val editor = IdeService.getEditor(e.dataContext)
                if (editor == null) {
                    log.info("Invoking outside of editor: ${info.actionId}")
                    IdeService.invokeAction(info.actionId)
                } else {
                    runInEditor(editor, info)
                }
            }
        } else {
            log.info("Command not recognized")
        }
    }
}
