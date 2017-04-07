package com.jetbrains.idear.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.jetbrains.idear.actions.recognition.TextToActionConverter

class ExecuteActionFromPredefinedText : ExecuteActionByCommandText() {

    override fun actionPerformed(e: AnActionEvent) {
        val dataContext = e.dataContext
        val editor = CommonDataKeys.EDITOR.getData(dataContext)!!

        // String text = "idea extract to field";
        val text = "idea rename to my super test"
        // String text = "idea inline";

        val provider = TextToActionConverter(e.dataContext)
        val info = provider.extractAction(text)
        invoke(editor, info)
    }
}
