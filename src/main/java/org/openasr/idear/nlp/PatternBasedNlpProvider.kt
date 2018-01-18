package org.openasr.idear.nlp

import com.intellij.openapi.actionSystem.IdeActions.*
import org.openasr.idear.actions.ActionRoutines.pauseSpeech
import org.openasr.idear.actions.ActionRoutines.routineAbout
import org.openasr.idear.actions.ActionRoutines.routineCheck
import org.openasr.idear.actions.ActionRoutines.routineExtract
import org.openasr.idear.actions.ActionRoutines.routineFind
import org.openasr.idear.actions.ActionRoutines.routineFocus
import org.openasr.idear.actions.ActionRoutines.routineFollowing
import org.openasr.idear.actions.ActionRoutines.routineGoto
import org.openasr.idear.actions.ActionRoutines.routineHandleBreakpoint
import org.openasr.idear.actions.ActionRoutines.routineOfLine
import org.openasr.idear.actions.ActionRoutines.routineOpen
import org.openasr.idear.actions.ActionRoutines.routinePress
import org.openasr.idear.actions.ActionRoutines.routinePrintln
import org.openasr.idear.actions.ActionRoutines.routinePsvm
import org.openasr.idear.actions.ActionRoutines.routineReleaseKey
import org.openasr.idear.actions.ActionRoutines.routineStep
import org.openasr.idear.actions.ActionRoutines.tellJoke
import org.openasr.idear.actions.WhereAmIAction
import org.openasr.idear.ide.IDEService
import org.openasr.idear.ide.IDEService.invokeAction
import org.openasr.idear.nlp.Commands.DEBUG
import org.openasr.idear.nlp.Commands.EXECUTE
import org.openasr.idear.nlp.Commands.EXPAND
import org.openasr.idear.nlp.Commands.FOCUS
import org.openasr.idear.nlp.Commands.GOTO
import org.openasr.idear.nlp.Commands.HI_IDEA
import org.openasr.idear.nlp.Commands.NAVIGATE
import org.openasr.idear.nlp.Commands.OKAY_GOOGLE
import org.openasr.idear.nlp.Commands.OKAY_IDEA
import org.openasr.idear.nlp.Commands.OPEN
import org.openasr.idear.nlp.Commands.PRESS
import org.openasr.idear.nlp.Commands.SHOW_USAGES
import org.openasr.idear.nlp.Commands.SHRINK
import org.openasr.idear.nlp.Commands.WHERE_AM_I
import org.openasr.idear.tts.TTSService
import java.awt.event.KeyEvent.*

class PatternBasedNlpProvider : NlpProvider {
    /**
     * @param u - the command as spoken
     */
    override fun processUtterance(u: String) {
        when {
            u == HI_IDEA -> TTSService.say("Hi, again!")
            u.startsWith(OPEN) -> routineOpen(u)
            u.startsWith(NAVIGATE) -> invokeAction("GotoDeclaration")
            u.startsWith(EXECUTE) -> invokeAction("Run")
            u == WHERE_AM_I -> WhereAmIAction.invoke()
            u.startsWith(FOCUS) -> routineFocus(u)
            u.startsWith(GOTO) -> routineGoto(u)
            u.startsWith(EXPAND) -> invokeAction(ACTION_EDITOR_SELECT_WORD_AT_CARET)
            u.startsWith(SHRINK) -> invokeAction(ACTION_EDITOR_UNSELECT_WORD_AT_CARET)
            u.startsWith(PRESS) -> routinePress(u)
            u.startsWith("release") -> routineReleaseKey(u)
            u.startsWith("following") -> routineFollowing(u)
            u.startsWith("extract this") -> routineExtract(u)
            u.startsWith("inspect code") -> invokeAction("CodeInspection.OnEditor")
            u.startsWith("speech pause") -> pauseSpeech()
            u == SHOW_USAGES -> invokeAction("ShowUsages")
//            u.startsWith(OKAY_IDEA) -> routineOkIdea()
//            u.startsWith(OKAY_GOOGLE) -> fireGoogleSearch()
            "break point" in u -> routineHandleBreakpoint(u)
            u.startsWith(DEBUG) -> IDEService.type(VK_CONTROL, VK_SHIFT, VK_F9)
            u.startsWith("step") -> routineStep(u)
            u.startsWith("resume") -> invokeAction("Resume")
            u.startsWith("tell me a joke") -> tellJoke()
            "check" in u -> routineCheck(u)
            "tell me about yourself" in u -> routineAbout()
//            "add new class" in u -> routineAddNewClass()
            "print line" in u -> routinePrintln()
//            "new string" in u -> routineNewString()
//            "enter " in u -> routineEnter(u)
            "public static void main" in u -> routinePsvm()
            u.endsWith("of line") -> routineOfLine(u)
            u.startsWith("find in") -> routineFind(u)
        }
    }
}