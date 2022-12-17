package org.openasr.idear.actions

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.diagnostic.logger
import org.openasr.idear.actions.recognition.ActionCallInfo
import org.openasr.idear.actions.recognition.TextToActionConverter
import org.openasr.idear.ide.IdeService
import java.awt.Component

/**
 * Similar to ExecuteActionFromPredefinedText but uses the `VoiceCommand.Text` data attached to the invoking `AnActionEvent`
 */
object ExecuteVoiceCommandAction : ExecuteActionByCommandText() {
    private val log = logger<ExecuteVoiceCommandAction>()

    override fun actionPerformed(e: AnActionEvent) {
//        log.info("project: ${e.project}")
//        e.dataContext.getData()
//        val component = e.getData(PlatformCoreDataKeys.CONTEXT_COMPONENT)
//        log.info("component: $component")

        val provider = TextToActionConverter(e.dataContext)
        val info = provider.extractAction((e.inputEvent as SpeechEvent).utterance)
        if (info != null) {
            if (info != ActionCallInfo.RoutineActioned) {
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
