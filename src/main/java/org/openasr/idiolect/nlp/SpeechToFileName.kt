package org.openasr.idiolect.nlp

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import org.openasr.idiolect.settings.aliases.GlobalFileAliases
import org.openasr.idiolect.settings.aliases.ProjectFileAliases
import org.openasr.idiolect.utils.speechFriendlyFileName

@Service
class SpeechToFileName {

    companion object {
        fun pickFileByAlias(project: Project?, alias: String): (fileName: String) -> Boolean {
            val projectFileAliases = getProjectAliases(project)
            val globalFileAliases = getGlobalAliases()

            return { fileName: String ->
                speechFriendlyFileName(fileName) == alias
                    || (projectFileAliases != null && projectFileAliases[fileName] == alias)
                    || globalFileAliases?.get(fileName) == alias
            }
        }

//        fun getAliasesForFiles(project: Project?, fileNames: List<String>) {
//            val aliases = GlobalFileAliases.getInstance().getAliasesForFiles(fileNames)
//
//            if (project != null) {
//                ProjectFileAliases.getInstance(project).getAliasesForFiles(fileNames)
//            }
//        }

        private fun getGlobalAliases(): Map<String, String>? {
            return GlobalFileAliases.getInstance().getAliases()
//            return mapOf(
//                ".editorconfig" to "editor configuration file",
//                ".gitignore" to "git ignore"
//            )
        }

        private fun getProjectAliases(project: Project?): Map<String, String>? {
            return if (project == null) null else ProjectFileAliases.getInstance(project).getAliases()
//            return null
        }
    }
}
