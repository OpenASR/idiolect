package org.openasr.idear.actions

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.actionSystem.IdeActions.*
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.SystemInfo
import org.openasr.idear.WordToNumberConverter
import org.openasr.idear.actions.recognition.SurroundWithNoNullCheckRecognizer
import org.openasr.idear.asr.ASRService
import org.openasr.idear.asr.ListeningState
import org.openasr.idear.ide.IdeService
import org.openasr.idear.nlp.Commands
import org.openasr.idear.tts.TTSService
import java.awt.EventQueue
import java.awt.event.KeyEvent.*
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import javax.sound.sampled.AudioSystem


object ActionRoutines {
    private const val COMMAND_DURATION = 3500L
    private const val GOOGLE_QUERY_DURATION = 3000L
    private val logger = Logger.getInstance(javaClass)

    fun routineReleaseKey(c: String) {
        if ("shift" in c) IdeService.releaseShift()
    }

    fun routineFind(c: String) {
        if (c.endsWith("file")) {
            IdeService.invokeAction(ACTION_FIND)
        } else if (c.endsWith("project")) {
            IdeService.invokeAction(ACTION_FIND_IN_PATH)
        }
    }

    fun routineOfLine(c: String) {
        if (c.startsWith("beginning")) {
            if (SystemInfo.isWindows) IdeService.type(VK_HOME) else IdeService.type(VK_META, VK_LEFT)
        } else if (c.startsWith("end")) {
            if (SystemInfo.isWindows) IdeService.type(VK_END) else IdeService.type(VK_META, VK_RIGHT)
        }
    }

    /**
     * Creates a `public static void Main` function
     */
    fun routinePsvm() {
        IdeService.type("psvm").also { pressKeystroke(VK_TAB) }
        pressKeystroke(VK_TAB)
    }

    /** `System.out.println()` */
    fun routinePrintln() {
        IdeService.type("sout")
        pressKeystroke(VK_TAB)
    }

    fun routineAddNewClass(utterance: String) {
        var name = Regex(".*new class( .*)?").matchEntire(utterance)?.groups?.get(1)?.value
        if (name.isNullOrEmpty()) {
            TTSService.say("what shall we call it?")
            name = ASRService.waitForUtterance()
        }

        IdeService.invokeAction(ACTION_NEW_ELEMENT)
        pressKeystroke(VK_ENTER)
        convertToCamelCase(name).wordCapitalize().let {
            logger.info("Class name: $it")
            IdeService.type(it)
            pressKeystroke(VK_ENTER)
        }
    }

    fun String.wordCapitalize() = this[0].uppercaseChar().toString() + substring(1)

    fun routineAbout() {
        val ai = ApplicationInfo.getInstance()

        val cal = ai.buildDate
        val df = SimpleDateFormat("EEEE, MMMM dd, yyyy")

        TTSService.say("My name is " + ai.versionName +
                ", I was built on " + df.format(cal.time) +
                ", I am running version " + ai.apiVersion +
                " of the IntelliJ Platform, and I am registered to " + ai.companyName)
    }

    fun routineCheck(c: String) =
            SurroundWithNoNullCheckRecognizer().let {
                if (it.isMatching(c))
                    DataManager.getInstance().dataContextFromFocusAsync
                            .then { dataContext: DataContext -> {
                                run(it, c, dataContext)
                            }}
            }

    fun routineStep(c: String) {
        when {
            c.endsWith("over") -> IdeService.invokeAction("StepOver")
            c.endsWith("into") -> IdeService.invokeAction("StepInto")
            c.endsWith("return") -> IdeService.invokeAction("StepOut")
        }
    }

    fun routineHandleBreakpoint(c: String) {
        if (c.startsWith("toggle")) {
            IdeService.invokeAction(ACTION_TOGGLE_LINE_BREAKPOINT)
        } else if (c.startsWith("view")) {
            IdeService.invokeAction("ViewBreakpoints")
        }
    }

//    fun routineOkIdea() {
//        beep()
//        fireVoiceCommand()
//    }

    fun routineExtract(c: String) {
        if (c.endsWith("method")) {
            IdeService.invokeAction("ExtractMethod")
        } else if (c.endsWith("parameter")) {
            IdeService.invokeAction("IntroduceParameter")
        }
    }

    fun routineFollowing(c: String) {
        when {
            c.endsWith(Commands.LINE) -> IdeService.invokeAction("EditorDown")
            c.endsWith(Commands.PAGE) -> IdeService.invokeAction("EditorPageDown")
            c.endsWith(Commands.METHOD) -> IdeService.invokeAction("MethodDown")
            c.endsWith("tab") -> IdeService.invokeAction("Diff.FocusOppositePane")
            c.endsWith("word") -> IdeService.type(if (SystemInfo.isWindows) VK_CONTROL else VK_ALT, VK_RIGHT)
        }
    }

    fun routinePress(c: String) {
        when {
            Commands.DELETE in c -> pressKeystroke(VK_DELETE)
            "return" in c || "enter" in c -> pressKeystroke(VK_ENTER)
            Commands.ESCAPE in c -> pressKeystroke(VK_ESCAPE)
            Commands.TAB in c -> pressKeystroke(VK_TAB)
            Commands.UNDO in c -> IdeService.invokeAction("\$Undo")
            "shift" in c -> IdeService.pressShift()
        }
    }

    fun routineGoto(c: String) {
        IdeService.invokeAction("GotoLine").doWhenDone {
            IdeService.type(*("" + WordToNumberConverter.getNumber(c.substring(
                    10))).toCharArray())
            IdeService.type(VK_ENTER)
        }
    }

    /**
     * "open settings|recent|terminal"
     */
    fun routineOpen(c: String) {
        when {
            c.endsWith(Commands.SETTINGS) -> IdeService.invokeAction(ACTION_SHOW_SETTINGS)
            c.endsWith(Commands.RECENT) -> IdeService.invokeAction(ACTION_RECENT_FILES)
            c.endsWith(Commands.TERMINAL) -> IdeService.invokeAction("ActivateTerminalToolWindow")
        }
    }

    fun routineFocus(c: String) {
        when {
            c.endsWith(Commands.EDITOR) -> pressKeystroke(VK_ESCAPE)
            c.endsWith(Commands.PROJECT) -> IdeService.invokeAction("ActivateProjectToolWindow")
            c.endsWith("symbols") -> {
                val ar = IdeService.invokeAction("AceAction")

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

                IdeService.type(" ")
                val jumpMarker = recognizeJumpMarker()
                IdeService.type("" + jumpMarker)
                logger.info("Typed: $jumpMarker")
            }
        }
    }

    fun convertToCamelCase(s: String): String {
        val m = Pattern.compile("([\\s]+)([A-Za-z0-9])").matcher(s)
        val sb = StringBuilder()
        var last = 0
        while (m.find()) {
            sb.append(s.substring(last, m.start()))
            sb.append(m.group(2).uppercase())
            last = m.end()
        }
        sb.append(s.substring(last))

        return sb.toString()
    }

    private fun pressKeystroke(vararg keys: Int) = IdeService.type(*keys)

    fun run(rec: SurroundWithNoNullCheckRecognizer, c: String, dataContext: DataContext) =
            EventQueue.invokeLater {
                ApplicationManager.getApplication().runWriteAction { rec.getActionInfo(c, dataContext) }
            }

    fun tellJoke() {
        TTSService.say("knock, knock, knock, knock, knock")

        var result: String? = null
        while ("who is there" != result) result = ASRService.waitForUtterance()

        TTSService.say("Hang on, I will be right back")

        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        TTSService.say("Jah, jah, jav, jav, jav, a, a, a, va, va, va, va, va")

        while ("who" !in result!!) result = ASRService.waitForUtterance()

        TTSService.say("It is me, Jah java va va, va, va. Open up already!")
    }

//    fun fireVoiceCommand() {
//        try {
//            val commandTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic(COMMAND_DURATION))
//
//            if (commandTuple == null || commandTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
//                return
//
//            // Notify of successful proceed
//            beep()
//
//            ExecuteVoiceCommandAction.invoke()
//        } catch (e: IOException) {
//            logger.error("Panic! Failed to dump WAV", e)
//        }
//    }

//    fun fireGoogleSearch() {
//        val searchQueryTuple = webSpeechResult ?: return
//        TTSService.say("I think you said " + searchQueryTuple.first + ", searching Google now")
//
//        GoogleHelper.searchGoogle(searchQueryTuple.first)
//    }

//    private val webSpeechResult: Pair<String, Double>?
//        get() {
//            var searchQueryTuple: Pair<String, Double>? = null
//            beep()
//            try {
//                searchQueryTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic(GOOGLE_QUERY_DURATION))
//            } catch (e: IOException) {
//                logger.error("Panic! Failed to dump WAV", e)
//            }
//
//            if (searchQueryTuple == null || searchQueryTuple.first.isEmpty())
//                return null
//
//            beep()
//            return searchQueryTuple
//        }

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

    private fun recognizeJumpMarker(): Int {
        logger.info("Recognizing number...")
        while (true) {
            val result = ASRService.waitForUtterance()
            if (result.startsWith("jump ")) {
                val number = WordToNumberConverter.getNumber(result.substring(5))
                logger.info("Recognized number: $number")
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
                        ASRService.javaClass.getResourceAsStream("/org.openasr.idear/sounds/beep.wav"))
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
