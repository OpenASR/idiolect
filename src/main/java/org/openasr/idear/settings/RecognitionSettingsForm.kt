package org.openasr.idear.settings

import org.openasr.idear.settings.RecognitionServiceId.*
import org.openasr.idear.settings.TtsServiceId.*
import javax.swing.JPanel
import javax.swing.JRadioButton

class RecognitionSettingsForm {
  lateinit var recCMUSphinx: JRadioButton
  lateinit var recAWSLex: JRadioButton
  lateinit var recApiAi: JRadioButton
  lateinit var ttsMary: JRadioButton
  lateinit var ttsAWSPolly: JRadioButton
  lateinit var ttsApiAi: JRadioButton
  lateinit var rootPanel: JPanel

  var recognitionService: RecognitionServiceId
    get() = if (recAWSLex.isSelected) AWS_LEX
    else if (recApiAi.isSelected) API_AI_STT
    else CMU_SPHINX
    set(value) = when (value) {
      AWS_LEX -> recAWSLex.isSelected = true
      API_AI_STT -> recApiAi.isSelected = true
      CMU_SPHINX -> recCMUSphinx.isSelected = true
    }

  var ttsService: TtsServiceId
    get() = if (ttsAWSPolly.isSelected) AWS_POLLY
    else if (ttsApiAi.isSelected) API_AI_TTS
    else MARY
    set(value) = when (value) {
      AWS_POLLY -> ttsAWSPolly.isSelected = true
      API_AI_TTS -> ttsApiAi.isSelected = true
      MARY -> ttsMary.isSelected = true
    }
}