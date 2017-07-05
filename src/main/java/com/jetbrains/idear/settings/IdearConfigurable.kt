package com.jetbrains.idear.settings

import com.intellij.openapi.options.SearchableConfigurable
import javax.swing.JComponent


/**
 * @see http://corochann.com/intellij-plugin-development-introduction-applicationconfigurable-projectconfigurable-873.html
 */
class IdearConfigurable : SearchableConfigurable {
    private val settings: IdearSettingsProvider by lazy { IdearSettingsProvider.getInstance() }
    private var initialState: IdearSettingsProvider.State? = null
    private var gui: RecognitionSettingsForm? = null

    override fun getId() = "preferences.IdearConfigurable"

    override fun getDisplayName() = "Recognition"

    override fun apply() {
        val state = settings.getState()
        state.ttsService = gui!!.ttsService
        state.recognitionService = gui!!.recognitionService
    }

    override fun createComponent(): JComponent? {
        gui = RecognitionSettingsForm()
        initialState = settings.getState()
        reset()
        return gui?.rootPanel
    }

    override fun disposeUIResources() {
        gui = null
        initialState = null
    }

    override fun isModified(): Boolean {
        return gui!!.recognitionService != initialState!!.recognitionService ||
               gui!!.ttsService != initialState!!.ttsService
    }

    override fun reset() {
        gui!!.recognitionService = initialState!!.recognitionService
        gui!!.ttsService = initialState!!.ttsService
    }
}
