package org.openasr.idear.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.options.Configurable
import org.openasr.idear.settings.IdearConfig.Companion.settings
import org.openasr.idear.asr.AsrService
import org.openasr.idear.nlp.*
import org.openasr.idear.settings.IdearConfig.Companion.initialiseAsrSystem
import org.openasr.idear.tts.*

/**
 * Manages the Settings UI
 *
 * @see http://corochann.com/intellij-plugin-development-introduction-applicationconfigurable-projectconfigurable-873.html
 */
class IdearConfigurable : Configurable {
    private val gui by lazy(::RecognitionSettingsForm)

    override fun getDisplayName() = "Idear"

    override fun createComponent() = gui.rootPanel

    companion object {

    }

    override fun isModified(): Boolean =
        gui.asrService != settings.asrService ||
            gui.nlpService != settings.nlpService ||
            gui.ttsService != settings.ttsService

    /**
     * Stores the settings from the Swing form to the configurable component.
     * This method is called on EDT upon user's request.
     */
    override fun apply() {
        if (isModified) {
            if (settings.asrService != gui.asrService) {
                AsrService.setAsrSystem(initialiseAsrSystem())
                settings.asrService = gui.asrService
            }
            settings.nlpService = gui.nlpService
            settings.ttsService = gui.ttsService
        }
    }

    /**
     * Loads the settings from the configurable component to the Swing form.
     * This method is called on EDT immediately after the form creation or later upon user's request.
     */
    override fun reset() {
        gui.reset(settings)
    }
}
