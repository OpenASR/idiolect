package org.openasr.idear.testing

import com.intellij.openapi.actionSystem.DataContext

class TestContext : DataContext {
    override fun getData(dataId: String): Any? {
        return "test"
    }
}
