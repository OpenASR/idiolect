package org.openasr.idiolect.presentation.toolwindow.ai

import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.util.minimumWidth
import com.intellij.ui.util.preferredWidth
import com.intellij.util.application
import com.intellij.util.ui.UIUtil
import org.openasr.idiolect.actions.AiAction
import org.openasr.idiolect.nlp.ai.AiResponseListener
import org.openasr.idiolect.nlp.ai.OpenAiClient
import org.openasr.idiolect.presentation.IdiolectHtmlEditorKit
import org.openasr.idiolect.settings.openai.OpenAiConfig
import java.awt.Rectangle
import java.util.Hashtable
import javax.swing.*
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener
import kotlin.reflect.KMutableProperty0


class AiTab(private val toolWindow: ToolWindow) : Disposable, AncestorListener, AiResponseListener, DumbAware {
    private val log = logger<AiTab>()
    private val maxLength = 100
    private val aiLog = mutableListOf<String>()
    private val logPane = JEditorPane(UIUtil.HTML_MIME, aiLog.joinToString(""))
//    private val logPane = JTextPane()//.apply { contentType = UIUtil.HTML_MIME }
//    private val userInput = JEditorPane("text", "")
//    private val userInput = JBTextArea(null, "Hello", 1, 20)
    private val userInput = JBTextField(20)
    private val chatModelSelector = LlmModelSelector(OpenAiClient.ModelType.chat, OpenAiConfig.settings::chatModel)
    private val completionModelSelector = LlmModelSelector(OpenAiClient.ModelType.completions, OpenAiConfig.settings::completionModel)


    init {
        application.messageBus.connect(this).subscribe(AiResponseListener.AI_RESPONSE_TOPIC, this)

        initialiseLogPane()

        userInput.minimumWidth = 100
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

                intTextField(1..4096, 16).columns(COLUMNS_TINY).label("Max tokens")
                    .onChanged {
                        if (it.text.isNotEmpty()) {
                            try {
                                OpenAiConfig.settings.maxTokens = it.text.toInt()
                            } catch (e: Exception) {
                                log.info("Invalid max token. Must be an integer value <= 4096")
                            }
                        }
                    }
                    .bindIntText(OpenAiConfig.settings::maxTokens)

                doubleSlider(OpenAiConfig.settings::temperature).label("Temperature")
                doubleSlider(OpenAiConfig.settings::topP).label("Top P")
            }
        }

        toolbar.addAncestorListener(this)

        return toolbar
    }

    override fun ancestorAdded(event: AncestorEvent?) {
        updateModels()
    }
    override fun ancestorRemoved(event: AncestorEvent?) {}
    override fun ancestorMoved(event: AncestorEvent?) {}

    private fun updateModels() {
        chatModelSelector.update()
        completionModelSelector.update()
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

    private fun Row.doubleSlider(prop: KMutableProperty0<Double?>, default: Double = 1.0, scale: Int = 10): Cell<JSlider> {
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
                }
            }
            .applyIfEnabled()
            .bindValue({
                prop.get()?.let {
                    (it * scale).toInt()
                } ?: (default * scale).toInt()
            }, {
                prop.set(it.toDouble() / scale)
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
