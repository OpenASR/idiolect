package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.codeInsight.daemon.impl.ShowIntentionsPass
import com.intellij.ide.DataManager
import com.intellij.ide.actions.ApplyIntentionAction
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.ui.SearchTextField
import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.actions.recognition.*
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import org.openasr.idiolect.presentation.toolwindow.IdiolectToolWindowFactory
import org.openasr.idiolect.tts.TtsService
import java.util.ArrayList

/** idiolect-specific commands */
class IdiolectCommandIntentHandler : IntentHandler {
    companion object {
        val INTENT_PREFIX = IntentResolver.INTENT_PREFIX_IDIOLECT_COMMAND
        const val SLOT_COMMAND_TERM = "term"
    }

    override fun tryFulfillIntent(nlpResponse: NlpResponse, nlpContext: NlpContext): ActionCallInfo? {
        val intentName = nlpResponse.intentName

        if (!intentName.startsWith(INTENT_PREFIX)) {
            return null
        }

        when (intentName) {
            IdiolectCommandRecognizer.INTENT_HI -> TtsService.say("Hello there!")
            IdiolectCommandRecognizer.INTENT_ABOUT -> ActionRoutines.routineAbout()
            IdiolectCommandRecognizer.INTENT_PAUSE -> ActionRoutines.pauseSpeech()
            IdiolectCommandRecognizer.INTENT_COMMANDS -> showCommands(nlpContext, nlpResponse.slots?.get(SLOT_COMMAND_TERM))
            IdiolectCommandRecognizer.INTENT_EDIT_PHRASES -> editCustomPhrases(nlpContext)
            SurroundWithNoNullCheckRecognizer.INTENT_NAME -> surroundWithNullCheck(nlpContext)

            IdiolectCommandRecognizer.INTENT_ACTION_MODE -> actionMode(nlpContext)
            IdiolectCommandRecognizer.INTENT_EDIT_MODE -> editMode(nlpContext)
            IdiolectCommandRecognizer.INTENT_CHAT_MODE -> chatMode(nlpContext)

            else -> return null
        }

        return ActionCallInfo(intentName, true)
    }

    private fun editCustomPhrases(nlpContext: NlpContext) {
        val project = ProjectManager.getInstance().openProjects[0]
        CustomPhraseRecognizer.openCustomPhrasesFile(project)
        showCommands(nlpContext)
    }

    private fun actionMode(nlpContext: NlpContext) {
        nlpContext.setMode(NlpContext.Mode.ACTION)
    }

    private fun editMode(nlpContext: NlpContext) {
        nlpContext.setMode(NlpContext.Mode.EDIT)
    }

    private fun chatMode(nlpContext: NlpContext) {
        nlpContext.setMode(NlpContext.Mode.CHAT)

        IdiolectToolWindowFactory.showTab(IdiolectToolWindowFactory.Tab.CHAT)
    }

    private fun showCommands(nlpContext: NlpContext, term: String? = null) {
        val commandsContent = IdiolectToolWindowFactory.showTab(IdiolectToolWindowFactory.Tab.COMMANDS)

        // enter the term in the search field
        val searchTextField = commandsContent.searchComponent as SearchTextField
        searchTextField.text = term
        searchTextField.requestFocus()
    }

    private fun surroundWithNullCheck(nlpContext: NlpContext) {
        val dataContext = nlpContext.getDataContext()
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
