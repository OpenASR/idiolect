package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.DataContext

class RenameActionRecognizer : ActionRecognizer {

    override fun isMatching(sentence: String): Boolean {
        return sentence.contains("rename")
    }

    override fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo? {
        if (!isMatching(sentence)) return null

        val words = sentence.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var renameIndex = 0
        for (i in words.indices) {
            if (words[i].contains("rename")) {
                renameIndex = i
                break
            }
        }

        val newName = StringBuilder()
        var first = true
        for (i in renameIndex + 2..words.size - 1) {
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
