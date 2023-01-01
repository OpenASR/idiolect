package org.openasr.idiolect.settings

interface ConfigurableExtension {
    fun displayName(): String
    fun activate() {} // default no-op
    fun deactivate() {} // default no-op
}
