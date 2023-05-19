package org.openasr.idiolect.presentation.toolwindow

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Computable
import com.intellij.openapi.wm.*
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import org.openasr.idiolect.presentation.toolwindow.chat.ChatTab
import org.openasr.idiolect.presentation.toolwindow.audio.AudioTab
import org.openasr.idiolect.presentation.toolwindow.commands.SpeechCommandsTab
import org.openasr.idiolect.presentation.toolwindow.log.SpeechLogTab
import org.openasr.idiolect.settings.PrintlnLogger
import java.util.ResourceBundle


class IdiolectToolWindowFactory : ToolWindowFactory, DumbAware {
    init {
        PrintlnLogger.installForLocalDev()
    }

    // Note: showTab(Tab) uses the ordinal value, so the order of these should match the display order
    enum class Tab {
        LOG,
        COMMANDS,
        AUDIO,
        CHAT
    }

    companion object {
        private lateinit var toolWindow: ToolWindow
        private val TAB_LOG: String
        private val TAB_COMMANDS: String
        private val TAB_AUDIO: String
        private val TAB_CHAT: String

        init {
            val bundle = ResourceBundle.getBundle("messages")
            TAB_LOG = bundle.getString("Log")
            TAB_COMMANDS = bundle.getString("Commands")
            TAB_AUDIO = bundle.getString("Audio")
            TAB_CHAT = bundle.getString("Chat")
        }

        fun showToolWindow() {
            if (!::toolWindow.isInitialized) {
              val project = ProjectManager.getInstance().openProjects[0]
              val toolWindowManager = ToolWindowManager.getInstance(project)
              toolWindow = toolWindowManager.getToolWindow("Idiolect")!!
            }

            toolWindow.show()
        }

        fun hideToolWindow() {
            toolWindow.hide()
        }

        fun showTab(tab: Tab/*, project: Project?*/): Content {
//            project?.let {
//                val toolWindow = ToolWindowManager.getInstance(it).getToolWindow("Idiolect")
            showToolWindow()

            val contentManager = toolWindow.contentManager
            val tabContent = contentManager.getContent(tab.ordinal)
            contentManager.setSelectedContent(tabContent!!)

            return tabContent
        }

//        fun showTab(tab: Tab, dataContext: DataContext) {
//            showTab(tab, dataContext.getData(PlatformDataKeys.PROJECT))
//        }
    }

    /**
     * Adds tabs (or "contents") to the Idiolect ToolWindow
     */
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
//        ApplicationManager.getApplication().invokeLater({
        val actionUpdateThread = ActionUpdateThread.BGT
        actionUpdateThread.run {
            IdiolectToolWindowFactory.toolWindow = toolWindow
            val contentFactory = ContentFactory.getInstance()

            // Log
            val speechLogTab = SpeechLogTab(toolWindow)
            val speechLogContent = contentFactory.createContent(speechLogTab.createComponent(), TAB_LOG, false)
            toolWindow.contentManager.addContent(speechLogContent)

            // Commands
            val speechCommandsTab = SpeechCommandsTab()
            val speechCommandsPanel = SimpleToolWindowPanel(true, false)
            speechCommandsPanel.toolbar = speechCommandsTab.createToolBar()
            speechCommandsPanel.setContent(speechCommandsTab.createComponent())

            val speechCommandsContent = contentFactory.createContent(speechCommandsPanel, TAB_COMMANDS, false)
            speechCommandsContent.searchComponent = speechCommandsTab.getSearchField()
            toolWindow.contentManager.addContent(speechCommandsContent)

            // Audio
            val audioTab = AudioTab()
            val audioContent = contentFactory.createContent(audioTab, TAB_AUDIO, false)
            audioContent.setDisposer(audioTab)
            toolWindow.contentManager.addContent(audioContent)

            // Chat
            val chatTab = ChatTab(toolWindow)
            val chatPanel = SimpleToolWindowPanel(true, true)
            chatPanel.toolbar = chatTab.createToolBar()
            chatPanel.setContent(chatTab.createComponent())
            val chatContent = contentFactory.createContent(chatPanel, TAB_CHAT, false)
            toolWindow.contentManager.addContent(chatContent)
        }
    }
}
