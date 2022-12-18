package org.openasr.idear.presentation

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class RecognitionStatusBarWidgetFactory : StatusBarWidgetFactory {
    override fun getId() = RecognitionStatusBarWidget.RECOGNITION_STATUS
    override fun getDisplayName() = "Idear"

    override fun isAvailable(project: Project) = true

    override fun createWidget(project: Project) = RecognitionStatusBarWidget(project)

    override fun disposeWidget(widget: StatusBarWidget) = Disposer.dispose(widget)

    override fun canBeEnabledOn(statusBar: StatusBar) = true
}
