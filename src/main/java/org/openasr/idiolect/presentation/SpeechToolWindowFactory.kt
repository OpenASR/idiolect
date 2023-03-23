package org.openasr.idiolect.presentation

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class SpeechToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.getInstance()

        // Add tabs (or "contents") to the Speech ToolWindow
        val speechLogTab = SpeechLogTab(toolWindow)
        val content = contentFactory.createContent(speechLogTab.getContent(), "Log", false)
        toolWindow.contentManager.addContent(content)

        toolWindow.contentManager.addContent(
            contentFactory.createContent(SpeechCommandsTab(toolWindow).getContent(), "Commands", false)
        )
    }
}
