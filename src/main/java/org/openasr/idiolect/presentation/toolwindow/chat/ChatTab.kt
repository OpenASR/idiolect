package org.openasr.idiolect.presentation.toolwindow.chat

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.util.minimumWidth
import com.intellij.ui.util.preferredWidth
import com.intellij.util.application
import com.intellij.util.ui.UIUtil
import org.openasr.idiolect.actions.LlmCompletionAction
import org.openasr.idiolect.nlp.ai.AiResponseListener
import org.openasr.idiolect.nlp.ai.AiService
import org.openasr.idiolect.nlp.ai.OpenAiClient
import org.openasr.idiolect.presentation.components.FocusableTextArea
import org.openasr.idiolect.presentation.components.IdiolectHtmlEditorKit
import org.openasr.idiolect.settings.openai.OpenAiConfig
import java.awt.Rectangle
import java.util.*
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import kotlin.reflect.KFunction1
import kotlin.reflect.KMutableProperty0


class ChatTab(private val toolWindow: ToolWindow) : Disposable, AncestorListener, AiResponseListener {
    private val log = logger<ChatTab>()
    private val maxLength = 100
    private val conversationLog = mutableListOf<String>()
    private val logPane = JEditorPane(UIUtil.HTML_MIME, conversationLog.joinToString(""))
//    private val logPane = JTextPane()//.apply { contentType = UIUtil.HTML_MIME }
//    private val userInput = JEditorPane("text", "")
//    private val userInput = JBTextArea(1, 20)
    private val userInput = FocusableTextArea(1, 20)
    private val chatModelSelector = LlmModelSelector(OpenAiClient.ModelType.chat, OpenAiConfig.settings::chatModel)
    private val completionModelSelector = LlmModelSelector(OpenAiClient.ModelType.completions, OpenAiConfig.settings::completionModel)
    private val aiService = service<AiService>()

    init {
        application.messageBus.connect(this).subscribe(AiResponseListener.AI_RESPONSE_TOPIC, this)

        initialiseLogPane()

        userInput.minimumWidth = 500
        userInput.emptyText.text = "Enter prompt here..."

        updateModels()
    }

    override fun dispose() {
    }


    fun createToolBar(): JComponent {
//        val group = DefaultActionGroup()
//        group.add(searchField)
//        val toolbar: ActionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, group, false)

        val toolbar = panel {
            row {
                cell(chatModelSelector).label("Chat model")
                cell(completionModelSelector).label("Completion model")

                intTextField(IntRange(1,4096), 16).columns(COLUMNS_TINY).label("Max tokens")
                    .applyToComponent {
                        toolTipText = "The maximum number of tokens the LLM will generate"
                    }
                    .onChanged {
                        if (it.text.isNotEmpty()) {
                            try {
                                val value = it.text.toInt()
                                OpenAiConfig.settings.maxTokens = value
//                                aiService.setMaxTokens(value)
                            } catch (e: Exception) {
                                log.info("Invalid max token. Must be an integer value <= 4096")
                            }
                        }
                    }
                    .bindIntText(OpenAiConfig.settings::maxTokens)

                doubleSlider(OpenAiConfig.settings::temperature, aiService::setTemperature).label("Temperature")
                    .applyToComponent {
                        toolTipText = "Randomness"
                    }
                doubleSlider(OpenAiConfig.settings::topP, aiService::setTopP).label("Top P")
                    .applyToComponent {
                        toolTipText = "Probability"
                    }
            }
        }

        toolbar.addAncestorListener(this)

        return toolbar
    }

    override fun ancestorAdded(event: AncestorEvent?) {
        updateModels()
        invokeLater {
            userInput.requestFocusInWindow()
        }
    }
    override fun ancestorRemoved(event: AncestorEvent?) {}
    override fun ancestorMoved(event: AncestorEvent?) {}

    private fun updateModels() {
        Thread({
            chatModelSelector.update()
            completionModelSelector.update()
        }, "Idiolect chat model list update").start()
    }

    fun createComponent(): JComponent {
//        val userInput = JBTextField(20).apply {
//            addActionListener {
//
//            }
//        }
        val llmCompletionAction = LlmCompletionAction().apply {
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
                cell(userInput).focused().accessibleName("prompt input")
                actionButton(llmCompletionAction, ActionPlaces.TOOLWINDOW_CONTENT).accessibleName("send")
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

    private fun Row.doubleSlider(
        prop: KMutableProperty0<Double?>,
        updateService: KFunction1<Double, Unit>,
        default: Double = 1.0,
        scale: Int = 10): Cell<JSlider>
    {
        val sliderLabels = Hashtable<Int, JComponent>()
        sliderLabels[0] = JLabel("0")
        sliderLabels[scale / 2] = JLabel("0.5")
        sliderLabels[scale] = JLabel("1")

        return slider(0, scale, 1, scale / 2)
            .applyToComponent {
                preferredWidth = 100
//                labelTable = sliderLabels
//                paintTicks = false
                paintLabels = false
                toolTipText = "${value.toDouble() / scale}"
                addChangeListener {
                    val doubleVal = value.toDouble() / scale
                    toolTipText = "$doubleVal"
                    prop.set(if (doubleVal == default) null else doubleVal)
                    updateService(doubleVal)
                }
            }
            .applyIfEnabled()
            .bindValue({
                prop.get()?.let {
                    (it * scale).toInt()
                } ?: (default * scale).toInt()
            }, {
                // this doesn't actually get called, not sure why - see addChangeListener above
                val value = it.toDouble() / scale
                prop.set(value)
                updateService(value)
            })
    }

    private fun initialiseLogPane() {
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
    }

    private fun formatMessage(text: String, messageType: String): String {
        var tripleTicks = 0
        return "<div class=\"$messageType\">${
            text //.replace("\n", "<br/>")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace(Regex("([^`])`([^`]+)`([^`])"), "$1`<code>$2</code>`$3")
                .replace(Regex("```")) { if (tripleTicks++ % 2 == 0) "<pre><code>" else "</code></pre>" }
        }</div>"
    }

    private fun updateLog(text: String, role: String) = updateLog(formatMessage(text, role))

    private fun updateLog(html: String) {
        if (conversationLog.size > maxLength) {
            conversationLog.removeFirst()
        }

        conversationLog.add(html)

        logPane.text = conversationLog.joinToString("\n")

        invokeLater {
            logPane.scrollRectToVisible(Rectangle(0, logPane.height,1,1))
        }
    }
}
