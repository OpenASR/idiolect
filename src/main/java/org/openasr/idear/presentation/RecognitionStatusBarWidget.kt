package org.openasr.idear.presentation

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import org.openasr.idear.nlp.NlpResultListener

class RecognitionStatusBarWidget(private val project: Project) : NlpResultListener, StatusBarWidget, StatusBarWidget.MultipleTextValuesPresentation {
    companion object {
        val RECOGNITION_STATUS = "org.openasr.idear.Status"
    }
    private var statusBar: StatusBar? = null
    var tooltip = "Idear"
    var text: String? = null

    override fun ID() = RECOGNITION_STATUS

    override fun install(statusBar: StatusBar) {
        project.messageBus.connect(this).subscribe(NlpResultListener.NLP_RESULT_TOPIC, this)

        this.statusBar = statusBar
        Disposer.register(statusBar, this)
    }

    override fun getPresentation() = this

    override fun getTooltipText(): String? {
        return tooltip
    }

    override fun getSelectedValue(): String? {
        return text
    }

    override fun onListening(listening: Boolean) {
        updateStatus(if (listening) "Listening..." else null)
    }

    override fun onRecognition(utterance: String) {
        tooltip = "Last heard: '$utterance'"
        updateStatus("\uD83C\uDFA4 $utterance")
    }

    override fun onFulfilled(intentName: String, slots: MutableMap<String, out String>?, sessionAttributes: MutableMap<String, out String>?) {
        updateStatus("Action: $intentName")
    }

    override fun onFailure(message: String) {
        updateStatus("Error: $message")
    }

    override fun onMessage(message: String, verbosity: NlpResultListener.Companion.Verbosity) {
        updateStatus("Idear: $message")
    }

    override fun dispose() {
        statusBar = null
    }

    private fun updateStatus(text: String?) {
//        UIUtil.invokeLaterIfNeeded {
            this.text = text
            this.statusBar?.updateWidget(RECOGNITION_STATUS)
//        }
    }
}
