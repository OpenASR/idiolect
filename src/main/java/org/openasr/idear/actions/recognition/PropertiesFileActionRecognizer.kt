package org.openasr.idear.actions.recognition

import com.intellij.openapi.actionSystem.*
import org.openasr.idear.asr.vosk.VoskAsr.Companion.propertiesFile
import org.openasr.idear.nlp.*
import java.io.File

class PropertiesFileActionRecognizer: ActionRecognizer("Properties File Recognizer", 500) {
    override val grammars: List<NlpGrammar> by lazy { buildGrammars() }

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
