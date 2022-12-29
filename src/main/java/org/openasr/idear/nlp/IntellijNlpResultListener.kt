package org.openasr.idear.nlp

import org.openasr.idear.actions.recognition.ActionCallInfo
import org.openasr.idear.nlp.NlpResultListener.Companion.Verbosity
import org.openasr.idear.tts.TtsService


class IntellijNlpResultListener(private var verbosity: Verbosity = Verbosity.INFO) : NlpResultListener {

    override fun onListening(listening: Boolean) {

    }

    override fun onRecognition(nlpRequest: NlpRequest) {

    }

    override fun onFulfilled(actionCallInfo: ActionCallInfo) {
//        sessionAttributes?.forEach { (key, value) ->
//            //                var remove = true
//            when (key) {
//                "invokeAction" -> IdeService.invokeAction(value)
//                "pressKeystroke" -> value.split(",").forEach { keyStroke -> IdeService.type(keyStroke.toInt()) }
//                "routineGoto" -> ActionRoutines.routineGoto(value)
//                "routinePress" -> ActionRoutines.routinePress(value)
//            }
//
////                if (remove) {
////                    params.remove(key)
////                }
//        }
    }

    override fun onFailure(message: String) = TtsService.say(message)

    override fun onMessage(message: String, verbosity: Verbosity) {
        if (verbosity >= this.verbosity) {
            TtsService.say(message)
        }
    }
}
