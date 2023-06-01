package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.editor.EditorFactory
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.actions.recognition.IdiolectNavigationRecognizer
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.SpeechToFileName
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import org.openasr.idiolect.psi.PsiUtil

/** idiolect-specific commands */
class IdiolectNavigationIntentHandler : IntentHandler {
    companion object {
        val INTENT_PREFIX = IntentResolver.INTENT_PREFIX_IDIOLECT_NAVIGATION
        const val SLOT_NAME = "name"
    }

    override fun tryFulfillIntent(nlpResponse: NlpResponse, nlpContext: NlpContext): ActionCallInfo? {
        val intentName = nlpResponse.intentName

        if (!intentName.startsWith(INTENT_PREFIX)) {
            return null
        }

        when (intentName) {
            IdiolectNavigationRecognizer.INTENT_OPEN_PROJECT_FILE -> openProjectFile(nlpContext, nlpResponse.slots?.get(SLOT_NAME)!!)
            IdiolectNavigationRecognizer.INTENT_SWITCH_TO_TAB -> switchToTab(nlpContext, nlpResponse.slots?.get(SLOT_NAME)!!)
            else -> return null
        }

        return ActionCallInfo(intentName, true)
    }

    private fun openProjectFile(nlpContext: NlpContext, name: String) {
        nlpContext.getProject()?.let { project ->
            ApplicationManager.getApplication().runReadAction {
                val projectFiles = PsiUtil.getAllFilesInProject(project)

                val predicate = SpeechToFileName.pickFileByAlias(project, name)
                val file = projectFiles.firstOrNull { predicate.invoke(it.name) }

                if (file != null) {
                    invokeLater {
                        PsiUtil.openFileInEditor(project, file)
                    }
                }
            }
        }
    }

    private fun switchToTab(nlpContext: NlpContext, name: String) {
        nlpContext.getProject()?.let { project ->
            val editorWindows = EditorFactory.getInstance().allEditors
            val predicate = SpeechToFileName.pickFileByAlias(project, name)
            val editor = editorWindows.firstOrNull { editor -> predicate.invoke(editor.virtualFile.name) }

            if (editor != null) {
                invokeLater {
                    PsiUtil.focusFileInEditor(project, editor.virtualFile)
                }
            }
        }
    }
}
