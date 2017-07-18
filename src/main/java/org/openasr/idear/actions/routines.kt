package org.openasr.idear.actions

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Pair
import com.intellij.util.Consumer
import org.openasr.idear.GoogleHelper
import org.openasr.idear.WordToNumberConverter
import org.openasr.idear.actions.recognition.SurroundWithNoNullCheckRecognizer
import org.openasr.idear.asr.ASRService
import org.openasr.idear.asr.ListeningState
import org.openasr.idear.ide.IDEService
import org.openasr.idear.nlp.Commands
import org.openasr.idear.recognizer.CustomMicrophone
import org.openasr.idear.tts.TTSService
import java.awt.EventQueue
import java.awt.event.KeyEvent
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import javax.sound.sampled.AudioSystem


object Routines {
    private val COMMAND_DURATION: Long = 3500
    private val GOOGLE_QUERY_DURATION: Long = 3000
    private val logger = Logger.getInstance(Routines::class.java)

    fun routineReleaseKey(c: String) {
        if (c.contains("shift")) IDEService.releaseShift()
    }

    fun routineFind(c: String) {
        if (c.endsWith("file")) {
            IDEService.invokeAction("Find")
        } else if (c.endsWith("project")) {
            IDEService.invokeAction("FindInPath")
        }
    }

    fun routineOfLine(c: String) {
        if (c.startsWith("beginning")) {
            IDEService.type(KeyEvent.VK_META, KeyEvent.VK_LEFT)
        } else if (c.startsWith("end")) {
            IDEService.type(KeyEvent.VK_META, KeyEvent.VK_RIGHT)
        }
    }

    fun routinePsvm() {
        IDEService.type("psvm")
        pressKeystroke(KeyEvent.VK_TAB)
    }

    fun routineEnter(c: String) {
        val result = webSpeechResult
        if (result != null) {
            if (c.endsWith("text")) {
                IDEService.type(result.first)
            } else if (c.endsWith("camel case")) {
                IDEService.type(convertToCamelCase(result.first))
            }
        }
    }

    fun routineNewString() {
        val result = webSpeechResult
        if (result != null) {
            IDEService.type(KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE)
            IDEService.type(result.first)
            IDEService.type(KeyEvent.VK_SHIFT, KeyEvent.VK_QUOTE)
        }
    }

    fun routinePrintln() {
        IDEService.type("sout")
        pressKeystroke(KeyEvent.VK_TAB)
    }

    fun routineAddNewClass() {
        IDEService.invokeAction("NewElement")
        pressKeystroke(KeyEvent.VK_ENTER)
        val className = webSpeechResult
        if (className != null) {
            var camelCase = convertToCamelCase(className.first)
            logger.info("Class name: $camelCase")
            camelCase = camelCase.substring(0,
                    1).toUpperCase() + camelCase.substring(1)
            IDEService.type(camelCase)
            pressKeystroke(KeyEvent.VK_ENTER)
        }
    }

    fun routineAbout() {
        val ai = ApplicationInfo.getInstance()

        val cal = ai.buildDate
        val df = SimpleDateFormat("EEEE, MMMM dd, yyyy")

        TTSService.say("My name is " + ai.versionName + ", I was built on " + df.format(cal.time) + ", I am running version " + ai.apiVersion + " of the IntelliJ Platform, and I am registered to " + ai.companyName)
    }

    fun routineCheck(c: String) {
        val nullCheckRecognizer = SurroundWithNoNullCheckRecognizer()
        if (nullCheckRecognizer.isMatching(c)) {
            DataManager.getInstance()
                    .dataContextFromFocus
                    .doWhenDone({ dataContext: DataContext ->
                        run(nullCheckRecognizer,
                                c,
                                dataContext)
                    } as Consumer<DataContext>)
        }
    }

    fun routineStep(c: String) {
        if (c.endsWith("over")) {
            IDEService.invokeAction("StepOver")
        } else if (c.endsWith("into")) {
            IDEService.invokeAction("StepInto")
        } else if (c.endsWith("return")) {
            IDEService.invokeAction("StepOut")
        }
    }

    fun routineHandleBreakpoint(c: String) {
        if (c.startsWith("toggle")) {
            IDEService.invokeAction("ToggleLineBreakpoint")
        } else if (c.startsWith("view")) {
            IDEService.invokeAction("ViewBreakpoints")
        }
    }

    fun routineOkIdea() {
        beep()
        fireVoiceCommand()
    }

    fun routineExtract(c: String) {
        if (c.endsWith("method")) {
            IDEService.invokeAction("ExtractMethod")
        } else if (c.endsWith("parameter")) {
            IDEService.invokeAction("IntroduceParameter")
        }
    }

    fun routineFollowing(c: String) {
        if (c.endsWith(Commands.LINE)) {
            IDEService.invokeAction("EditorDown")
        } else if (c.endsWith(Commands.PAGE)) {
            IDEService.invokeAction("EditorPageDown")
        } else if (c.endsWith(Commands.METHOD)) {
            IDEService.invokeAction("MethodDown")
        } else if (c.endsWith("tab")) {
            IDEService.invokeAction("Diff.FocusOppositePane")
        } else if (c.endsWith("page")) {
            IDEService.invokeAction("EditorPageDown")
        } else if (c.endsWith("word")) {
            IDEService.type(KeyEvent.VK_ALT, KeyEvent.VK_RIGHT)
        }
    }

    fun routinePress(c: String) {
        if (c.contains(Commands.DELETE)) {
            pressKeystroke(KeyEvent.VK_DELETE)
        } else if (c.contains("return") || c.contains("enter")) {
            pressKeystroke(KeyEvent.VK_ENTER)
        } else if (c.contains(Commands.ESCAPE)) {
            pressKeystroke(KeyEvent.VK_ESCAPE)
        } else if (c.contains(Commands.TAB)) {
            pressKeystroke(KeyEvent.VK_TAB)
        } else if (c.contains(Commands.UNDO)) {
            IDEService.invokeAction("\$Undo")
        } else if (c.contains("shift")) {
            IDEService.pressShift()
        }
    }

    fun routineGoto(c: String) {
        IDEService.invokeAction("GotoLine").doWhenDone({
            IDEService.type(*("" + WordToNumberConverter.getNumber(c.substring(
                    10))).toCharArray())
            IDEService.type(KeyEvent.VK_ENTER)
        })
    }

    fun routineOpen(c: String) {
        when {
            c.endsWith(Commands.SETTINGS) -> IDEService.invokeAction(IdeActions.ACTION_SHOW_SETTINGS)
            c.endsWith(Commands.RECENT) -> IDEService.invokeAction(IdeActions.ACTION_RECENT_FILES)
            c.endsWith(Commands.TERMINAL) -> IDEService.invokeAction("ActivateTerminalToolWindow")
        }
    }

    fun routineFocus(c: String) {
        when {
            c.endsWith(Commands.EDITOR) -> pressKeystroke(KeyEvent.VK_ESCAPE)
            c.endsWith(Commands.PROJECT) -> IDEService.invokeAction("ActivateProjectToolWindow")
            c.endsWith("symbols") -> {
                val ar = IDEService.invokeAction("AceAction")

                while (!ar.isProcessed) {
                    //Spin lock
                    logger.info("Not done...")
                    try {
                        Thread.sleep(250)
                    } catch (e: InterruptedException) {
                        logger.warn(e)
                    }
                }
                logger.info("Done!")

                IDEService.type(" ")
                val jumpMarker = recognizeJumpMarker()
                IDEService.type("" + jumpMarker)
                logger.info("Typed: " + jumpMarker)
            }
        }
    }

    fun convertToCamelCase(s: String): String {
        val m = Pattern.compile("([\\s]+)([A-Za-z0-9])").matcher(s)
        val sb = StringBuilder()
        var last = 0
        while (m.find()) {
            sb.append(s.substring(last, m.start()))
            sb.append(m.group(2).toUpperCase())
            last = m.end()
        }
        sb.append(s.substring(last))

        return sb.toString()
    }

    fun pressKeystroke(vararg keys: Int) = IDEService.type(*keys)

    fun run(rec: SurroundWithNoNullCheckRecognizer, c: String, dataContext: DataContext) =
            EventQueue.invokeLater {
                ApplicationManager.getApplication().runWriteAction {
                    rec.getActionInfo(c,
                            dataContext)
                }
            }

    fun tellJoke() {
        TTSService.say("knock, knock, knock, knock, knock")

        var result: String? = null
        while ("who is there" != result) {
            result = ASRService.waitForUtterance()
        }

        TTSService.say("Hang on, I will be right back")

        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        TTSService.say("Jah, jah, jav, jav, jav, a, a, a, va, va, va, va, va")

        while (!result!!.contains("wait who") && !result.contains("who are you")) {
            result = ASRService.waitForUtterance()
        }

        TTSService.say("It is me, Jah java va va, va, va. Open up already!")
    }

    fun fireVoiceCommand() {
        try {
            val commandTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic(COMMAND_DURATION))

            if (commandTuple == null || commandTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
                return

            // Notify of successful proceed
            beep()

            IDEService.invokeAction("Idear.VoiceAction")
        } catch (e: IOException) {
            logger.error("Panic! Failed to dump WAV", e)
        }
    }

    fun fireGoogleSearch() {
        val searchQueryTuple = webSpeechResult ?: return
        TTSService.say("I think you said " + searchQueryTuple.first + ", searching Google now")

        GoogleHelper.searchGoogle(searchQueryTuple.first)
    }

    val webSpeechResult: Pair<String, Double>?
        get() {
            var searchQueryTuple: Pair<String, Double>? = null
            beep()
            try {
                searchQueryTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic(
                        GOOGLE_QUERY_DURATION))
            } catch (e: IOException) {
                logger.error("Panic! Failed to dump WAV", e)
            }

            if (searchQueryTuple == null || searchQueryTuple.first.isEmpty())
                return null

            beep()
            return searchQueryTuple
        }

    fun pauseSpeech() {
        beep()
        var result: String
        while (ListeningState.isActive) {
            result = ASRService.waitForUtterance()
            if (result == "speech resume") {
                beep()
                break
            }
        }
    }

    fun recognizeJumpMarker(): Int {
        var result: String
        logger.info("Recognizing number...")
        while (true) {
            result = ASRService.waitForUtterance()
            if (result.startsWith("jump ")) {
                val number = WordToNumberConverter.getNumber(result.substring(5))
                logger.info("Recognized number: " + number)
                return number
            }
        }
    }

    @Synchronized
    fun beep() {
        val t = Thread {
            try {
                val clip = AudioSystem.getClip()
                val inputStream = AudioSystem.getAudioInputStream(
                        ASRService::class.java.getResourceAsStream("/org.openasr.idear/sounds/beep.wav"))
                clip.open(inputStream)
                clip.start()
            } catch (e: Exception) {
                System.err.println(e.message)
            }
        }

        t.start()

        try {
            t.join()
        } catch (ignored: InterruptedException) {
        }
    }
}