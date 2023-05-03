package org.openasr.idiolect.presentation.toolwindow.ai

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.util.minimumWidth
import com.intellij.util.application
import com.intellij.util.ui.UIUtil
import org.openasr.idiolect.actions.AiAction
import org.openasr.idiolect.nlp.ai.AiResponseListener
import org.openasr.idiolect.presentation.IdiolectHtmlEditorKit
import java.awt.Rectangle
import javax.swing.*


class AiTab(private val toolWindow: ToolWindow) : Disposable, AiResponseListener, DumbAware {
    private val log = logger<AiTab>()
    private val maxLength = 100
    private val aiLog = mutableListOf<String>()
    private val logPane = JEditorPane(UIUtil.HTML_MIME, aiLog.joinToString(""))
//    private val logPane = JTextPane()//.apply { contentType = UIUtil.HTML_MIME }
//    private val userInput = JEditorPane("text", "")
//    private val userInput = JBTextArea(null, "Hello", 1, 20)
    private val userInput = JBTextField(20)

    init {
        application.messageBus.connect(this).subscribe(AiResponseListener.AI_RESPONSE_TOPIC, this)

//        logPane.addHyperlinkListener {  }
        logPane.isEditable = false
        // HtmlEditorKit doesn't seem to support multiple class names
        val commonStyles = "margin-top: 10px; padding: 6px; color: black;"
        logPane.editorKit = IdiolectHtmlEditorKit().withStyle("""
            .prompt {
                $commonStyles
                background-color: silver;
            }
            .response {
                $commonStyles                
                background-color: white;
            }
            """)

        userInput.minimumWidth = 100
    }

    override fun dispose() {
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

    fun createComponent(): JComponent {
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

    override fun onUserPrompt(prompt: String) {
        updateLog(prompt, "prompt")
    }

    override fun onAiResponse(choices: List<String>) {
        choices.forEach { choice ->
            updateLog(choice, "response")
        }

        toolWindow.show()
    }

    private fun formatMessage(text: String, messageType: String): String {
        var tripleTicks = 0
        return "<div class=\"$messageType\">${
            text.replace("\\n", "<br/>")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace(Regex("([^`])`([^`]+)`([^`])"), "$1`<code>$2</code>`$3")
                .replace(Regex("```")) { if (tripleTicks++ % 2 == 0) "<pre><code>" else "</code></pre>" }
        }</div>"
    }

    private fun updateLog(text: String, role: String) = updateLog(formatMessage(text, role))

    private fun updateLog(html: String) {
        if (aiLog.size > maxLength) {
            aiLog.removeFirst()
        }

        aiLog.add(html)

        logPane.text = aiLog.joinToString("\n")

        invokeLater {
            logPane.scrollRectToVisible(Rectangle(0, logPane.height,1,1))
        }
    }
}
