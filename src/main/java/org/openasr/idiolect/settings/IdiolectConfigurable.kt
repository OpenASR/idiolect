package org.openasr.idiolect.settings

import com.intellij.openapi.components.*
import com.intellij.openapi.options.Configurable
import org.openasr.idiolect.asr.AsrService
import org.openasr.idiolect.nlp.*
import org.openasr.idiolect.settings.IdiolectConfig.Companion.initialiseAsrSystem
import org.openasr.idiolect.settings.IdiolectConfig.Companion.settings
import org.openasr.idiolect.tts.*

/**
 * Manages the Settings UI
 *
 * @see http://corochann.com/intellij-plugin-development-introduction-applicationconfigurable-projectconfigurable-873.html
 */
class IdiolectConfigurable : Configurable {
    private val asrService = service<AsrService>()
    private val gui by lazy(::RecognitionSettingsForm)

    override fun getDisplayName() = "Idiolect"

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
                settings.asrService = gui.asrService
                asrService.setAsrSystem(initialiseAsrSystem(gui.asrService))
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
