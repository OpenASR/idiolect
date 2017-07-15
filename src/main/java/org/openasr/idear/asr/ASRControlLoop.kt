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
import org.openasr.idear.actions.recognition.SurroundWithNoNullCheckRecognizer
import org.openasr.idear.ide.IDEService
import org.openasr.idear.ide.IDEService.invokeAction
import org.openasr.idear.recognizer.CustomMicrophone.Companion.recordFromMic
import org.openasr.idear.tts.TTSService.say
import java.awt.EventQueue
import java.awt.event.KeyEvent.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.regex.Pattern
import javax.sound.sampled.AudioSystem

class ASRControlLoop(private val asrProvider: ASRProvider) : Runnable {
    override fun run() {
        while (!ListeningState.isTerminated) {
            // This blocks on a recognition result
            val result = asrProvider.waitForUtterance()

            //TODO: nlpService.processUtterance(result, getContext())

            if (ListeningState.isInit) {
                if (result == HI_IDEA) {
                    // Greet invoker
                    say("Hi")
                    invokeAction("Idear.Start")
                }
            } else if (ListeningState.isActive) {
                logger.info("Recognized: $result")

                applyAction(result)
            }
        }
    }

    // TODO: replace with nlp.NlpResultListener
    private fun applyAction(c: String): Any =
        when {
            c == HI_IDEA -> say("Hi, again!")
            c.startsWith(OPEN) -> routineOpen(c)
            c.startsWith(NAVIGATE) -> invokeAction("GotoDeclaration")
            c.startsWith(EXECUTE) -> invokeAction("Run")
            c == WHERE_AM_I -> invokeAction("Idear.WhereAmI")
            c.startsWith(FOCUS) -> routineFocus(c)
            c.startsWith(GOTO) -> routineGoto(c)
            c.startsWith(EXPAND) -> invokeAction("EditorSelectWord")
            c.startsWith(SHRINK) -> invokeAction("EditorUnSelectWord")
            c.startsWith(PRESS) -> routinePress(c)
            c.startsWith("release") -> routineRelelaseKey(c)
            c.startsWith("following") -> routineFollowing(c)
            c.startsWith("extract this") -> routineExtract(c)
            c.startsWith("inspect code") -> invokeAction("CodeInspection.OnEditor")
            c.startsWith("speech pause") -> pauseSpeech()
            c == SHOW_USAGES -> invokeAction("ShowUsages")
            c.startsWith(OK_IDEA) -> routineOkIdea()
            c.startsWith(OKAY_GOOGLE) -> fireGoogleSearch()
            c.contains("break point") -> routineHandleBreakpoint(c)
            c.startsWith(DEBUG) -> IDEService.type(VK_CONTROL, VK_SHIFT, VK_F9)
            c.startsWith("step") -> routineStep(c)
            c.startsWith("resume") -> invokeAction("Resume")
            c.startsWith("tell me a joke") -> tellJoke()
            c.contains("check") -> routineCheck(c)
            c.contains("tell me about yourself") -> routineAbout()
            c.contains("add new class") -> routineAddNewClass()
            c.contains("print line") -> routinePrintln()
            c.contains("new string") -> routineNewString()
            c.contains("enter ") -> routineEnter(c)
            c.contains("public static void main") -> routinePsvm()
            c.endsWith("of line") -> routineOfLine(c)
            c.startsWith("find in") -> routineFind(c)
            else -> {
            }
        }

    private fun routineRelelaseKey(c: String) {
        if (c.contains("shift")) IDEService.releaseShift()
    }

    private fun routineFind(c: String) {
        if (c.endsWith("file")) {
            invokeAction("Find")
        } else if (c.endsWith("project")) {
            invokeAction("FindInPath")
        }
    }

    private fun routineOfLine(c: String) {
        if (c.startsWith("beginning")) {
            IDEService.type(VK_META, VK_LEFT)
        } else if (c.startsWith("end")) {
            IDEService.type(VK_META, VK_RIGHT)
        }
    }

    private fun routinePsvm() {
        IDEService.type("psvm")
        pressKeystroke(VK_TAB)
    }

    private fun routineEnter(c: String) {
        val result = webSpeechResult
        if (result != null) {
            if (c.endsWith("text")) {
                IDEService.type(result.first)
            } else if (c.endsWith("camel case")) {
                IDEService.type(convertToCamelCase(result.first))
            }
        }
    }

    private fun routineNewString() {
        val result = webSpeechResult
        if (result != null) {
            IDEService.type(VK_SHIFT, VK_QUOTE)
            IDEService.type(result.first)
            IDEService.type(VK_SHIFT, VK_QUOTE)
        }
    }

    private fun routinePrintln() {
        IDEService.type("sout")
        pressKeystroke(VK_TAB)
    }

    private fun routineAddNewClass() {
        invokeAction("NewElement")
        pressKeystroke(VK_ENTER)
        val className = webSpeechResult
        if (className != null) {
            var camelCase = convertToCamelCase(className.first)
            logger.info("Class name: $camelCase")
            camelCase = camelCase.substring(0,
                1).toUpperCase() + camelCase.substring(1)
            IDEService.type(camelCase)
            pressKeystroke(VK_ENTER)
        }
    }

    private fun routineAbout() {
        val ai = ApplicationInfo.getInstance()

        val cal = ai.buildDate
        val df = SimpleDateFormat("EEEE, MMMM dd, yyyy")

        say("My name is " + ai.versionName + ", I was built on " + df.format(cal.time) + ", I am running version " + ai.apiVersion + " of the IntelliJ Platform, and I am registered to " + ai.companyName)
    }

    private fun routineCheck(c: String) {
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

    private fun routineStep(c: String) {
        if (c.endsWith("over")) {
            invokeAction("StepOver")
        } else if (c.endsWith("into")) {
            invokeAction("StepInto")
        } else if (c.endsWith("return")) {
            invokeAction("StepOut")
        }
    }

    private fun routineHandleBreakpoint(c: String) {
        if (c.startsWith("toggle")) {
            invokeAction("ToggleLineBreakpoint")
        } else if (c.startsWith("view")) {
            invokeAction("ViewBreakpoints")
        }
    }

    private fun routineOkIdea() {
        beep()
        fireVoiceCommand()
    }

    private fun routineExtract(c: String) {
        if (c.endsWith("method")) {
            invokeAction("ExtractMethod")
        } else if (c.endsWith("parameter")) {
            invokeAction("IntroduceParameter")
        }
    }

    private fun routineFollowing(c: String) {
        if (c.endsWith(LINE)) {
            invokeAction("EditorDown")
        } else if (c.endsWith(PAGE)) {
            invokeAction("EditorPageDown")
        } else if (c.endsWith(METHOD)) {
            invokeAction("MethodDown")
        } else if (c.endsWith("tab")) {
            invokeAction("Diff.FocusOppositePane")
        } else if (c.endsWith("page")) {
            invokeAction("EditorPageDown")
        } else if (c.endsWith("word")) {
            IDEService.type(VK_ALT, VK_RIGHT)
        }
    }

    private fun routinePress(c: String) {
        if (c.contains(DELETE)) {
            pressKeystroke(VK_DELETE)
        } else if (c.contains("return") || c.contains("enter")) {
            pressKeystroke(VK_ENTER)
        } else if (c.contains(ESCAPE)) {
            pressKeystroke(VK_ESCAPE)
        } else if (c.contains(TAB)) {
            pressKeystroke(VK_TAB)
        } else if (c.contains(UNDO)) {
            invokeAction("\$Undo")
        } else if (c.contains("shift")) {
            IDEService.pressShift()
        }
    }

    private fun routineGoto(c: String) {
        invokeAction("GotoLine").doWhenDone({
            IDEService.type(*("" + WordToNumberConverter.getNumber(c.substring(
                10))).toCharArray())
            IDEService.type(VK_ENTER)
        })
    }

    private fun routineOpen(c: String) {
        when {
            c.endsWith(SETTINGS) -> invokeAction(ACTION_SHOW_SETTINGS)
            c.endsWith(RECENT) -> invokeAction(ACTION_RECENT_FILES)
            c.endsWith(TERMINAL) -> invokeAction("ActivateTerminalToolWindow")
        }
    }

    private fun routineFocus(c: String) {
        when {
            c.endsWith(EDITOR) -> pressKeystroke(VK_ESCAPE)
            c.endsWith(PROJECT) -> invokeAction("ActivateProjectToolWindow")
            c.endsWith("symbols") -> {
                val ar = invokeAction("AceJumpAction")

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

    private fun convertToCamelCase(s: String): String {
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

    private fun pressKeystroke(vararg keys: Int) {
        IDEService.type(*keys)
    }

    private fun run(rec: SurroundWithNoNullCheckRecognizer,
                    c: String,
                    dataContext: DataContext) {
        EventQueue.invokeLater {
            ApplicationManager.getApplication().runWriteAction {
                rec.getActionInfo(c,
                    dataContext)
            }
        }
    }

    private fun tellJoke() {
        say("knock, knock, knock, knock, knock")

        var result: String? = null
        while ("who is there" != result) {
            result = asrProvider.waitForUtterance()
        }

        say("Hang on, I will be right back")

        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        say("Jah, jah, jav, jav, jav, a, a, a, va, va, va, va, va")

        while (!result!!.contains("wait who") && !result.contains("who are you")) {
            result = asrProvider.waitForUtterance()
        }

        say("It is me, Jah java va va, va, va. Open up already!")
    }

    private fun fireVoiceCommand() {
        try {
            val commandTuple = getBestTextForUtterance(recordFromMic(
                COMMAND_DURATION))

            if (commandTuple == null || commandTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
                return

            // Notify of successful proceed
            beep()

            invokeAction("Idear.VoiceAction")
        } catch (e: IOException) {
            logger.error("Panic! Failed to dump WAV", e)
        }
    }

    private fun fireGoogleSearch() {
        val searchQueryTuple = webSpeechResult ?: return
        say("I think you said " + searchQueryTuple.first + ", searching Google now")

        GoogleHelper.searchGoogle(searchQueryTuple.first)
    }

    private val webSpeechResult: Pair<String, Double>?
        get() {
            var searchQueryTuple: Pair<String, Double>? = null
            beep()
            try {
                searchQueryTuple = getBestTextForUtterance(recordFromMic(
                    GOOGLE_QUERY_DURATION))
            } catch (e: IOException) {
                logger.error("Panic! Failed to dump WAV", e)
            }

            if (searchQueryTuple == null || searchQueryTuple.first.isEmpty())
                return null

            beep()
            return searchQueryTuple
        }

    private fun pauseSpeech() {
        beep()
        var result: String
        while (ListeningState.isActive) {
            result = asrProvider.waitForUtterance()
            if (result == "speech resume") {
                beep()
                break
            }
        }
    }

    private fun recognizeJumpMarker(): Int {
        var result: String
        logger.info("Recognizing number...")
        while (true) {
            result = asrProvider.waitForUtterance()
            if (result.startsWith("jump ")) {
                val number = WordToNumberConverter.getNumber(result.substring(5))
                logger.info("Recognized number: " + number)
                return number
            }
        }
    }

    companion object {
        private val logger = Logger.getInstance(ASRControlLoop::class.java)

        private val OPEN = "open"
        private val SETTINGS = "settings"
        private val RECENT = "recent"
        private val TERMINAL = "terminal"
        private val FOCUS = "focus"
        private val EDITOR = "editor"
        private val PROJECT = "project"
        private val SELECTION = "selection"
        private val EXPAND = "grow"
        private val SHRINK = "shrink"
        private val PRESS = "press"
        private val DELETE = "delete"
        private val DEBUG = "debug"
        private val ENTER = "enter"
        private val ESCAPE = "escape"
        private val TAB = "tab"
        private val UNDO = "undo"
        private val NEXT = "next"
        private val LINE = "line"
        private val PAGE = "page"
        private val METHOD = "method"
        private val PREVIOUS = "previous"
        private val INSPECT_CODE = "inspect code"
        private val OKAY_GOOGLE = "okay google"
        private val OK_GOOGLE = "ok google"
        private val OKAY_IDEA = "okay idea"
        private val OK_IDEA = "ok idea"
        private val HI_IDEA = "hi idea"
        private val WHERE_AM_I = "where am i"
        private val NAVIGATE = "navigate"
        private val EXECUTE = "execute"
        private val GOTO = "goto line"
        private val SHOW_USAGES = "show usages"

        private val COMMAND_DURATION: Long = 3500

        private val GOOGLE_QUERY_DURATION: Long = 3000

        @Synchronized fun beep() {
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

        private fun splitCamelCase(s: String): String {
            return s.replace(String.format("%s|%s|%s",
                "(?<=[A-Z])(?=[A-Z][a-z])",
                "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"
            ).toRegex(), " ")
        }
    }
}
