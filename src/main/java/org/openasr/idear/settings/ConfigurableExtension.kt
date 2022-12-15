package org.openasr.idear.settings

interface ConfigurableExtension {
    fun displayName(): String
    fun activate() {} // default no-op
    fun deactivate() {} // default no-op
}
