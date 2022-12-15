package org.openasr.idear.asr

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.diagnostic.Logger
import com.intellij.util.messages.MessageBus

object GrammarService : AnActionListener {
    private val logger = Logger.getInstance(javaClass)

    fun init(bus: MessageBus) = bus.connect().subscribe(AnActionListener.TOPIC, this)

    override fun beforeActionPerformed(anAction: AnAction,
                                       anActionEvent: AnActionEvent) {
        val actionId = ActionManager.getInstance().getId(anAction)

        if ("someaction" == actionId) {
            logger.info("Swapping in grammar for action: $anAction")
            //swap in a context aware grammar
        }
    }

    override fun beforeEditorTyping(c: Char, dataContext: DataContext) = Unit
}
