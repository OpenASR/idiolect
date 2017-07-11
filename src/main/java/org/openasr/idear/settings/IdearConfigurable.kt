package org.openasr.idear.settings

import com.intellij.openapi.options.SearchableConfigurable
import org.openasr.idear.settings.IdearSettingsProvider.Companion.instance
import org.openasr.idear.settings.IdearSettingsProvider.State
import javax.swing.JComponent

/**
 * @see http://corochann.com/intellij-plugin-development-introduction-applicationconfigurable-projectconfigurable-873.html
 */
class IdearConfigurable : SearchableConfigurable {
    private val settings: IdearSettingsProvider by lazy { instance }
    private lateinit var initialState: State
    private lateinit var gui: RecognitionSettingsForm

    override fun getId() = "preferences.IdearConfigurable"

    override fun getDisplayName() = "Recognition"

    override fun apply() {
        val state = settings.state
        state.ttsService = gui.ttsService
        state.recognitionService = gui.recognitionService
    }

    override fun createComponent(): JComponent {
        gui = RecognitionSettingsForm()
        initialState = settings.state
        reset()
        return gui.rootPanel
    }

    override fun isModified() =
        gui.recognitionService != initialState.recognitionService ||
               gui.ttsService != initialState.ttsService

    override fun reset() {
        gui.recognitionService = initialState.recognitionService
        gui.ttsService = initialState.ttsService
    }
}
