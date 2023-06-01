package org.openasr.idiolect.settings.aliases

import com.intellij.openapi.components.*


/** Common file names which are hard to say or recognise */
@Service(Service.Level.APP)
@State(
    name = "File Aliases",
    storages = [Storage("\$APP_CONFIG\$/idiolect.xml")],
    category = SettingsCategory.PLUGINS,
)
class GlobalFileAliases : BaseFileAliases() {
    companion object {
        fun getInstance() = service<GlobalFileAliases>()

        init {
            val instance = getInstance()
//            if (instance.state == null) {
//                instance.setState(State(mutableMapOf(
//                    ".editorconfig" to "editor configuration file",
//                    ".gitignore" to "git ignore"
//                )))
//            }
            instance.state!!.apply {
                if (aliases.isNullOrEmpty()) {
                    aliases = mutableMapOf(
                        ".editorconfig" to "editor configuration file",
                        ".gitignore" to "git ignore"
                    )
                }
            }
        }
    }
}
