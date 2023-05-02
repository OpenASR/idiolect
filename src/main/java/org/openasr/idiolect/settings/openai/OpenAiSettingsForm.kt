package org.openasr.idiolect.settings.openai

import com.intellij.ui.dsl.builder.*
import javax.swing.*

class OpenAiSettingsForm {

    internal val apiKey = JTextField()
    internal val chatModel = JTextField()
    internal val completionModel = JTextField()

    fun reset() {
        apiKey.text = OpenAiConfig.apiKey
        chatModel.text = OpenAiConfig.settings.chatModel
        completionModel.text = OpenAiConfig.settings.completionModel
    }

    internal val rootPanel: JPanel = panel {
        group("Access") {
            row { browserLink("OpenAi API Keys", "https://platform.openai.com/account/api-keys") }
            row("API key") { cell(apiKey).columns(COLUMNS_SHORT) }
            row { browserLink("GPT-3.5 models", "https://platform.openai.com/docs/models/gpt-3-5") }
            row("Chat model") { cell(chatModel).columns(COLUMNS_SHORT) }
            row("Completion model") { cell(completionModel).columns(COLUMNS_SHORT) }
        }
    }
}
