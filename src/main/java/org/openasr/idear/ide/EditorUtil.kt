package org.openasr.idear.ide

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor

val Editor.dataContext: DataContext
    get() = DataManager.getInstance().getDataContext(contentComponent)
