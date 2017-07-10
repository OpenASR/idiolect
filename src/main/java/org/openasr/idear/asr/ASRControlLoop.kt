package org.openasr.idear.asr

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Pair
import com.intellij.util.Consumer
import org.openasr.idear.GoogleHelper
import org.openasr.idear.GoogleHelper.getBestTextForUtterance
import org.openasr.idear.WordToNumberConverter
import org.openasr.idear.actions.ExecuteVoiceCommandAction
import org.openasr.idear.actions.recognition.SurroundWithNoNullCheckRecognizer
import org.openasr.idear.ide.IDEService
import org.openasr.idear.ide.IDEService.invokeAction
import org.openasr.idear.recognizer.CustomMicrophone
import org.openasr.idear.tts.TTSService.say
import java.awt.EventQueue
import java.awt.event.KeyEvent.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.logging.Level
import java.util.logging.Logger
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
                logger.log(Level.INFO, "Recognized: " + result)

                applyAction(result)
            }
        }
    }

    // TODO: replace with nlp.NlpResultListener
    private fun applyAction(c: String) {
        if (c == HI_IDEA) {
            // Greet some more
            say("Hi, again!")
        } else if (c.startsWith(OPEN)) {
            if (c.endsWith(SETTINGS)) {
                invokeAction(IdeActions.ACTION_SHOW_SETTINGS)
            } else if (c.endsWith(RECENT)) {
                invokeAction(IdeActions.ACTION_RECENT_FILES)
            } else if (c.endsWith(TERMINAL)) {
                invokeAction("ActivateTerminalToolWindow")
            }
        } else if (c.startsWith(NAVIGATE)) {
            invokeAction("GotoDeclaration")
        } else if (c.startsWith(EXECUTE)) {
            invokeAction("Run")
        } else if (c == WHERE_AM_I) {
            // TODO(kudinkin): extract to action
            invokeAction("Idear.WhereAmI")
        } else if (c.startsWith("focus")) {
            if (c.endsWith(EDITOR)) {
                pressKeystroke(VK_ESCAPE)
            } else if (c.endsWith(PROJECT)) {
                invokeAction("ActivateProjectToolWindow")
            } else if (c.endsWith("symbols")) {
                val ar = invokeAction("AceJumpAction")

                while (!ar.isProcessed) {
                    //Spin lock
                    logger.info("Not done...")
                    try {
                        Thread.sleep(250)
                    } catch (e: InterruptedException) {
                        logger.warning(e.toString())
                    }
                }
                logger.info("Done!")

                IDEService.type(" ")
                val jumpMarker = recognizeJumpMarker()
                IDEService.type("" + jumpMarker)
                logger.info("Typed: " + jumpMarker)
            }
        } else if (c.startsWith(GOTO)) {
            if (c.startsWith("goto line")) {
                invokeAction("GotoLine").doWhenDone({
                    IDEService.type(*("" + WordToNumberConverter.getNumber(c.substring(10))).toCharArray())
                    IDEService.type(VK_ENTER)
                } as Consumer<DataContext>)
            }
        } else if (c.startsWith(EXPAND)) {
            //            ActionManager instance = ActionManager.getInstance();
            //            AnAction a = instance.getAction("EditorSelectWord");
            //            AnActionEvent event = new AnActionEvent(null, DataManager.getInstance().getDataContext(),
            //                    ActionPlaces.UNKNOWN, a.getTemplatePresentation(), instance, 0);
            //            a.actionPerformed(event);
            invokeAction("EditorSelectWord")
        } else if (c.startsWith(SHRINK)) {
            invokeAction("EditorUnSelectWord")
        } else if (c.startsWith("press")) {
            if (c.contains("delete")) {
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
        } else if (c.startsWith("release")) {
            if (c.contains("shift"))
                IDEService.releaseShift()
        } else if (c.startsWith("following")) {
            if (c.endsWith("line")) {
                invokeAction("EditorDown")
            } else if (c.endsWith("page")) {
                invokeAction("EditorPageDown")
            } else if (c.endsWith("method")) {
                invokeAction("MethodDown")
            } else if (c.endsWith("tab")) {
                invokeAction("Diff.FocusOppositePane")
            } else if (c.endsWith("page")) {
                invokeAction("EditorPageDown")
            } else if (c.endsWith("word")) {
                IDEService.type(VK_ALT, VK_RIGHT)
            }
        } else if (c.startsWith("previous")) {
            if (c.endsWith("line")) {
                invokeAction("EditorUp")
            } else if (c.endsWith("page")) {
                invokeAction("EditorPageUp")
            } else if (c.endsWith("method")) {
                invokeAction("MethodUp")
            } else if (c.endsWith("tab")) {
                invokeAction("Diff.FocusOppositePaneAndScroll")
            } else if (c.endsWith("page")) {
                invokeAction("EditorPageUp")
            }
        } else if (c.startsWith("extract this")) {
            if (c.endsWith("method")) {
                invokeAction("ExtractMethod")
            } else if (c.endsWith("parameter")) {
                invokeAction("IntroduceParameter")
            }
        } else if (c.startsWith("inspect code")) {
            invokeAction("CodeInspection.OnEditor")
        } else if (c.startsWith("speech pause")) {
            pauseSpeech()
        } else if (c == SHOW_USAGES) {
            invokeAction("ShowUsages")
        }
        if (c.startsWith(OK_IDEA) || c.startsWith(OKAY_IDEA)) {
            beep()
            fireVoiceCommand()
        } else if (c.startsWith(OKAY_GOOGLE) || c.startsWith(OK_GOOGLE)) {
            fireGoogleSearch()
        } else if (c.contains("break point")) {
            if (c.startsWith("toggle")) {
                invokeAction("ToggleLineBreakpoint")
            } else if (c.startsWith("view")) {
                invokeAction("ViewBreakpoints")
            }
        } else if (c.startsWith("debug")) {
            //            IDEService.invokeAction("Debug");
            IDEService.type(VK_CONTROL, VK_SHIFT, VK_F9)
        } else if (c.startsWith("step")) {
            if (c.endsWith("over")) {
                invokeAction("StepOver")
            } else if (c.endsWith("into")) {
                invokeAction("StepInto")
            } else if (c.endsWith("return")) {
                invokeAction("StepOut")
            }
        } else if (c.startsWith("resume")) {
            invokeAction("Resume")
        } else if (c.startsWith("tell me a joke")) {
            tellJoke()
        } else if (c.contains("check")) {
            val nullCheckRecognizer = SurroundWithNoNullCheckRecognizer()
            if (nullCheckRecognizer.isMatching(c)) {
                DataManager.getInstance()
                        .dataContextFromFocus
                        .doWhenDone({ dataContext: DataContext -> run(nullCheckRecognizer, c, dataContext) } as Consumer<DataContext>)
            }
        } else if (c.contains("tell me about yourself")) {
            val ai = ApplicationInfo.getInstance()

            val cal = ai.buildDate
            val df = SimpleDateFormat("EEEE, MMMM dd, yyyy")

            say("My name is " + ai.versionName + ", I was built on " + df.format(cal.time) + ", I am running version " + ai.apiVersion + " of the IntelliJ Platform, and I am registered to " + ai.companyName)
        } else if (c.contains("add new class")) {
            invokeAction("NewElement")
            pressKeystroke(VK_ENTER)
            val className = webSpeechResult
            if (className != null) {
                var camelCase = convertToCamelCase(className.first)
                logger.log(Level.INFO, "Class name: " + camelCase)
                camelCase = camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1)
                IDEService.type(camelCase)
                pressKeystroke(VK_ENTER)
            }
        } else if (c.contains("print line")) {
            IDEService.type("sout")
            pressKeystroke(VK_TAB)
        } else if (c.contains("new string")) {
            val result = webSpeechResult
            if (result != null) {
                IDEService.type(VK_SHIFT, VK_QUOTE)
                IDEService.type(result.first)
                IDEService.type(VK_SHIFT, VK_QUOTE)
            }
        } else if (c.contains("enter ")) {
            val result = webSpeechResult
            if (result != null) {
                if (c.endsWith("text")) {
                    IDEService.type(result.first)
                } else if (c.endsWith("camel case")) {
                    IDEService.type(convertToCamelCase(result.first))
                }
            }
        } else if (c.contains("public static void main")) {
            IDEService.type("psvm")
            pressKeystroke(VK_TAB)
        } else if (c.endsWith("of line")) {
            if (c.startsWith("beginning")) {
                IDEService.type(VK_META, VK_LEFT)
            } else if (c.startsWith("end")) {
                IDEService.type(VK_META, VK_RIGHT)
            }
        } else if (c.startsWith("find in")) {
            if (c.endsWith("file")) {
                invokeAction("Find")
            } else if (c.endsWith("project")) {
                invokeAction("FindInPath")
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

    private fun run(rec: SurroundWithNoNullCheckRecognizer, c: String, dataContext: DataContext) {
        EventQueue.invokeLater { ApplicationManager.getApplication().runWriteAction { rec.getActionInfo(c, dataContext) } }
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
            val commandTuple = getBestTextForUtterance(CustomMicrophone.recordFromMic(COMMAND_DURATION))

            if (commandTuple == null || commandTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
                return

            // Notify of successful proceed
            beep()

            invokeAction(
                    "Idear.VoiceAction"
            ) { dataContext ->
                AnActionEvent(null,
                        SimpleDataContext.getSimpleContext(ExecuteVoiceCommandAction.KEY.name, commandTuple.first, dataContext),
                        ActionPlaces.UNKNOWN,
                        Presentation(),
                        ActionManager.getInstance(),
                        0
                )
            }
        } catch (e: IOException) {
            logger.log(Level.SEVERE, "Panic! Failed to dump WAV", e)
        }

    }

    private fun fireGoogleSearch() {
        val searchQueryTuple = webSpeechResult ?: return
        say("I think you said " + searchQueryTuple.first + ", searching Google now")

        GoogleHelper.searchGoogle(searchQueryTuple.first)
    }

    private /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */ val webSpeechResult: Pair<String, Double>?
        get() {
            var searchQueryTuple: Pair<String, Double>? = null
            beep()
            try {
                searchQueryTuple = GoogleHelper.getBestTextForUtterance(CustomMicrophone.recordFromMic(GOOGLE_QUERY_DURATION))
            } catch (e: IOException) {
                logger.log(Level.SEVERE, "Panic! Failed to dump WAV", e)
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
        private val logger = Logger.getLogger(ASRControlLoop::class.java.simpleName)

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
