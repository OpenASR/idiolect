package org.openasr.idiolect.presentation.toolwindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import org.openasr.idiolect.presentation.toolwindow.audio.AudioTab
import org.openasr.idiolect.presentation.toolwindow.commands.SpeechCommandsTab
import org.openasr.idiolect.presentation.toolwindow.log.SpeechLogTab


class SpeechToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()

        // Add tabs (or "contents") to the Speech ToolWindow
        val speechLogTab = SpeechLogTab(toolWindow)
        val speechLogContent = contentFactory.createContent(speechLogTab.createComponent(), "Log", false)
        toolWindow.contentManager.addContent(speechLogContent)

        val speechCommandsTab = SpeechCommandsTab()
        val speechCommandsContent = contentFactory.createContent(speechCommandsTab.createComponent(), "Commands", false)
        speechCommandsContent.searchComponent = speechCommandsTab.getSearchField()
        toolWindow.contentManager.addContent(speechCommandsContent)

        val audioTab = AudioTab()
        val audioContent = contentFactory.createContent(audioTab, "Audio", false)
        audioContent.setDisposer(audioTab)
        toolWindow.contentManager.addContent(audioContent)
    }
}