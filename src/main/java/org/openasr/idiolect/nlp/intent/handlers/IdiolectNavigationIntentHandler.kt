package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileEditorManager
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.actions.recognition.IdiolectNavigationRecognizer
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.SpeechToFileName
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver

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
            IdiolectNavigationRecognizer.INTENT_SWITCH_TO_TAB -> switchToTab(nlpContext, nlpResponse.slots?.get(SLOT_NAME)!!)
            else -> return null
        }

        return ActionCallInfo(intentName, true)
    }

    private fun switchToTab(nlpContext: NlpContext, name: String) {
        val project = nlpContext.getProject()?.let { project ->
            val fileEditorManager: FileEditorManager = FileEditorManager.getInstance(project)

            val editorWindows = EditorFactory.getInstance().allEditors
            val predicate = SpeechToFileName.pickFileByAlias(project, name)
            val editor = editorWindows.firstOrNull { editor -> predicate.invoke(editor.virtualFile.name) }
            println("editor: " + editor.toString())

            if (editor != null) {
                invokeLater {
                    // open the file, even if it's not already open
                    fileEditorManager.openFile(editor.virtualFile, true)
                    // or only focus if it's already open...
//                    val openFile = OpenFileDescriptor(project, editor.virtualFile)
//                    fileEditorManager.openTextEditor(openFile, true)
                }
            }
        }
    }
}
