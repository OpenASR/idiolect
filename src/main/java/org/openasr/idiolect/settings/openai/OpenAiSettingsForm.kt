package org.openasr.idiolect.settings.openai

import com.intellij.ui.dsl.builder.*
import javax.swing.*

class OpenAiSettingsForm {

    internal val apiKey = JTextField()
    internal val model = JTextField()

    fun reset() {
        apiKey.text = OpenAiConfig.apiKey
        model.text = OpenAiConfig.settings.model
    }

    internal val rootPanel: JPanel = panel {
        group("Access") {
            row { browserLink("OpenAi API Keys", "https://platform.openai.com/account/api-keys") }
            row("API key") { cell(apiKey).columns(COLUMNS_SHORT) }
            row { browserLink("GPT-3.5 models", "https://platform.openai.com/docs/models/gpt-3-5") }
            row("Model") { cell(model).columns(COLUMNS_SHORT) }
        }
    }
}
