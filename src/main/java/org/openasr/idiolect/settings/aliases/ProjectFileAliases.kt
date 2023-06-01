package org.openasr.idiolect.settings.aliases

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(
    name = "File Aliases",
    storages = [Storage(StoragePathMacros.PRODUCT_WORKSPACE_FILE)], // or "idiolect.xml"?
    category = SettingsCategory.PLUGINS,
)
class ProjectFileAliases : BaseFileAliases() {
    companion object {
        fun getInstance(project: Project) = project.service<ProjectFileAliases>()
    }
}
