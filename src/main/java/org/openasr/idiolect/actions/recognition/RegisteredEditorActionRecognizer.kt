package org.openasr.idiolect.actions.recognition

import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpGrammar
import org.openasr.idiolect.utils.toUpperCamelCase
import java.awt.Component

/**
 * As a near-last resort, attempt to match a registered Action ID, prefixed with "Editor"
 *
 * @see com.intellij.openapi.actionSystem.IdeActions - ACTION_EDITOR_*
 */
open class RegisteredEditorActionRecognizer : RegisteredActionRecognizer() {
    override val displayName = "Editor Actions"
    override val order = Int.MAX_VALUE - 1

    override fun buildGrammars() = listOf(
        NlpGrammar("Undo").withExamples("undo", "whoops"),
    )

    override fun isSupported(context: NlpContext, component: Component?): Boolean =
        context.isActionMode() &&
        component is EditorComponentImpl

    override fun getActionIdForUtterance(utterance: String): String {
        val actionId = mapOf(
                "whoops" to "undo",
                "carrot" to "caret"
                )
                .getOrDefault(utterance, utterance)
                .toUpperCamelCase()

        return if (actionId in setOf(//"Copy", "Cut", "Delete", "Paste",
                        "SelectAll", "Undo", "Redo"))
            "$$actionId"
        else "Editor$actionId"
    }
}
