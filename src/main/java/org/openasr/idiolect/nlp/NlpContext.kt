package org.openasr.idiolect.nlp

import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKey

class NlpContext(private var dataContext: DataContext) {
    enum class Mode {
        ACTION,
        EDIT,
        CHAT
    }

    companion object {
        var mode: Mode = Mode.ACTION
    }

    fun getDataContext() = dataContext
    fun getData(dataId: String) = dataContext.getData(dataId)
    fun <T> getData(dataId: DataKey<T>) = dataContext.getData(dataId)

    fun getEditor() = dataContext.getData(CommonDataKeys.EDITOR)
    fun getProject() = getEditor()?.project
    fun getCurrentFile() = dataContext.getData(CommonDataKeys.PSI_FILE)


    fun setMode(mode: Mode) {
        NlpContext.mode = mode
    }
    fun getMode() = mode
    fun isActionMode() = mode == Mode.ACTION
    fun isEditMode() = mode == Mode.EDIT
    fun isChatMode() = mode == Mode.CHAT
}
