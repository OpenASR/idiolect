package org.openasr.idiolect.settings.aliases

import com.intellij.openapi.components.PersistentStateComponent

abstract class BaseFileAliases : PersistentStateComponent<BaseFileAliases.State> {
    /**
     * .env=dot env
     * .editorconfig=editor configuration
     */
    data class State(var aliases: MutableMap<String, String>?)

    private var state: State = State(mutableMapOf())
//        private var state: State? = State(mapOf())

//    companion object {
//        fun getInstance(project: Project): Companion = project.getService(Companion::class.java)
//    }

    fun getAliasesForFiles(fileNames: List<String>) {
        state.aliases?.entries?.filter { entry -> fileNames.contains(entry.key) }
    }

    fun getAliases() = state.aliases

    fun setFileAlias(fileName: String, alias: String) {
        state.aliases?.put(fileName, alias)
    }

    override fun getState(): State? = state

    protected fun setState(state: State) {
        this.state = state
    }

    override fun loadState(state: State) {
        this.state = state
    }
}
