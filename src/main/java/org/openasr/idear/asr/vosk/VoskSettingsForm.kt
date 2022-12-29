package org.openasr.idear.asr.vosk

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.*
import com.intellij.ui.dsl.builder.*
import java.awt.event.ItemEvent
import javax.swing.*

class VoskSettingsForm : TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor()) {
    internal val modelPathChooser = TextFieldWithBrowseButton()

    internal val languageCombo = ComboBox<String>()
    private val modelInfoCombo = ComboBox<ModelInfo>()
    private val installButton = JButton("Install")
    private var modelInfoOptions: List<ModelInfo> = emptyList()

    init {
        modelPathChooser.addBrowseFolderListener(this)
        modelInfoOptions = VoskAsr.listModels()

        initialiseLanguages()

        filterModelInfoOptionsByLang(languageCombo.selectedItem as String)

        languageCombo.addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                filterModelInfoOptionsByLang(e.item as String)
            }
        }

        installButton.addActionListener { _ -> onClickInstall() }

        modelInfoCombo.addActionListener { _ ->
            installButton.isEnabled = true
        }
    }

    private fun onClickInstall() {
        installButton.isEnabled = false
        installButton.text = "Installing model..."

        val url = (modelInfoCombo.selectedItem as ModelInfo).url
        modelPathChooser.text = VoskAsr.pathForModelUrl(url)
        rootPanel.repaint()

        ApplicationManager.getApplication().invokeLater {
            try {
                VoskAsr.installModel(url)
                installButton.text = "Install"
            } catch (e: Exception) {
                installButton.text = "Installation failed"
            }
        }
    }

    internal val rootPanel: JPanel = panel {
        group("Model") {
            row { browserLink("https://alphacephei.com/vosk/models", "https://alphacephei.com/vosk/models") }
            row("Language") { cell(languageCombo).columns(COLUMNS_SHORT) }
            row("Install model") {
                cell(modelInfoCombo).columns(COLUMNS_SHORT)
                cell(installButton)
            }
            row("Model path") { cell(modelPathChooser).columns(COLUMNS_LARGE) }
        }
    }

    private fun initialiseLanguages() {
        val languages = modelInfoOptions.map { it.langText }.toSortedSet { a, b ->
            val aIsEnglish = a.contains("English")
            val bIsEnglish = b.contains("English")
            if (aIsEnglish && bIsEnglish) {
                b.compareTo(a)
            }
            else if (aIsEnglish && !bIsEnglish) {
                -1
            } else if (!aIsEnglish && bIsEnglish) {
                1
            } else {
                a.compareTo(b)
            }
        }

        for (lang in languages) {
            languageCombo.addItem(lang)
        }
    }

    private fun filterModelInfoOptionsByLang(lang: String? = "US English") {
        modelInfoCombo.removeAllItems()

        var options = modelInfoOptions
        if (lang != null) {
            options = options.filter { it.langText == lang }
        }

        for (option in options) {
            modelInfoCombo.addItem(option)
        }
    }
}
