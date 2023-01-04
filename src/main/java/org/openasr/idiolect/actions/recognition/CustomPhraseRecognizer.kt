package org.openasr.idiolect.actions.recognition

import com.intellij.ide.actions.OpenFileAction
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import org.openasr.idiolect.nlp.*
import org.openasr.idiolect.settings.IdiolectConfig
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.io.File

class CustomPhraseRecognizer: IntentResolver("Custom Phrases", 500) {
    override val grammars: List<NlpGrammar> by lazy { buildGrammars() }

    companion object {
        private val defaultPropertiesFileContents = """
            # This file may be used to bind custom utterances to actions.
            # The first action with a matching utterance will be invoked.
            # For a list of potential actions that could be rebound, see:
            # https://github.com/OpenASR/idiolect/blob/master/src/main/resources/phrases.example.properties

            FindInPath=find in project
            MethodDown=next method
            ActivateTerminalToolWindow=open terminal
            Diff.FocusOppositePane=other pane
            CodeInspection.OnEditor=inspect code
            """.trimIndent()

        private val propertiesFile by lazy { File(IdiolectConfig.idiolectHomePath, "phrases.properties")
            .apply {
                if (exists()) readText()
                else {
                    defaultPropertiesFileContents.also { writeText(it) }

                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Idiolect")
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

        private fun openCustomPhrasesFile(project: Project) {
            OpenFileAction.openFile(propertiesFile.absolutePath, project)
        }
    }

    data class Binding(val name: String, val boundPhrases: List<String>)

    override fun tryResolveIntent(nlpRequest: NlpRequest, dataContext: DataContext) =
        properties.firstOrNull {
            it.boundPhrases.any {
                phrase -> phrase in nlpRequest.alternatives
            }
        }?.let { NlpResponse(it.name) }

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
        propertiesFile.readText().lines()
            .filter { it.split("=").size == 2 } // Only take lines containing a single '='
            .map {
                it.split("=").let { (k, v) -> Binding(k, v.split("|")) }
            }
            // Check if name is a valid actionId
            .filter {
                it.name.startsWith("idiolect.")
                    || it.name.startsWith("Template.")
                    || it.name.endsWith("]")
                    || actionManager.getAction(it.name) != null
            }

    private fun buildGrammars(): List<NlpGrammar> =
        properties.map { (actionId, boundPhrases) ->
            NlpGrammar("Custom.$actionId").withExamples(boundPhrases)
        }
}
