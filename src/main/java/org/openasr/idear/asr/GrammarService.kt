package org.openasr.idear.asr

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer

object GrammarService : Disposable {
    private val logger = Logger.getInstance(GrammarService::class.java)
    override fun dispose() {
        Disposer.dispose(this)
    }

    fun init() {
        val actionManager = ActionManager.getInstance()

        actionManager.addAnActionListener(GrammarListener(actionManager))
    }

    class GrammarListener(private val actionManager: ActionManager) : AnActionListener {
        override fun beforeActionPerformed(anAction: AnAction,
                                           dataContext: DataContext,
                                           anActionEvent: AnActionEvent) {
            val actionId = actionManager.getId(anAction)

            if ("someaction" == actionId) {
                logger.info("Swapping in grammar for action: " + anAction.toString())
                //swap in a context dependent grammar
            }
        }

        override fun afterActionPerformed(anAction: AnAction?,
                                          dataContext: DataContext?,
                                          anActionEvent: AnActionEvent?) {

            //swap out a context dependent grammar
        }

        override fun beforeEditorTyping(c: Char, dataContext: DataContext?) {

        }
    }
}
