package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext

class RenameActionRecognizer : ActionRecognizer {
    override fun isMatching(sentence: String) = "rename" in sentence

    override fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo? {
        if (!isMatching(sentence)) return null

        val words = sentence.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val renameIndex = words.indices.firstOrNull { "rename" in words[it] } ?: 0

        val newName = StringBuilder()
        var first = true
        for (i in renameIndex + 2 until words.size) {
            val word = if (first) words[i] else words[i].toUpperCase()
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
