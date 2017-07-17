package org.openasr.idear.asr

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions.ACTION_RECENT_FILES
import com.intellij.openapi.actionSystem.IdeActions.ACTION_SHOW_SETTINGS
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.Consumer
import org.openasr.idear.GoogleHelper
import org.openasr.idear.GoogleHelper.getBestTextForUtterance
import org.openasr.idear.WordToNumberConverter
import org.openasr.idear.actions.Routines
import org.openasr.idear.actions.recognition.SurroundWithNoNullCheckRecognizer
import org.openasr.idear.ide.IDEService
import org.openasr.idear.ide.IDEService.invokeAction
import org.openasr.idear.nlp.Commands
import org.openasr.idear.nlp.NlpProvider
import org.openasr.idear.recognizer.CustomMicrophone.Companion.recordFromMic
import org.openasr.idear.tts.TTSService.say
import java.awt.EventQueue
import java.awt.event.KeyEvent.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import javax.sound.sampled.AudioSystem

class ASRControlLoop(private val asrProvider: ASRProvider, private val nlpProvder: NlpProvider) : Runnable {
    override fun run() {
        while (!ListeningState.isTerminated) {
            // This blocks on a recognition result
            val result = asrProvider.waitForUtterance()

            //TODO: nlpService.processUtterance(result, getContext())

            if (ListeningState.isInit) {
                if (result == Commands.HI_IDEA) {
                    // Greet invoker
                    say("Hi")
                    invokeAction("Idear.Start")
                }
            } else if (ListeningState.isActive) {
                logger.info("Recognized: $result")

                nlpProvder.processUtterance(result)
            }
        }
    }


    companion object {
        private val logger = Logger.getInstance(ASRControlLoop::class.java)



        private fun splitCamelCase(s: String): String {
            return s.replace(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"
            ).toRegex(), " ")
        }
    }
}
