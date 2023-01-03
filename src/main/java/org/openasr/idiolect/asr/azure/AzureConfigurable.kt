package org.openasr.idiolect.asr.azure

import com.intellij.openapi.options.Configurable
import org.openasr.idiolect.asr.azure.AzureConfig.Companion.settings

/**
 * Manages the Settings UI
 */
class AzureConfigurable : Configurable {
    private val gui by lazy(::AzureSettingsForm)

    override fun getDisplayName() = "Azure"

    override fun createComponent() = gui.rootPanel

    override fun isModified(): Boolean {
        return gui.subscriptionKey.text != settings.speechSubscriptionKey
            || gui.serviceRegion.text != settings.serviceRegion
    }

    override fun apply() {
        if (isModified) {
            settings.speechSubscriptionKey = gui.subscriptionKey.text
            settings.serviceRegion = gui.serviceRegion.text
        }
    }

    override fun reset() {
        gui.reset(settings)
    }
}
