package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.impl.text.TextEditorComponent
import io.ktor.util.reflect.*
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.actions.recognition.IdiolectNavigationRecognizer
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.SpeechToFileName
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import javax.swing.JComponent

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
        val editorWindows = EditorFactory.getInstance().allEditors
        val project = nlpContext.getProject()
        val predicate = SpeechToFileName.pickFileByAlias(project, name)

        editorWindows.firstOrNull { editor -> predicate.invoke(editor.virtualFile.name) }
            ?.component?.apply {
                var pane: JComponent? = this
                while (pane != null && !pane.instanceOf(TextEditorComponent::class)) { pane = pane.rootPane }

//                val pane = rootPane.rootPane
                println("Found a tab for $name, requesting focus")
                pane?.isVisible = true
                pane?.requestFocus()            }
    }
}
