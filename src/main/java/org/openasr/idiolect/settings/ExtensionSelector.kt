package org.openasr.idiolect.settings

import com.intellij.openapi.extensions.*

class ExtensionSelector<T : ConfigurableExtension>(
    val extensionPointName: ExtensionPointName<T>
) : ExtensionPointListener<T> {
    private val options =
        HashMap(extensionPointName.extensionList.associate { e -> e.displayName() to ExtensionOption(e) })

    init {
        extensionPointName.addExtensionPointListener(this, null)
    }

    fun getExtensionByName(displayName: String): T =
        options.entries.firstOrNull { it.key == displayName }?.value?.extension ?: extensionPointName.extensions.first()

    override fun extensionAdded(extension: T, pluginDescriptor: PluginDescriptor) {
        options[extension.displayName()] = ExtensionOption(extension)
    }

    override fun extensionRemoved(extension: T, pluginDescriptor: PluginDescriptor) {
        options.remove(extension.displayName())
    }
}

private class ExtensionOption<T : ConfigurableExtension>(val extension: T) {
    override fun toString() = extension.displayName()
}
