package org.openasr.idear.actions.recognition

import com.intellij.ide.actions.OpenFileAction
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import org.openasr.idear.nlp.*
import org.openasr.idear.settings.IdearConfig
import org.openasr.idear.settings.IdearConfigurable
import java.io.File

class CustomUtteranceActionRecognizer: ActionRecognizer("Properties File Recognizer", 500) {
    override val grammars: List<NlpGrammar> by lazy { buildGrammars() }

    companion object {
        private val defaultPropertiesFileContents = """
            # This file may be used to bind custom utterances to actions.
            # The first action with a matching utterance will be invoked.
            # For a list of potential actions that could be rebound, see:
            # https://github.com/OpenASR/idear/blob/master/src/main/resources/phrases.example.properties
        """.trimIndent()

        private val propertiesFile by lazy { File(IdearConfig.idearHomePath, "phrases.properties")
            .apply {
                if (exists()) readText()
                else {
                    defaultPropertiesFileContents.also { writeText(it) }

                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Idear")
                        .createNotification("Custom Phrases",
                            "You can configure phrases to be recognised in $absolutePath",
                            NotificationType.INFORMATION)
                        .addAction(NotificationAction.create("Open properties file (~/${name})") { e ->
                            openCustomPhrasesFile(e.project!!)
                        })
                        .notify(null)
                }
            }
        }

        fun openCustomPhrasesFile(project: Project) {
            OpenFileAction.openFile(propertiesFile.absolutePath, project)
        }
    }

    data class Binding(val name: String, val boundUtterances: List<String>)


    override fun tryResolveIntent(nlpRequest: NlpRequest, dataContext: DataContext) =
        properties.firstOrNull { it.boundUtterances.any { it in nlpRequest.alternatives } }
            ?.let { ActionCallInfo(it.name) }

    var lastModified = 0L

    private var cachedProperties = listOf<Binding>()
    private val properties: List<Binding> get() =
        if (propertiesFile.lastModified() == lastModified) cachedProperties
        else readBindingsFromPropertiesFile().apply {
            cachedProperties = this
            lastModified = propertiesFile.lastModified()
        }

    private val actionManager by lazy { ActionManager.getInstance() }

    private fun readBindingsFromPropertiesFile(): List<Binding> =
        propertiesFile.readText().lines().filter { it.split("=").size == 2 } // Only take lines containing a single '='
            .map { it.split("=").let { (k, v) -> Binding(k, v.split("|")) } }
            // Check if name is a valid actionId
            .filter { actionManager.getAction(it.name) != null }

    private fun buildGrammars(): List<NlpGrammar> =
        properties.flatMap { (actionId, boundUtterances) ->
            boundUtterances.map { boundUtterance ->
                object : NlpGrammar(boundUtterance) {
                    override fun tryMatchRequest(utterance: String, dataContext: DataContext): ActionCallInfo? =
                        if (utterance == boundUtterance) ActionCallInfo(actionId) else null
                }
            }
        }
}
