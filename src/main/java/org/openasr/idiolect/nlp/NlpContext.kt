package org.openasr.idiolect.nlp

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.DataKey

class NlpContext(public var dataContext: DataContext) {
    fun getData(dataId: String) = dataContext.getData(dataId)
    fun <T> getData(dataId: DataKey<T>) = dataContext.getData(dataId)
}
