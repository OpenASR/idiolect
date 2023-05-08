package org.openasr.idiolect.settings.openai

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import org.openasr.idiolect.nlp.ai.AiService

/**
 * Manages the Settings UI
 */
class OpenAiConfigurable : Configurable {
    private val gui by lazy(::OpenAiSettingsForm)

    override fun getDisplayName() = "OpenAI"

    override fun createComponent() = gui.rootPanel

    override fun isModified(): Boolean {
        return gui.apiKey.text != OpenAiConfig.apiKey || gui.chatModel.text != OpenAiConfig.settings.chatModel
    }

    override fun apply() {
        if (isModified) {
            OpenAiConfig.apiKey = gui.apiKey.text
            OpenAiConfig.settings.chatModel = gui.chatModel.text
            OpenAiConfig.settings.completionModel = gui.completionModel.text

            val aiService = service<AiService>()
            aiService.setApiKey(gui.apiKey.text)
            aiService.setChatModel(gui.chatModel.text)
            aiService.setCompletionModel(gui.completionModel.text)
        }
    }

    override fun reset() {
        gui.reset()
    }
}
