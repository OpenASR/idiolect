package org.openasr.idiolect.presentation.toolwindow.log

import com.intellij.ide.DataManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.application
import com.intellij.util.ui.UIUtil
import org.openasr.idiolect.actions.ExecuteActionFromTextField
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.actions.recognition.IdiolectCommandRecognizer
import org.openasr.idiolect.nlp.NlpRequest
import org.openasr.idiolect.nlp.NlpResultListener
import java.awt.Rectangle
import javax.swing.JComponent
import javax.swing.JEditorPane
import javax.swing.text.html.HTMLEditorKit


class SpeechLogTab(private val toolWindow: ToolWindow) :
    Disposable,
    DumbAware,
    NlpResultListener
{
    private val maxLength = 100
    private val log = mutableListOf<String>()
    private var logPane = JEditorPane(UIUtil.HTML_MIME, log.joinToString(""))

    init {
        application.messageBus.connect(this).let {
            it.subscribe(NlpResultListener.NLP_RESULT_TOPIC, this)
//            it.subscribe(ToolWindowManagerListener.TOPIC, this)
        }

        val kit = logPane.editorKit
        if (kit is HTMLEditorKit) {
            kit.styleSheet.addRule(
                ".recognition {margin: 10px 0 0 0;} " +
                ".alternatives {color: gray; margin: 0 0 0 20px;} " +
                ".error {color: red; margin: 0 0 0 20px;} " +
                ".fulfilled {color: green; margin: 0 0 0 20px;} " +
                ".message {color: yellow; margin: 0 0 0 20px;} ")
        }
    }

    override fun dispose() {
    }

    fun createComponent(): JComponent {
        val manualEntry = JBTextField(20)
        val textFieldAction = ExecuteActionFromTextField().apply { textField = manualEntry }
        manualEntry.addActionListener {
            val dataContext = DataManager.getInstance().getDataContext(manualEntry)
            val event = AnActionEvent.createFromDataContext(ActionPlaces.TOOLWINDOW_CONTENT, null, dataContext)
            textFieldAction.actionPerformed(event)
        }

        return panel {
            row {
                scrollCell(logPane).align(Align.FILL)
            }.resizableRow()
            row {
                cell(manualEntry)
                actionButton(textFieldAction, ActionPlaces.TOOLWINDOW_CONTENT)
            }.enabled(true)
        }
    }

    override fun onRecognition(nlpRequest: NlpRequest) {
        val utterance = nlpRequest.utterance
        updateLog("<div class=\"recognition\">\uD83C\uDFA4 $utterance</div>")
        if (nlpRequest.alternatives.size > 1) {
            updateLog("<div class=\"alternatives\">" +
                nlpRequest.alternatives.subList(1, nlpRequest.alternatives.size)
                    .joinToString("\" | \"", "\"", "\"")
                + "</div>")
        }
    }

    override fun onFulfilled(actionCallInfo: ActionCallInfo) {
        when (actionCallInfo.actionId) {
            IdiolectCommandRecognizer.INTENT_HI -> updateLog("\uD83D\uDC4B\uD83D\uDE00")
            else -> updateLog("<div class=\"fulfilled\">${actionCallInfo.actionId}</div>")
        }
    }

    override fun onFailure(message: String) {
        updateLog("<div class=\"error\">$message</div>")
    }

    override fun onMessage(message: String, verbosity: NlpResultListener.Companion.Verbosity) {
        val displayMessage = if (verbosity != NlpResultListener.Companion.Verbosity.INFO) {
            verbosity.name + ": " + message
        } else message

        updateLog("<div class=\"message ${verbosity.name}\">$displayMessage</div>")
        toolWindow.show()
    }

    private fun updateLog(line: String) {
        if (log.size > maxLength) {
            log.removeFirst()
        }
        log.add(line)

        logPane.text = log.joinToString("")

        invokeLater {
            logPane.scrollRectToVisible(Rectangle(0, logPane.height,1,1))
        }
    }
}