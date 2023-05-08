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
            set(apiKey) {
                PasswordSafe.instance.setPassword(credentialAttributes, apiKey)
            }
    }

    override fun getState() = settings

    override fun loadState(state: Settings) {
        settings = state
    }

    data class Settings(var chatModel: String = "gpt-3.5-turbo",
                        var completionModel: String = "text-davinci-003",
                        var maxTokens: Int = 16,
                        var temperature: Double? = null, // = 1.0,
                        var topP: Double? = null         // = 1.0
        )
}
