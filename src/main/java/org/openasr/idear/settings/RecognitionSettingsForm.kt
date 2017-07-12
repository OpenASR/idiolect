package org.openasr.idear.settings

import org.openasr.idear.settings.RecognitionServiceId.AWS_LEX
import org.openasr.idear.settings.RecognitionServiceId.CMU_SPHINX
import org.openasr.idear.settings.TtsServiceId.AWS_POLLY
import org.openasr.idear.settings.TtsServiceId.MARY
import javax.swing.JPanel
import javax.swing.JRadioButton

class RecognitionSettingsForm {
    lateinit var recCMUSphinx: JRadioButton
    lateinit var recAWSLex: JRadioButton
    lateinit var ttsMary: JRadioButton
    lateinit var ttsAWSPolly: JRadioButton
    lateinit var rootPanel: JPanel

    var recognitionService: RecognitionServiceId
        get() = if (recAWSLex.isSelected) AWS_LEX else CMU_SPHINX
        set(value) = when (value) {
            AWS_LEX -> recAWSLex.isSelected = true
            CMU_SPHINX -> recCMUSphinx.isSelected = true
        }

    var ttsService: TtsServiceId
        get() = if (ttsAWSPolly.isSelected) AWS_POLLY else MARY
        set(value) = when (value) {
            AWS_POLLY -> ttsAWSPolly.isSelected = true
            MARY -> ttsMary.isSelected = true
        }
}