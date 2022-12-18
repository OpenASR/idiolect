package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import org.openasr.idear.utils.toUpperCamelCase
import java.awt.Component

/**
 * As a near-last resort, attempt to match a registered Action ID, prefixed with "Editor"
 *
 * @see com.intellij.openapi.actionSystem.IdeActions - ACTION_EDITOR_*
 */
open class RegisteredEditorActionRecognizer : RegisteredActionRecognizer() {
    override fun isSupported(dataContext: DataContext, component: Component?): Boolean {
        return component is EditorComponentImpl
    }

    override fun getActionIdForUtterance(utterance: String): String {
        val actionId = mapOf(
                "whoops" to "undo",
                "carrot" to "caret"
                )
                .getOrDefault(utterance, utterance)
                .toUpperCamelCase()

        if (arrayOf(//"Copy", "Cut", "Delete", "Paste",
                        "SelectAll",
                        "Undo", "Redo").contains(actionId)) {
            return "$$actionId"
        }
        return "Editor$actionId"
    }
}
