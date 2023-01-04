package org.openasr.idiolect.actions

import com.intellij.openapi.actionSystem.IdeActions.*
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.util.SystemInfo
import org.openasr.idiolect.asr.*
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.Commands
import org.openasr.idiolect.tts.TtsService
import org.openasr.idiolect.utils.*
import java.awt.event.KeyEvent.*
import java.text.SimpleDateFormat
import javax.sound.sampled.AudioSystem


object ActionRoutines {
    private val log = logger<ActionRoutines>()

    fun routineReleaseKey(c: String) {
        if ("shift" in c) IdeService.releaseShift()
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
     * @depractated - Template.psvm
     */
    @Deprecated("Template.psvm")
    fun routinePsvm() {
        IdeService.type("psvm").also { pressKeystroke(VK_TAB) }
        pressKeystroke(VK_TAB)
    }

    /** `System.out.println()`
     * @depractated - Template.sout
     */
    @Deprecated("Template.sout")
    fun routinePrintln() {
        IdeService.type("sout")
        pressKeystroke(VK_TAB)
    }

    fun routineAddNewClass(className: String) {
        IdeService.invokeAction(ACTION_NEW_ELEMENT)
        pressKeystroke(VK_ENTER)
        className.toUpperCamelCase().let {
            log.info("Class name: $it")
            IdeService.type(it)
            pressKeystroke(VK_ENTER)
        }
    }

    fun promptForVisibility(grammar: Array<String>): String? {
        return AsrService.promptForUtterance("with what visibility?", grammar)
    }

    fun promptForReturnType(): String {
        return AsrService.promptForUtterance("what will it return?")
    }

    fun promptForName(): String {
        return AsrService.promptForUtterance("what shall we call it?")
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

    fun routineHandleBreakpoint(c: String) {
        if (c.startsWith("toggle")) {
            IdeService.invokeAction(ACTION_TOGGLE_LINE_BREAKPOINT)
        } else if (c.startsWith("view")) {
            IdeService.invokeAction("ViewBreakpoints")
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

    fun routineGoto(line: String) {
        IdeService.invokeAction("GotoLine").doWhenDone {
            IdeService.type(*("" + WordToNumberConverter.getNumber(line)).toCharArray())
            IdeService.type(VK_ENTER)
        }
    }

    fun routineFocus(target: String) {
        when (target) {
            Commands.EDITOR -> pressKeystroke(VK_ESCAPE)
            Commands.PROJECT -> IdeService.invokeAction("ActivateProjectToolWindow")
            "symbols" -> {
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

    fun pressKeystroke(vararg keys: Int) = IdeService.type(*keys)

//    fun run(rec: SurroundWithNoNullCheckRecognizer, c: String, dataContext: DataContext) =
//            EventQueue.invokeLater {
//                ApplicationManager.getApplication().runWriteAction { rec.getActionInfo(c, dataContext) }
//            }


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
