package org.openasr.idiolect.ide

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor

val Editor.dataContext: DataContext
    get() = DataManager.getInstance().getDataContext(contentComponent)

fun Editor.isTextCurrentlySelected() = selectionModel.hasSelection()


private fun splitCamelCase(s: String) = s.replace(String.format("%s|%s|%s",
        "(?<=[A-Z])(?=[A-Z][a-z])",
        "(?<=[^A-Z])(?=[A-Z])",
        "(?<=[A-Za-z])(?=[^A-Za-z])"
).toRegex(), " ")
