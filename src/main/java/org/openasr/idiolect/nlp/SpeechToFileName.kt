package org.openasr.idiolect.nlp

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import org.openasr.idiolect.utils.speechFriendlyFileName

//@State(
//    name = "File Aliases",
//    storages = [
//        Storage("\$APP_CONFIG\$/idiolect.xml"),
//        Storage(value=StoragePathMacros.WORKSPACE_FILE,
//            stateSplitter = StateSplitterEx
//
//        /*, id="workspace", isDefault=false*/)
//    ]
//)
class SpeechToFileName /*: PersistentStateComponent<State>*/ {
    /**
     * .env=dot env
     * .editorconfig=editor configuration
     */
    data class State(var aliases: Map<String, String>)

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
//
//            if (project != null) {
//                ProjectFileAliases.getInstance(project).getAliasesForFiles(fileNames)
//            }
//        }

        private fun getGlobalAliases(): Map<String, String>? {
//            return GlobalFileAliases.getInstance().getAliases()
            return mapOf(
                ".editorconfig" to "editor configuration file",
                ".gitignore" to "git ignore"
            )
        }

        private fun getProjectAliases(project: Project?): Map<String, String>? {
//            return if (project == null) null else ProjectFileAliases.getInstance(project).getAliases()
            return null
        }
    }


/*

    @com.intellij.openapi.components.State(
        name = "File Aliases",
        storages = [Storage("\$APP_CONFIG\$/idiolect.xml")],
    )
    class GlobalFileAliases : BaseFileAliases() {
        companion object {
            fun getInstance() = service<GlobalFileAliases>()

            init {
                val instance = getInstance()
                if (instance.state == null) {
                    instance.setState(State(mapOf(
                        ".editorconfig" to "editor configuration file",
                        ".gitignore" to "git ignore"
                        )))
                }
            }
        }
    }

    @com.intellij.openapi.components.State(
        name = "File Aliases",
        storages = [Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)],
    )
    class ProjectFileAliases : BaseFileAliases() {
        companion object {
            fun getInstance(project: Project) = project.getService(ProjectFileAliases::class.java)
        }
    }

    abstract class BaseFileAliases(/*private var state: State*/) : PersistentStateComponent<State> {
        private var state: State? = null // State(mapOf())

        companion object {
            fun getInstance(project: Project) = project.getService(Companion::class.java)
        }

//        fun getAliasesForFiles(fileNames: List<String>) {
//            state.aliases.entries.filter { entry -> fileNames.contains(entry.key) }
//        }

        fun getAliases() = state?.aliases

        override fun getState(): State? = state

        protected fun setState(state: State) {
            this.state = state
        }

        override fun loadState(state: State) {
            this.state = state
        }
    } */
}
