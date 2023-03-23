package org.openasr.idiolect.testing

import com.intellij.openapi.actionSystem.DataContext

class TestContext : DataContext {
    override fun getData(dataId: String): Any? = "test"
}
