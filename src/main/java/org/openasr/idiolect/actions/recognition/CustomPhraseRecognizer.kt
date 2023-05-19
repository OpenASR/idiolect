package org.openasr.idiolect.actions.recognition

import com.intellij.ide.actions.OpenFileAction
import com.intellij.notification.*
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.project.Project
import org.openasr.idiolect.nlp.*
import org.openasr.idiolect.settings.IdiolectConfig
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import java.awt.Component
import java.io.File

class CustomPhraseRecognizer: IntentResolver("Custom Phrases", 500) {
    override val grammars: List<NlpGrammar> by lazy { buildGrammars() }

    companion object {
        private val defaultPropertiesFileContents = """
            # This file may be used to bind custom utterances to actions.
            # The first action with a matching utterance will be invoked.
            # Any changes saved to this file are applied immediately.
            # For a list of potential actions that could be rebound, see:
            # https://github.com/OpenASR/idiolect/blob/master/src/main/resources/phrases.example.properties

            FindInPath=find in project
            MethodDown=next method
            ActivateProjectToolWindow=open project
            ActivateTerminalToolWindow=open terminal
            CodeInspection.OnEditor=inspect code
            """.trimIndent()

        private val phrasesFile by lazy { File(IdiolectConfig.idiolectHomePath, "phrases.properties")
            .apply {
                if (exists()) readText()
                else {
                    defaultPropertiesFileContents.also { writeText(it) }

                    NotificationGroupManager.getInstance()
                        .getNotificationGroup("Idiolect")
                        .createNotification("Custom phrases",
                            """
                            Edit $absolutePath
                            or say "Edit custom phrases".
                            """,
                            NotificationType.INFORMATION)
                        .addAction(NotificationAction.create("Open properties file (~/${name})") { e ->
                            openCustomPhrasesFile(e.project!!)
                        })
                        .notify(null)
                }
            }
        }

        fun openCustomPhrasesFile(project: Project) {
            OpenFileAction.openFile(phrasesFile.absolutePath, project)
        }
    }

    data class PhraseToActionBinding(val name: String, val boundPhrases: List<String>)


    // Custom phrases may need to be enabled for all modes, but for now, only in ACTION mode
    override fun isSupported(context: NlpContext, component: Component?) = context.isActionMode()

    override fun tryResolveIntent(nlpRequest: NlpRequest, context: NlpContext) =
        properties.firstOrNull {
            it.boundPhrases.any { phrase ->
                phrase in nlpRequest.alternatives
            }
        }?.let { NlpResponse(it.name) }

    var lastModified = 0L

    private var cachedProperties = listOf<PhraseToActionBinding>()

    /**
     * Upon access, updates `cachedProperties` if the file has been modified since the last access.
     *
     * @return the cached properties.
     */
    private val properties: List<PhraseToActionBinding> get() =
        if (phrasesFile.lastModified() == lastModified) cachedProperties
        else readBindingsFromPropertiesFile().apply {
            cachedProperties = this
            lastModified = phrasesFile.lastModified()
        }

    private val actionManager by lazy { ActionManager.getInstance() }

    private fun readBindingsFromPropertiesFile(): List<PhraseToActionBinding> =
        phrasesFile.readText().lines()
            .filter { it.split("=").size == 2 } // Only take lines containing a single '='
            .map {
                it.split("=").let { (k, v) -> PhraseToActionBinding(k, v.split("|")) }
            }
            // Check if name is a valid actionId
            .filter {
                it.name.startsWith("Idiolect.")
                    || it.name.startsWith("Template.")
                    || it.name.endsWith("]")
                    || actionManager.getAction(it.name) != null
            }

    private fun buildGrammars(): List<NlpGrammar> =
        properties.map { (actionId, boundPhrases) ->
            NlpGrammar("Custom.$actionId").withExamples(boundPhrases)
        }
}
