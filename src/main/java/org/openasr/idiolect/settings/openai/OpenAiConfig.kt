package org.openasr.idiolect.settings.openai

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.components.*
import com.intellij.util.application

/**
 * Persists State across IDE restarts
 */
@State(name = "OpenAI",
    storages = [Storage("\$APP_CONFIG\$/idiolect.xml")],
    category = SettingsCategory.PLUGINS
)
class OpenAiConfig : PersistentStateComponent<OpenAiConfig.Settings> {
    private var settings = Settings()

    companion object {
        val settings get() = application.getService(OpenAiConfig::class.java).settings
        private val credentialAttributes = CredentialAttributes(generateServiceName("openai", "idiolect"))
        var apiKey get() = PasswordSafe.instance.getPassword(credentialAttributes)
            set(password) = PasswordSafe.instance.setPassword(credentialAttributes, password)
    }

    override fun getState() = settings

    override fun loadState(state: Settings) {
        settings = state
    }

    data class Settings(var model: String = "gpt-3.5-turbo")
}
