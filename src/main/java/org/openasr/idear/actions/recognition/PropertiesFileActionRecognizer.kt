package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.*
import org.openasr.idear.nlp.*
import java.io.File

class PropertiesFileActionRecognizer: ActionRecognizer("Properties File Recognizer", 500) {
    override val grammars: List<NlpGrammar> by lazy { buildGrammars() }

    data class Binding(val name: String, val boundUtterances: List<String>)

    override fun tryResolveIntent(nlpRequest: NlpRequest, dataContext: DataContext) =
        properties.firstOrNull { it.boundUtterances.any { it in nlpRequest.alternatives } }
            ?.let { ActionCallInfo(it.name) }

    val lastModified = 0L
    val propertiesFile = File(System.getProperty("user.home"), ".idear.properties")

    private var cachedProperties = listOf<Binding>()
    val properties: List<Binding> get() =
        if (propertiesFile.lastModified() == lastModified) cachedProperties
        else readBindingsFromPropertiesFile().apply { cachedProperties = this }

    private fun getDefaultProperties() = """See example properties file.""".trimIndent()

    private val actionManager by lazy { ActionManager.getInstance() }

    private fun readBindingsFromPropertiesFile(): List<Binding> =
        propertiesFile
            // If exists, get contents, otherwise create file and write default contents
            .run { if (exists()) readText() else getDefaultProperties().also { writeText(it) } }
            .lines().filter { it.split("=").size == 2 } // Only take lines containing a single '='
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
