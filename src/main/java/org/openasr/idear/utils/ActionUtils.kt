package org.openasr.idear.utils

import com.intellij.openapi.actionSystem.ActionManager
import org.openasr.idear.nlp.NlpGrammar

object ActionUtils {
    fun addActionWords(grammar: HashSet<String>) {
        val actionManager = ActionManager.getInstance()

        for (actionId in actionManager.getActionIdList("")) {
            if (!actionManager.isGroup(actionId)) {
                grammar.addAll(actionId
                        .replace("$", "")
                        .replace(".", "")
                        .splitCamelCase())
            }
        }
    }

    fun buildGrammar(): List<NlpGrammar> {
        val actionManager = ActionManager.getInstance()

        println("---- ${actionManager.getActionIdList("").size} total actions")

        return actionManager.getActionIdList("")
                .filter { !actionManager.isGroup(it) }
                .filter { filterSpeakableActionId(it) }
                .sorted()
                .map { NlpGrammar(it, scoreActionId(it)).withExample(formatSpeakableActionId(it)) }
    }

    fun filterSpeakableActionId(actionId: String): Boolean {
        return !actionId.contains("Uast")
                && !actionId.startsWith("Ace")
                && !actionId.contains(Regex("[$.-]"))   // TODO - implement these elsewhere
                && !arrayOf(
                        // Remove some silly examples
                        "UsageGrouping.FlattenModules",
                        "SegmentedVcsControlAction",
                        "ExpandCollapseToggleAction",
                        "RefreshAllProjects",
                        "RoundedIconTestAction",
                        "UiDslTestAction",
                        "RestoreFontPreviewTextAction",
                        "WrapLayoutTestAction",
                        "MarkVfsCorrupted",
                        "CollectFUStatisticsAction",
                        "Document2XSD"
                ).contains(actionId)
    }

    fun formatSpeakableActionId(actionId: String): String {
        return actionId
                // Only make changes here that we can un-do when converting utterance to actionId
                .replace("Goto", "GoTo")
                .replace("Cvs", "Git")
                .replace(Regex("^Editor"), "")
//                                    .replace("$", "")
//                                    .replace(".", "")
//                                    .replace("-", " ")
                .replace("1", " one")
                .replace("2", " two")
                .replace("3", " three")
                .replace("4", " four")
                .replace("5", " five")
                .replace("6", " six")
                .replace("7", " seven")
                .replace("8", " eight")
                .replace("9", " nine")
                .expandCamelCase()
    }

    fun scoreActionId(actionId: String): Int {
        return when {
            actionId.startsWith("Editor") -> 5
            arrayOf("Back").contains(actionId) -> 10
            arrayOf("").contains(actionId) -> 0
            arrayOf("").contains(actionId) -> 0
            arrayOf("").contains(actionId) -> 0
            arrayOf("").contains(actionId) -> 0
            arrayOf("").contains(actionId) -> 0
            arrayOf("CallHierarchy").contains(actionId) -> 20
            arrayOf("AutoIndentLines").contains(actionId) -> 25
            arrayOf("AnonymousToInner").contains(actionId) -> 30
            arrayOf("BuildArtifact").contains(actionId) -> 35
            actionId.contains("Caret") -> 40
            actionId.startsWith("Change") -> 100
            actionId.startsWith("Breakpoint") -> 50
            actionId.startsWith("Activate") -> 200
            actionId.startsWith("BreadCrumbs") -> 400
            actionId.contains("BookMark") -> 401
            actionId.contains("Bom") -> 800
            actionId.endsWith("Action") -> 250
            else -> Int.MAX_VALUE
        }
    }
}
