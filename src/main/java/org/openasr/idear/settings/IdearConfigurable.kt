package org.openasr.idear.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.options.Configurable
import org.openasr.idear.settings.ASRServiceId.CMU_SPHINX
import org.openasr.idear.settings.IdearConfigurable.Settings
import org.openasr.idear.settings.TTSServiceId.MARY

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
            var asrService: ASRServiceId = CMU_SPHINX,
            var ttsService: TTSServiceId = MARY
    )

    override fun getDisplayName() = "Recognition"

    override fun isModified() =
        gui.asrService != settings.asrService ||
            gui.ttsService != settings.ttsService

    override fun createComponent() =
        RecognitionSettingsForm().apply { gui = this }.rootPanel

    override fun apply() {
        settings.ttsService = gui.ttsService
        settings.asrService = gui.asrService
    }

    override fun reset() {
        gui.asrService = settings.asrService
        gui.ttsService = settings.ttsService
    }
}
