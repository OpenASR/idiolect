package org.openasr.idiolect.presentation.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.openasr.idiolect.presentation.toolwindow.audio.AudioTab
import org.openasr.idiolect.presentation.toolwindow.commands.SpeechCommandsTab
import org.openasr.idiolect.presentation.toolwindow.log.SpeechLogTab
import org.openasr.idiolect.settings.PrintlnLogger


class SpeechToolWindowFactory : ToolWindowFactory, DumbAware {
    init {
        PrintlnLogger.installForLocalDev()
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()

        // Add tabs (or "contents") to the Speech ToolWindow
        val speechLogTab = SpeechLogTab(toolWindow)
        val speechLogContent = contentFactory.createContent(speechLogTab.createComponent(), "Log", false)
        toolWindow.contentManager.addContent(speechLogContent)

        // Commands
        val speechCommandsTab = SpeechCommandsTab()
        val speechCommandsPanel = SimpleToolWindowPanel(true, false)
        speechCommandsPanel.toolbar = speechCommandsTab.createToolBar()
        speechCommandsPanel.setContent(speechCommandsTab.createComponent())

        val speechCommandsContent = contentFactory.createContent(speechCommandsPanel, "Commands", false)
        speechCommandsContent.searchComponent = speechCommandsTab.getSearchField()
        toolWindow.contentManager.addContent(speechCommandsContent)

        // Audio
        val audioTab = AudioTab()
        val audioContent = contentFactory.createContent(audioTab, "Audio", false)
        audioContent.setDisposer(audioTab)
        toolWindow.contentManager.addContent(audioContent)
    }
}
