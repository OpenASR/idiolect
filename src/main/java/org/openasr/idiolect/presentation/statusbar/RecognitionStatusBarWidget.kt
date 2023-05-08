package org.openasr.idiolect.presentation.statusbar

import com.intellij.notification.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.wm.*
import com.intellij.openapi.wm.StatusBarWidget.WidgetPresentation
import com.intellij.openapi.wm.impl.status.TextPanel
import com.intellij.ui.*
import com.intellij.util.Consumer
import com.intellij.util.application
import org.openasr.idiolect.actions.recognition.ActionCallInfo
import org.openasr.idiolect.asr.*
import org.openasr.idiolect.nlp.*
import org.openasr.idiolect.presentation.Icons
import org.openasr.idiolect.settings.PrintlnLogger
import java.awt.event.MouseEvent
import javax.swing.JComponent

class RecognitionStatusBarWidget :
    TextPanel.WithIconAndArrows(),
    CustomStatusBarWidget,
    WidgetPresentation,
    NlpResultListener,
    AsrSystemStateListener,
    Disposable
{
    companion object {
        const val RECOGNITION_STATUS = "org.openasr.idiolect.Status"

        init {
            PrintlnLogger.installForLocalDev()
        }
    }

    private val log = logger<RecognitionStatusBarWidget>()
    private val asrService = service<AsrService>()
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

        GotItTooltip("org.openasr.idiolect.intro", "Click <b><a href=\"\">here</a></b> to get started with voice control", this)
            .show(this, GotItTooltip.TOP_MIDDLE)

        object : ClickListener() {
            override fun onClick(e: MouseEvent, clickCount: Int): Boolean =
                onClick(e)
        }.installOn(this, true)
    }

    override fun getClickConsumer() = Consumer<MouseEvent> {
        fun consume(e: MouseEvent) = this.onClick(e)
    }

    private fun onClick(event: MouseEvent): Boolean {
        try {
//            log.info("Clicked the status bar widget")
            asrService.toggleListening()
            return true
        } catch (e: Exception) {
            log.info("Failed to toggle listening: ${e.message}")
        }
        return false
    }

    override fun onAsrStatus(message: String) = updateStatus(message)

    override fun onAsrReady(message: String) =
        NotificationGroupManager.getInstance()
            .getNotificationGroup("Idiolect")
            .createNotification("Idiolect is Ready",
                message,
                NotificationType.INFORMATION)
            .notify(null)
            .also { updateStatus("") }

    override fun getTooltipText() = toolTipText

    override fun getPresentation() = this

    override fun getComponent(): JComponent = this

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
