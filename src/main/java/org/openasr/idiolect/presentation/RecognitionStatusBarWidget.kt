package org.openasr.idiolect.presentation

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.*
import com.intellij.openapi.wm.StatusBarWidget.WidgetPresentation
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetWrapper.StatusBarWidgetClickListener
import com.intellij.ui.GotItTooltip
import com.intellij.util.*
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.asr.*
import org.openasr.idiolect.nlp.*
import java.awt.event.MouseEvent
import javax.swing.JComponent

class RecognitionStatusBarWidget() :
    TextPanel.WithIconAndArrows(),
    CustomStatusBarWidget,
    WidgetPresentation,
    NlpResultListener,
    AsrSystemStateListener
{
    companion object {
        const val RECOGNITION_STATUS = "org.openasr.idiolect.Status"
    }

    private val log = logger<RecognitionStatusBarWidget>()
    private var statusBar: StatusBar? = null
    private var isListening: Boolean = false

    override fun ID() = RECOGNITION_STATUS

    init {
        icon = Icons.RECORD_START
        text = ""
    }

    override fun install(statusBar: StatusBar) {
        application.messageBus.connect(this).let {
            it.subscribe(NlpResultListener.NLP_RESULT_TOPIC, this)
            it.subscribe(AsrSystemStateListener.ASR_STATE_TOPIC, this)
        }

        this.statusBar = statusBar
        Disposer.register(statusBar, this)

        GotItTooltip("org.openasr.idiolect.intro", "Click <b><a href=\"\">here</a></b> to get started with voice control", this)
            .show(this, GotItTooltip.TOP_MIDDLE)

        StatusBarWidgetClickListener(clickConsumer).installOn(this, true)
    }

    override fun onAsrStatus(message: String) = updateStatus(message)

    override fun onAsrReady(message: String) =
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Idiolect")
            .createNotification("ASR is Ready",
                message,
                NotificationType.INFORMATION)
            .notify(null)
            .also { updateStatus("") }

    override fun getTooltipText() = toolTipText

    override fun getPresentation() = this

    override fun getComponent(): JComponent = this

    override fun getClickConsumer() = Consumer<MouseEvent> {
        try {
            AsrService.toggleListening()
        } catch (e: Exception) {
            log.info("Failed to toggle listening: ${e.message}")
        }
    }

    override fun onListening(listening: Boolean) {
        isListening = listening
        icon = if (listening) Icons.RECORD_END else Icons.RECORD_START

        toolTipText = if (isListening) "Listening..." else "Click to activate voice control"
        updateStatus(if (listening) "Listening..." else null)
    }

    override fun onRecognition(nlpRequest: NlpRequest) {
        val utterance = nlpRequest.utterance
        toolTipText = "Last heard: '$utterance'"
        updateStatus("\uD83C\uDFA4 $utterance")
    }

    override fun onFulfilled(actionCallInfo: ActionCallInfo) =
        updateStatus("Action: ${actionCallInfo.actionId}")

    override fun onFailure(message: String) = updateStatus("Error: $message")

    override fun onMessage(message: String, verbosity: NlpResultListener.Companion.Verbosity) =
        updateStatus("Idiolect: $message")

    override fun dispose() { statusBar = null }

    private fun updateStatus(text: String?) {
//        UIUtil.invokeLaterIfNeeded {
            this.text = text
            this.statusBar?.updateWidget(RECOGNITION_STATUS)
//        }
    }
}
