package org.openasr.idear.nlp

import org.openasr.idear.actions.ActionRoutines
import org.openasr.idear.ide.IDEService
import org.openasr.idear.tts.TTSService
import java.awt.event.KeyEvent

class PatternBasedNlpProvider : NlpProvider {
    /**
     * @param c - the command as spoken
     */
    override fun processUtterance(c: String) {
        when {
            c == Commands.HI_IDEA -> TTSService.say("Hi, again!")
            c.startsWith(Commands.OPEN) -> ActionRoutines.routineOpen(c)
            c.startsWith(Commands.NAVIGATE) -> IDEService.invokeAction("GotoDeclaration")
            c.startsWith(Commands.EXECUTE) -> IDEService.invokeAction("Run")
            c == Commands.WHERE_AM_I -> IDEService.invokeAction("Idear.WhereAmI")
            c.startsWith(Commands.FOCUS) -> ActionRoutines.routineFocus(c)
            c.startsWith(Commands.GOTO) -> ActionRoutines.routineGoto(c)
            c.startsWith(Commands.EXPAND) -> IDEService.invokeAction("EditorSelectWord")
            c.startsWith(Commands.SHRINK) -> IDEService.invokeAction("EditorUnSelectWord")
            c.startsWith(Commands.PRESS) -> ActionRoutines.routinePress(c)
            c.startsWith("release") -> ActionRoutines.routineReleaseKey(c)
            c.startsWith("following") -> ActionRoutines.routineFollowing(c)
            c.startsWith("extract this") -> ActionRoutines.routineExtract(c)
            c.startsWith("inspect code") -> IDEService.invokeAction("CodeInspection.OnEditor")
            c.startsWith("speech pause") -> ActionRoutines.pauseSpeech()
            c == Commands.SHOW_USAGES -> IDEService.invokeAction("ShowUsages")
            c.startsWith(Commands.OK_IDEA) -> ActionRoutines.routineOkIdea()
            c.startsWith(Commands.OKAY_GOOGLE) -> ActionRoutines.fireGoogleSearch()
            c.contains("break point") -> ActionRoutines.routineHandleBreakpoint(c)
            c.startsWith(Commands.DEBUG) -> IDEService.type(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_F9)
            c.startsWith("step") -> ActionRoutines.routineStep(c)
            c.startsWith("resume") -> IDEService.invokeAction("Resume")
            c.startsWith("tell me a joke") -> ActionRoutines.tellJoke()
            c.contains("check") -> ActionRoutines.routineCheck(c)
            c.contains("tell me about yourself") -> ActionRoutines.routineAbout()
            c.contains("add new class") -> ActionRoutines.routineAddNewClass()
            c.contains("print line") -> ActionRoutines.routinePrintln()
            c.contains("new string") -> ActionRoutines.routineNewString()
            c.contains("enter ") -> ActionRoutines.routineEnter(c)
            c.contains("public static void main") -> ActionRoutines.routinePsvm()
            c.endsWith("of line") -> ActionRoutines.routineOfLine(c)
            c.startsWith("find in") -> ActionRoutines.routineFind(c)
            else -> {
            }
        }
    }
}