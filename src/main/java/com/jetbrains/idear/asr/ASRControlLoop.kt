package com.jetbrains.idear.asr

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.util.Pair
import com.intellij.util.Consumer
import com.jetbrains.idear.GoogleHelper
import com.jetbrains.idear.GoogleHelper.getBestTextForUtterance
import com.jetbrains.idear.WordToNumberConverter
import com.jetbrains.idear.actions.ExecuteVoiceCommandAction
import com.jetbrains.idear.actions.recognition.SurroundWithNoNullCheckRecognizer
import com.jetbrains.idear.ide.IDEService
import com.jetbrains.idear.recognizer.CustomLiveSpeechRecognizer
import com.jetbrains.idear.recognizer.CustomMicrophone
import com.jetbrains.idear.tts.TTSService
import java.awt.EventQueue
import java.awt.event.KeyEvent.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.logging.Level
import java.util.logging.Logger
import java.util.regex.Pattern
import javax.sound.sampled.AudioSystem

class ASRControlLoop(private val recognizer: CustomLiveSpeechRecognizer) : Runnable {
    override fun run() {
        while (!ListeningState.isTerminated) {
            // This blocks on a recognition result
            val result = resultFromRecognizer

            if (ListeningState.isInit) {
                if (result == HI_IDEA) {
                    // Greet invoker
                    say("Hi")
                    IDEService.invokeAction("Idear.Start")
                }
            } else if (ListeningState.isActive) {
                logger.log(Level.INFO, "Recognized: " + result)

                applyAction(result)
            }
        }
    }

    private val resultFromRecognizer: String
        get() {
            val result = recognizer.result

            println("Recognized: ")
            println("\tTop H:       " + result.result + " / " + result.result.bestToken + " / " + result.result.bestPronunciationResult)
            println("\tTop 3H:      " + result.getNbest(3))

            logger.info("Recognized:    ")
            logger.info("\tTop H:       " + result.result + " / " + result.result.bestToken + " / " + result.result.bestPronunciationResult)
            logger.info("\tTop 3H:      " + result.getNbest(3))

            return result.hypothesis
        }

    private fun applyAction(c: String) {

        if (c == HI_IDEA) {
            // Greet some more
            say("Hi, again!")
        } else if (c.startsWith(OPEN)) {
            if (c.endsWith(SETTINGS)) {
                IDEService.invokeAction(IdeActions.ACTION_SHOW_SETTINGS)
            } else if (c.endsWith(RECENT)) {
                IDEService.invokeAction(IdeActions.ACTION_RECENT_FILES)
            } else if (c.endsWith(TERMINAL)) {
                IDEService.invokeAction("ActivateTerminalToolWindow")
            }
        } else if (c.startsWith(NAVIGATE)) {
            IDEService.invokeAction("GotoDeclaration")
        } else if (c.startsWith(EXECUTE)) {
            IDEService.invokeAction("Run")
        } else if (c == WHERE_AM_I) {
            // TODO(kudinkin): extract to action
            IDEService.invokeAction("Idear.WhereAmI")
        } else if (c.startsWith("focus")) {
            if (c.endsWith(EDITOR)) {
                pressKeystroke(VK_ESCAPE)
            } else if (c.endsWith(PROJECT)) {
                IDEService.invokeAction("ActivateProjectToolWindow")
            } else if (c.endsWith("symbols")) {
                val ar = IDEService.invokeAction("AceJumpAction")

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
                IDEService.invokeAction("GotoLine").doWhenDone({ dataContext: DataContext ->
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
            IDEService.invokeAction("EditorSelectWord")
        } else if (c.startsWith(SHRINK)) {
            IDEService.invokeAction("EditorUnSelectWord")
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
                IDEService.invokeAction("\$Undo")
            } else if (c.contains("shift")) {
                IDEService.pressShift()
            }
        } else if (c.startsWith("release")) {
            if (c.contains("shift"))
                IDEService.releaseShift()
        } else if (c.startsWith("following")) {
            if (c.endsWith("line")) {
                IDEService.invokeAction("EditorDown")
            } else if (c.endsWith("page")) {
                IDEService.invokeAction("EditorPageDown")
            } else if (c.endsWith("method")) {
                IDEService.invokeAction("MethodDown")
            } else if (c.endsWith("tab")) {
                IDEService.invokeAction("Diff.FocusOppositePane")
            } else if (c.endsWith("page")) {
                IDEService.invokeAction("EditorPageDown")
            } else if (c.endsWith("word")) {
                IDEService.type(VK_ALT, VK_RIGHT)
            }
        } else if (c.startsWith("previous")) {
            if (c.endsWith("line")) {
                IDEService.invokeAction("EditorUp")
            } else if (c.endsWith("page")) {
                IDEService.invokeAction("EditorPageUp")
            } else if (c.endsWith("method")) {
                IDEService.invokeAction("MethodUp")
            } else if (c.endsWith("tab")) {
                IDEService.invokeAction("Diff.FocusOppositePaneAndScroll")
            } else if (c.endsWith("page")) {
                IDEService.invokeAction("EditorPageUp")
            }
        } else if (c.startsWith("extract this")) {
            if (c.endsWith("method")) {
                IDEService.invokeAction("ExtractMethod")
            } else if (c.endsWith("parameter")) {
                IDEService.invokeAction("IntroduceParameter")
            }
        } else if (c.startsWith("inspect code")) {
            IDEService.invokeAction("CodeInspection.OnEditor")
        } else if (c.startsWith("speech pause")) {
            pauseSpeech()
        } else if (c == SHOW_USAGES) {
            IDEService.invokeAction("ShowUsages")
        }
        if (c.startsWith(OK_IDEA) || c.startsWith(OKAY_IDEA)) {
            beep()
            fireVoiceCommand()
        } else if (c.startsWith(OKAY_GOOGLE) || c.startsWith(OK_GOOGLE)) {
            fireGoogleSearch()
        } else if (c.contains("break point")) {
            if (c.startsWith("toggle")) {
                IDEService.invokeAction("ToggleLineBreakpoint")
            } else if (c.startsWith("view")) {
                IDEService.invokeAction("ViewBreakpoints")
            }
        } else if (c.startsWith("debug")) {
            //            IDEService.invokeAction("Debug");
            IDEService.type(VK_CONTROL, VK_SHIFT, VK_F9)
        } else if (c.startsWith("step")) {
            if (c.endsWith("over")) {
                IDEService.invokeAction("StepOver")
            } else if (c.endsWith("into")) {
                IDEService.invokeAction("StepInto")
            } else if (c.endsWith("return")) {
                IDEService.invokeAction("StepOut")
            }
        } else if (c.startsWith("resume")) {
            IDEService.invokeAction("Resume")
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
            IDEService.invokeAction("NewElement")
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
                IDEService.invokeAction("Find")
            } else if (c.endsWith("project")) {
                IDEService.invokeAction("FindInPath")
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
        TTSService.say("knock, knock, knock, knock, knock")

        var result: String? = null
        while ("who is there" != result) {
            result = resultFromRecognizer
        }

        TTSService.say("Hang on, I will be right back")

        try {
            Thread.sleep(3000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        TTSService.say("Jah, jah, jav, jav, jav, a, a, a, va, va, va, va, va")

        while (!result!!.contains("wait who") && !result.contains("who are you")) {
            result = resultFromRecognizer
        }

        TTSService.say("It is me, Jah java va va, va, va. Open up already!")
    }

    private fun fireVoiceCommand() {
        try {
            val commandTuple = getBestTextForUtterance(CustomMicrophone.recordFromMic(COMMAND_DURATION))

            if (commandTuple == null || commandTuple.first.isEmpty() /* || searchQuery.second < CONFIDENCE_LEVEL_THRESHOLD */)
                return

            // Notify of successful proceed
            beep()

            IDEService.invokeAction(
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

        ServiceManager
                .getService(TTSService::class.java)
                .say("I think you said " + searchQueryTuple.first + ", searching Google now")

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
            result = resultFromRecognizer
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
            result = resultFromRecognizer
            if (result.startsWith("jump ")) {
                val number = WordToNumberConverter.getNumber(result.substring(5))
                logger.info("Recognized number: " + number)
                return number
            }
        }
    }

    // Helpers

    @Synchronized fun say(something: String) {
        TTSService
                .say(splitCamelCase(something))
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
                            ASRService::class.java.getResourceAsStream("/com.jetbrains.idear/sounds/beep.wav"))
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
