package org.openasr.idear.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import org.openasr.idear.settings.IdearConfigurable.Settings
import org.openasr.idear.settings.RecognitionServiceId.CMU_SPHINX
import org.openasr.idear.settings.TtsServiceId.MARY

/**
 * @see http://corochann.com/intellij-plugin-development-introduction-applicationconfigurable-projectconfigurable-873.html
 */

@State(name = "IdearSettingsProvider",
    storages = arrayOf(Storage("recognition.xml")))
object IdearConfigurable : Configurable, PersistentStateComponent<Settings> {
    override fun getState() = settings
    override fun loadState(state: Settings) {
        this.settings = state
    }

    private var settings = Settings()
    private lateinit var gui: RecognitionSettingsForm

    data class Settings(
        var recognitionService: RecognitionServiceId = CMU_SPHINX,
        var ttsService: TtsServiceId = MARY
    )

    override fun getDisplayName() = "Recognition"

    override fun isModified() =
        gui.recognitionService != settings.recognitionService ||
            gui.ttsService != settings.ttsService

    override fun createComponent() =
        RecognitionSettingsForm().apply { gui = this }.rootPanel

    override fun apply() {
        settings.ttsService = gui.ttsService
        settings.recognitionService = gui.recognitionService
    }

    override fun reset() {
        gui.recognitionService = settings.recognitionService
        gui.ttsService = settings.ttsService
    }
}
