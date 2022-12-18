package org.openasr.idear.utils

import com.intellij.openapi.actionSystem.ActionManager

object ActionUtils {
    fun addActionWords(grammar: HashSet<String>) {
        for (actionId in ActionManager.getInstance().getActionIdList("")) {
            if (!ActionManager.getInstance().isGroup(actionId)) {
                grammar.addAll(actionId
                        .replace("$", "")
                        .replace(".", "")
                        .splitCamelCase())
            }
        }
    }
}
