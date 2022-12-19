package org.openasr.idear.actions

import org.openasr.idear.nlp.NlpRequest
import java.awt.Component
import java.awt.event.KeyEvent

class SpeechEvent(source: Component, id: Int, time: Long, val nlpRequest: NlpRequest)
    : KeyEvent(source, id, time, 0, 0, 's', 0)
