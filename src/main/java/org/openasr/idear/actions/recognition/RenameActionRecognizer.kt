package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import java.awt.Component

/**
 * "rename"
 */
class RenameActionRecognizer : ActionRecognizer {
    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
    override fun isMatching(utterance: String) = "rename" in utterance

    override fun getActionInfo(utterance: String, dataContext: DataContext): ActionCallInfo? {
        if (!isMatching(utterance)) return null

        val words = utterance.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val renameIndex = words.indices.firstOrNull { "rename" in words[it] } ?: 0

        val newName = StringBuilder()
        var first = true
        for (i in renameIndex + 2 until words.size) {
            val word = if (first) words[i] else words[i].uppercase()
            newName.append(word)
            first = false
        }

        val info = ActionCallInfo("RenameElement")
        if (newName.isNotEmpty()) {
            info.typeAfter = newName.toString()
            info.hitTabAfter = true
        }

        return info
    }
}
