package org.openasr.idiolect.presentation.toolwindow.ai

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.util.minimumWidth
import com.intellij.util.application
import com.intellij.util.ui.UIUtil
import org.openasr.idiolect.actions.AiAction
import org.openasr.idiolect.nlp.ai.AiResponseListener
import java.awt.BorderLayout
import java.awt.Rectangle
import javax.swing.*
import javax.swing.text.html.HTMLEditorKit


class AiTab(private val toolWindow: ToolWindow) : JComponent(), Disposable, AiResponseListener {
    private val log = logger<AiTab>()
    private val maxLength = 100
    private val aiLog = mutableListOf<String>()
    private val logPane = JEditorPane(UIUtil.HTML_MIME, aiLog.joinToString(""))
//    private val userInput = JEditorPane("text", "")
    private val userInput = JBTextArea(null, "Hello", 1, 20)

    init {
        layout = BorderLayout()
        add(createComponent(), BorderLayout.CENTER)

        application.messageBus.connect(this).let {
            it.subscribe(AiResponseListener.AI_RESPONSE_TOPIC, this)
//            it.subscribe(ToolWindowManagerListener.TOPIC, this)
        }

        logPane.isEditable = false
        userInput.minimumWidth = 100
//        logPane.addHyperlinkListener {  }

        val kit = logPane.editorKit
        if (kit is HTMLEditorKit) {
            kit.styleSheet.addRule(
                ".message {margin: 10px; color: black; border-radius: 10px;} " +
                    ".ai {background-color: gray;} " +
                    ".user {background: white;} ")
        }
    }

    fun createToolBar(): JComponent {
//        val group = DefaultActionGroup()
//        group.add(searchField)
//        val toolbar: ActionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, false)
        return panel {
            row {
                comboBox(listOf("gpt-4", "gpt-4-0314", "gpt-4-32k", "gpt-4-32k-0314", "gpt-3.5-turbo", "gpt-3.5-turbo-0301"))
                    .label("Chat model")

                comboBox(listOf("text-davinci-003", "text-davinci-002", "text-curie-001", "text-babbage-001", "text-ada-001"))
                    .label("Completion model")
            }
        }
    }

    private fun createComponent(): JComponent {
//        val userInput = JBTextField(20).apply {
//            addActionListener {
//
//            }
//        }
        val aiAction = AiAction().apply {
            setTextField(userInput)
        }
//        userInput.addKeyListener { event ->
//            val dataContext = DataManager.getInstance().getDataContext(userInput)
////            val event = AnActionEvent.createFromDataContext(ActionPlaces.TOOLWINDOW_CONTENT, null, dataContext)
////            val event = AnActionEvent.createFromInputEvent()
//            aiAction.actionPerformed(event)
//        }

        return panel {
            row {
                scrollCell(logPane).align(Align.FILL)
            }.resizableRow()
            row {
                cell(userInput)
                actionButton(aiAction, ActionPlaces.TOOLWINDOW_CONTENT)
            }.enabled(true)
        }
    }

    override fun dispose() {
    }

    override fun onUserPrompt(prompt: String) {
        updateLog("<div class=\"user message\">${prompt}</div>")
    }

    override fun onAiResponse(choices: List<String>) {
        updateLog("<div class=\"ai message\">${choices[0]}</div>")
        toolWindow.show()
    }

    private fun updateLog(line: String) {
        if (aiLog.size > maxLength) {
            aiLog.removeFirst()
        }
        aiLog.add(line)

        logPane.text = aiLog.joinToString("")

        invokeLater {
            logPane.scrollRectToVisible(Rectangle(0, logPane.height,1,1))
        }
    }
}
