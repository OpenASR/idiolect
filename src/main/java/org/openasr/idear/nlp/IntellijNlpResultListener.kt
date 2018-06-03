package org.openasr.idear.nlp

import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.ide.IDEService
import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity
import org.openasr.idear.tts.TTSService


class IntellijNlpResultListener(var verbosity: Verbosity = Verbosity.INFO) : NlpResultListener {

    override fun onFulfilled(intentName: String, slots: MutableMap<String, out String>?, sessionAttributes: MutableMap<String, out String>?) {
        sessionAttributes?.forEach { (key, value) ->
            //                var remove = true
            when (key) {
                "invokeAction" -> IDEService.invokeAction(value)
                "pressKeystroke" -> value.split(",").forEach { keyStroke -> IDEService.type(keyStroke.toInt()) }
                "routineGoto" -> ActionRoutines.routineGoto(value)
                "routinePress" -> ActionRoutines.routinePress(value)
            }

//                if (remove) {
//                    params.remove(key)
//                }
        }
    }

    override fun onFailure(message: String) = TTSService.say(message)

    override fun onMessage(message: String, verbosity: Verbosity) {
        if (verbosity >= this.verbosity) {
            TTSService.say(message)
        }
    }
}
