package org.openasr.idiolect.settings

import com.intellij.openapi.diagnostic.DefaultLogger
import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.annotations.NonNls


class PrintlnLogger(category: String) : DefaultLogger(category) {
    private val clazz: String

    companion object {
        private var installChecked = false
        
        fun installForLocalDev() {
            if (!installChecked) {
                if (System.getProperty("idiolect.environment") == "development") {
                    val factory = Logger.Factory { category ->
                        if (category.startsWith("#org.openasr.idiolect")) PrintlnLogger(category)
                        else DefaultLogger(category)
                    }

                    Logger.setFactory(factory)
                }

                installChecked = true
            }
        }
    }

    init {
        clazz = category.substringAfterLast(".")
    }

    override fun isDebugEnabled(): Boolean {
        return true
    }

    override fun debug(message: String?) {
        println("DEBUG ($clazz): $message")
    }

    override fun debug(t: Throwable?) {
        t?.apply {
            debug(message)
            printStackTrace(System.out)
        }
    }

    override fun debug(message: @NonNls String?, t: Throwable?) {
        debug(message)
        t?.printStackTrace(System.out)
    }

    override fun info(message: String?) {
        println("INFO  ($clazz): $message")
    }

    override fun info(message: String?, t: Throwable?) {
        info(message)
        t?.printStackTrace(System.out)
    }
}
