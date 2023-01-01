package org.openasr.idiolect.actions

import com.intellij.openapi.actionSystem.IdeActions.*
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.SystemInfo
import org.openasr.idiolect.utils.WordToNumberConverter
import org.openasr.idiolect.asr.AsrService
import org.openasr.idiolect.asr.ListeningState
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.Commands
import org.openasr.idiolect.tts.TtsService
import org.openasr.idiolect.utils.toUpperCamelCase
import java.awt.event.KeyEvent.*
import java.text.SimpleDateFormat
import javax.sound.sampled.AudioSystem


object ActionRoutines {
    private val log = logger<ActionRoutines>()

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

    fun routineAddNewClass(name: String) {
        val className: String = name.ifEmpty {
            TtsService.say("what shall we call it?")
            AsrService.waitForUtterance()
        }

        IdeService.invokeAction(ACTION_NEW_ELEMENT)
        pressKeystroke(VK_ENTER)
        className.toUpperCamelCase().let {
            log.info("Class name: $it")
            IdeService.type(it)
            pressKeystroke(VK_ENTER)
        }
    }

    fun promptForVisibility(grammar: Array<String>): String? {
        TtsService.say("with what visibility?")
        return AsrService.waitForUtterance(grammar)
    }

    fun promptForReturnType(): String {
        TtsService.say("what will it return?")
        return AsrService.waitForUtterance()
    }

    fun promptForName(): String {
        TtsService.say("what shall we call it?")
        return AsrService.waitForUtterance()
    }

    fun routineAbout() {
        val ai = ApplicationInfo.getInstance()

        val cal = ai.buildDate
        val df = SimpleDateFormat("EEEE, MMMM dd, yyyy")

        TtsService.say("My name is " + ai.versionName +
                ", I was built on " + df.format(cal.time) +
                ", I am running version " + ai.apiVersion +
                " of the IntelliJ Platform, and I am registered to " + ai.companyName)
    }

//    fun routineCheck(c: String) =
//            SurroundWithNoNullCheckRecognizer().let {
//                if (it.isMatching(c))
//                    DataManager.getInstance().dataContextFromFocusAsync
//                            .then { dataContext: DataContext -> {
//                                run(it, c, dataContext)
//                            }}
//            }

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

    fun routineExtract(c: String) {
        if (c.endsWith("method")) {
            IdeService.invokeAction("ExtractMethod")
        } else if (c.endsWith("parameter")) {
            IdeService.invokeAction("IntroduceParameter")
        }
    }

    fun routineFollowing(c: String) {
        when {
            c.endsWith(Commands.LINE) -> IdeService.invokeAction(ACTION_EDITOR_MOVE_CARET_DOWN)
            c.endsWith(Commands.PAGE) -> IdeService.invokeAction(ACTION_EDITOR_MOVE_CARET_PAGE_DOWN)
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
            Commands.UNDO in c -> IdeService.invokeAction(ACTION_UNDO)
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
                    log.info("Not done...")
                    try {
                        Thread.sleep(250)
                    } catch (e: InterruptedException) {
                        log.warn(e)
                    }
                }
                log.info("Done!")

                IdeService.type(" ")
                val jumpMarker = recognizeJumpMarker()
                IdeService.type("" + jumpMarker)
                log.info("Typed: $jumpMarker")
            }
        }
    }

    private fun pressKeystroke(vararg keys: Int) = IdeService.type(*keys)

//    fun run(rec: SurroundWithNoNullCheckRecognizer, c: String, dataContext: DataContext) =
//            EventQueue.invokeLater {
//                ApplicationManager.getApplication().runWriteAction { rec.getActionInfo(c, dataContext) }
//            }

    fun tellJoke() {
        TtsService.say("knock, knock, knock, knock, knock")

        var result: String? = null
        while ("who is there" != result) result = AsrService.waitForUtterance()

        TtsService.say("Hang on, I will be right back")

        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        TtsService.say("Jah, jah, jav, jav, jav, a, a, a, va, va, va, va, va")

        while ("who" !in result!!) result = AsrService.waitForUtterance()

        TtsService.say("It is me, Jah java va va, va, va. Open up already!")
    }

    fun pauseSpeech() {
        beep()
        while (ListeningState.isActive) {
            val result = AsrService.waitForUtterance(arrayOf("resume", "listening"))

            if (result == "resume listening") {
                beep()
                break
            }
        }
    }

    private fun recognizeJumpMarker(): Int {
        log.info("Recognizing number...")
        while (true) {
            val result = AsrService.waitForUtterance()
            if (result.startsWith("jump ")) {
                val number = WordToNumberConverter.getNumber(result.substring(5))
                log.info("Recognized number: $number")
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
                        AsrService.javaClass.getResourceAsStream("/org/openasr/idiolect/sounds/beep.wav"))
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
