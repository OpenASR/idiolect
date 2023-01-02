package org.openasr.idiolect.asr

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.messages.MessageBus
import org.openasr.idiolect.utils.ActionUtils

object GrammarService : AnActionListener {
    private val log = logger<GrammarService>()

    fun init(bus: MessageBus) = bus.connect().subscribe(AnActionListener.TOPIC, this)

    fun useCommandGrammar() {
        val grammar = HashSet<String>()

        addIdiolectGrammar(grammar)
        ActionUtils.addActionWords(grammar)

        AsrService.setGrammar(grammar.toTypedArray())
    }

    fun useDictationGrammar() = AsrService.setGrammar(emptyArray())

    private fun addIdiolectGrammar(grammar: HashSet<String>) =
        grammar.addAll(arrayOf("command", "dictation", "mode"))

    override fun beforeActionPerformed(anAction: AnAction, anActionEvent: AnActionEvent) {
        val actionId = ActionManager.getInstance().getId(anAction)

        if ("someaction" == actionId) {
            log.info("Swapping in grammar for action: $anAction")
            //swap in a context aware grammar
        }
    }

    override fun beforeEditorTyping(c: Char, dataContext: DataContext) = Unit
}