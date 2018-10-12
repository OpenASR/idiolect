package org.openasr.idear.asr

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.diagnostic.Logger

object GrammarService : AnActionListener {
    private val logger = Logger.getInstance(javaClass)

    fun init() = ActionManager.getInstance().addAnActionListener(this)

    override fun beforeActionPerformed(anAction: AnAction,
                                       dataContext: DataContext,
                                       anActionEvent: AnActionEvent) {
        val actionId = ActionManager.getInstance().getId(anAction)

        if ("someaction" == actionId) {
            logger.info("Swapping in grammar for action: " + anAction.toString())
            //swap in a context aware grammar
        }
    }

    override fun afterActionPerformed(anAction: AnAction?, dataContext: DataContext, anActionEvent: AnActionEvent?) = //swap out a context aware grammar
            Unit

    override fun beforeEditorTyping(c: Char, dataContext: DataContext) = Unit
}
