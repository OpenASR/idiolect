package org.openasr.idear.asr

import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.ex.AnActionListener
import com.intellij.openapi.diagnostic.Logger

object GrammarService : AnActionListener {
    private val logger = Logger.getInstance(javaClass)

    // TODO: ActionManager.addAnActionListener is deprecated, but this alternative is only supported in #183 (2018.3)
    //       Currently we're supporting <idea-version since-build="131"/> which is pre-2016
//    import com.intellij.openapi.application.ApplicationManager
//    fun init() = ApplicationManager.getApplication().getMessageBus().connect().subscribe(AnActionListener.TOPIC, this)
    @Suppress("DEPRECATION")
    fun init() = ActionManager.getInstance().addAnActionListener(this)

    override fun beforeActionPerformed(anAction: AnAction,
                                       dataContext: DataContext,
                                       anActionEvent: AnActionEvent) {
        val actionId = ActionManager.getInstance().getId(anAction)

        if ("someaction" == actionId) {
            logger.info("Swapping in grammar for action: $anAction")
            //swap in a context aware grammar
        }
    }

    override fun beforeEditorTyping(c: Char, dataContext: DataContext) = Unit
}
