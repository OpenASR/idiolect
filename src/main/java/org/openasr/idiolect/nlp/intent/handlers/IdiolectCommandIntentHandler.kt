package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass
import com.intellij.ide.DataManager
import com.intellij.ide.actions.ApplyIntentionAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.actions.recognition.*
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import org.openasr.idiolect.tts.TtsService
import java.util.ArrayList

/** idiolect-specific commands */
class IdiolectCommandIntentHandler : IntentHandler {
    companion object {
        val INTENT_PREFIX = IntentResolver.INTENT_PREFIX_IDIOLECT_COMMAND
    }

    override fun tryFulfillIntent(nlpResponse: NlpResponse, dataContext: DataContext): ActionCallInfo? {
        val intentName = nlpResponse.intentName

        if (!intentName.startsWith(INTENT_PREFIX)) {
            return null
        }

        when (intentName) {
            IdiolectCommandRecognizer.INTENT_HI -> TtsService.say("Hi!")
            IdiolectCommandRecognizer.INTENT_ABOUT -> ActionRoutines.routineAbout()
            IdiolectCommandRecognizer.INTENT_PAUSE -> ActionRoutines.pauseSpeech()
            SurroundWithNoNullCheckRecognizer.INTENT_NAME -> surroundWithNullCheck(dataContext)
            else -> return null
        }

        return ActionCallInfo(intentName, true)
    }

    private fun surroundWithNullCheck(dataContext: DataContext) {
        val editor = IdeService.getEditor(dataContext)

//        val project = IdeService.getProject(dataContext)
        if (editor != null) {
            val file = getFile(dataContext, editor)

            if (file != null) {
                val info = ShowIntentionsPass.IntentionsInfo()
                ApplicationManager.getApplication().runReadAction { ShowIntentionsPass.getActionsToShow(editor, file, info, -1) }
                if (info.isEmpty) return

                val actions = ArrayList(info.run { errorFixesToShow + inspectionFixesToShow + intentionsToShow })
                val result = arrayOfNulls<ApplyIntentionAction>(actions.size)
                for (i in result.indices) {
                    val descriptor = actions[i]
                    val actionText = ApplicationManager.getApplication().runReadAction(Computable { descriptor.action.text })
                    result[i] = ApplyIntentionAction(descriptor, actionText, editor, file)
                }

                DataManager.getInstance().run {
                    val context = getDataContext(editor.contentComponent)
                    result[1]!!.actionPerformed(
                        AnActionEvent(null, context, "", Presentation("surround with not null"),
                        ActionManager.getInstance(), 0)
                    )
                }
            }
        }
    }

    private fun getFile(dataContext: DataContext, editor: Editor): PsiFile? =
        IdeService.getProject(dataContext)?.let { project ->
            PsiDocumentManager.getInstance(project).getPsiFile(editor.document)
        }
}
