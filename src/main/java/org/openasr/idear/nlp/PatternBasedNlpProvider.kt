package org.openasr.idear.nlp

import org.openasr.idear.actions.Routines
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
            c.startsWith(Commands.OPEN) -> Routines.routineOpen(c)
            c.startsWith(Commands.NAVIGATE) -> IDEService.invokeAction("GotoDeclaration")
            c.startsWith(Commands.EXECUTE) -> IDEService.invokeAction("Run")
            c == Commands.WHERE_AM_I -> IDEService.invokeAction("Idear.WhereAmI")
            c.startsWith(Commands.FOCUS) -> Routines.routineFocus(c)
            c.startsWith(Commands.GOTO) -> Routines.routineGoto(c)
            c.startsWith(Commands.EXPAND) -> IDEService.invokeAction("EditorSelectWord")
            c.startsWith(Commands.SHRINK) -> IDEService.invokeAction("EditorUnSelectWord")
            c.startsWith(Commands.PRESS) -> Routines.routinePress(c)
            c.startsWith("release") -> Routines.routineReleaseKey(c)
            c.startsWith("following") -> Routines.routineFollowing(c)
            c.startsWith("extract this") -> Routines.routineExtract(c)
            c.startsWith("inspect code") -> IDEService.invokeAction("CodeInspection.OnEditor")
            c.startsWith("speech pause") -> Routines.pauseSpeech()
            c == Commands.SHOW_USAGES -> IDEService.invokeAction("ShowUsages")
            c.startsWith(Commands.OK_IDEA) -> Routines.routineOkIdea()
            c.startsWith(Commands.OKAY_GOOGLE) -> Routines.fireGoogleSearch()
            c.contains("break point") -> Routines.routineHandleBreakpoint(c)
            c.startsWith(Commands.DEBUG) -> IDEService.type(KeyEvent.VK_CONTROL, KeyEvent.VK_SHIFT, KeyEvent.VK_F9)
            c.startsWith("step") -> Routines.routineStep(c)
            c.startsWith("resume") -> IDEService.invokeAction("Resume")
            c.startsWith("tell me a joke") -> Routines.tellJoke()
            c.contains("check") -> Routines.routineCheck(c)
            c.contains("tell me about yourself") -> Routines.routineAbout()
            c.contains("add new class") -> Routines.routineAddNewClass()
            c.contains("print line") -> Routines.routinePrintln()
            c.contains("new string") -> Routines.routineNewString()
            c.contains("enter ") -> Routines.routineEnter(c)
            c.contains("public static void main") -> Routines.routinePsvm()
            c.endsWith("of line") -> Routines.routineOfLine(c)
            c.startsWith("find in") -> Routines.routineFind(c)
            else -> {
            }
        }
    }
}