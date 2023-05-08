package org.openasr.idiolect.presentation.statusbar

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.*

class RecognitionStatusBarWidgetFactory : StatusBarWidgetFactory {
    companion object {
        var widget: RecognitionStatusBarWidget = RecognitionStatusBarWidget()
    }

    override fun getId() = RecognitionStatusBarWidget.RECOGNITION_STATUS
    override fun getDisplayName() = "Idiolect"

    override fun isAvailable(project: Project) = true

    override fun createWidget(project: Project): StatusBarWidget = widget

    override fun disposeWidget(widget: StatusBarWidget) = Disposer.dispose(widget)

    override fun canBeEnabledOn(statusBar: StatusBar) = true
}
