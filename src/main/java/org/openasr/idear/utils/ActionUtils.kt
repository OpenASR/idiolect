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

        return actionManager.getActionIdList("")
                .filter { actionId -> !actionManager.isGroup(actionId) }
                .filter { !it.contains("Uast")}
                .filter { !it.contains(Regex("[$.-]"))}   // TODO - implement these elsewhere
                .filter { !arrayOf(
                        // Remove some silly examples
                        "AceLineAction",
                        "UsageGrouping.FlattenModules",
                        "SegmentedVcsControlAction",
                        "ExpandCollapseToggleAction",
                        "BookmarksView.MoveDown",
                        "Diff.OpenDiffInEditor",
                        "Vcs.ReformatCommitMessage",
                        "Vcs.ShowMessageHistory",
                        "Vcs.Log.CompactReferencesView",
                        "LocalHistory.ShowSelectionHistory",
                        "ChangesView.CreatePatchFromChanges",
                        "DirDiffMenu.EnableNotEqual",
                        "ExternalSystem.RefreshAllProjects",
                        "ExternalSystem.IgnoreProject",
                        "ExternalSystem.CreateRunConfiguration",
                        "RunDashboard.Stop",
                        "RoundedIconTestAction",
                        "ProjectView.CompactDirectories",
                        "DirDiffMenu.SetDelete",
                        "Table-selectNextColumn",
                        "UiDslTestAction",
                        "RestoreFontPreviewTextAction",
                        "WrapLayoutTestAction",
                        "Scratch.ChangeLanguage",
                        "MarkVfsCorrupted",
                        "BookmarksView.Delete",
                        "CollectFUStatisticsAction",
                        "Tree-selectFirstExtendSelection",
                        "BookmarksView.DefaultGroup",
                        "Document2XSD"
                ).contains(it) }
                .filter { !it.startsWith("com.intellij")}
                .map { actionId ->
                    NlpGrammar(actionId)
                            .withExample(actionId
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
                                    .expandCamelCase())
                }
                .sortedBy { gram -> gram.examples.first() }
    }
}
