package org.openasr.idiolect.utils

import com.intellij.openapi.actionSystem.ActionManager
import org.openasr.idiolect.nlp.NlpGrammar

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

        return actionManager.getActionIdList("")
                .filter { !actionManager.isGroup(it) }
                .filter { filterPronouncableActionId(it) }
                .sorted()
                .map { NlpGrammar(it, scoreActionId(it)).withExample(formatSpeakableActionId(it)) }
    }

    fun filterPronouncableActionId(actionId: String): Boolean {
        return !actionId.contains("Uast")
                && !actionId.startsWith("Ace")
                && !actionId.endsWith("DebugAction")
                && !actionId.contains(Regex("[$.-]"))   // TODO - implement these elsewhere
                && !arrayOf(
                        // Remove some silly examples
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
                .replace("Laf", "LookAndFeel")
                .replace(Regex("^Editor"), "")
//                                    .replace("$", "")
//                                    .replace(".", "")
//                                    .replace("-", " ")
                .replace("0", "Zero")
                .replace("1", "One")
                .replace("2", "Two")
                .replace("3", "Three")
                .replace("4", "Four")
                .replace("5", "Five")
                .replace("6", "Six")
                .replace("7", "Seven")
                .replace("8", "Eight")
                .replace("9", "Nine")
                .expandCamelCase()
    }

    fun scoreActionId(actionId: String): Int {
        return when {
            actionId.startsWith("Editor") -> 5
            arrayOf("Back").contains(actionId) -> 10
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
