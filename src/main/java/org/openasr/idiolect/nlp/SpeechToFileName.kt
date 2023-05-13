package org.openasr.idiolect.nlp

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros

class SpeechToFileName {

    /**
     * .env=dot env
     * .editorconfig=editor configuration
     */
    data class State(var aliases: Map<String, String>)

    @com.intellij.openapi.components.State(
        name = "File Aliases",
        storages = [Storage("\$APP_CONFIG\$/idiolect.xml")],
    )
    class CustomFileAliases : PersistentStateComponent<State> {
        override fun getState(): State? {
            TODO("Not yet implemented")
        }

        override fun loadState(state: State) {
            TODO("Not yet implemented")
        }

    }

    @com.intellij.openapi.components.State(
        name = "File Aliases",
        storages = [Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)],
    )
    class ProjectFileAliases : PersistentStateComponent<State> {

    }
}
