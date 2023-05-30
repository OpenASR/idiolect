package org.openasr.idiolect.asr

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.dsl.builder.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openasr.idiolect.asr.models.ModelInfo
import org.openasr.idiolect.asr.models.ModelManager
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JPanel

abstract class AsrProviderSettingsForm<C : Configurable>(private val modelManager: ModelManager<C>)
    : TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFolderDescriptor())
{
    companion object {
        protected val log = logger<AsrProvider>()
    }

    internal val languageCombo = ComboBox<String>()
    private val modelInfoCombo = ComboBox<ModelInfo>()
    private val installButton = JButton("Install")
    internal val modelPathChooser = TextFieldWithBrowseButton()

    private var modelInfoOptions: List<ModelInfo> = emptyList()

    init {
        modelPathChooser.addBrowseFolderListener(this)
        modelInfoOptions = modelManager.listModels()

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

    abstract fun reset(): Unit

    private fun onClickInstall() {
        installButton.isEnabled = false
        installButton.text = "Installing model..."

        val model = (modelInfoCombo.selectedItem as ModelInfo)
        val url = model.url
        modelPathChooser.text = modelManager.pathForModelUrl(url)
        rootPanel.repaint()

        CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            try {
                modelManager.installModel(model)
                installButton.text = "Install"
            } catch (e: Exception) {
                log.error("Failed to install model", e)
                installButton.text = "Installation failed"
            }
        }
    }

    internal val rootPanel: JPanel = panel {
        group("Model") {
            row { browserLink(modelManager.modelsPageUrl, modelManager.modelsPageUrl) }
            row("Language") { cell(languageCombo).columns(COLUMNS_SHORT) }
            row("Install model") {
                cell(modelInfoCombo).columns(COLUMNS_MEDIUM)
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

    protected fun selectModel(modelPath: String) {
        if (!modelPath.isNullOrEmpty()) {
            var slash = modelPath.lastIndexOf('/')
            val modelFile = modelPath.substring(slash + 1)

            for (i in 0 until modelInfoCombo.model.size) {
                val model = modelInfoCombo.model.getElementAt(i)
                slash = model.url.lastIndexOf('/')
                if (model.url.substring(slash + 1) == modelFile) {
                    modelInfoCombo.selectedItem = model
                    break
                }
            }
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