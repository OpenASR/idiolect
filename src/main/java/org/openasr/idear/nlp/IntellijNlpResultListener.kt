package org.openasr.idear.nlp

import org.openasr.idear.actions.Routines
import org.openasr.idear.ide.IDEService
import org.openasr.idear.tts.TTSService
import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity


class IntellijNlpResultListener(var verbosity: Verbosity = Verbosity.INFO) : NlpResultListener {

    override fun onFulfilled(intentName: String, params: MutableMap<String, out String>?) {
        if (params != null) {
            for((key, value) in params) {
//                var remove = true
                when (key) {
                    "invokeAction" -> IDEService.invokeAction(value)
                    "pressKeystroke" -> value.split(",").forEach({keyStroke -> IDEService.type(keyStroke.toInt())})
                    "routineGoto" -> Routines.routineGoto(value)
                    "routinePress" -> Routines.routinePress(value)
                }

//                if (remove) {
//                    params.remove(key)
//                }
            }
        }
    }

    override fun onFailure(message: String) {
        TTSService.say(message)
    }

    override fun onMessage(message: String, verbosity: Verbosity) {
        if (verbosity >= this.verbosity) {
            TTSService.say(message)
        }
    }
}
