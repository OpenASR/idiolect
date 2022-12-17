package org.openasr.idear.actions

import java.awt.Component
import java.awt.event.KeyEvent

class SpeechEvent(source: Component, id: Int, time: Long, val utterance: String)
    : KeyEvent(source, id, time, 0, 0, 's', 0)
