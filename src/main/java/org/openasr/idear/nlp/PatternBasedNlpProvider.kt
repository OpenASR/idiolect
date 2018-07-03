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
import org.openasr.idear.nlp.Commands.OPEN
import org.openasr.idear.nlp.Commands.PRESS
import org.openasr.idear.nlp.Commands.SHOW_USAGES
import org.openasr.idear.nlp.Commands.SHRINK
import org.openasr.idear.nlp.Commands.WHERE_AM_I
import org.openasr.idear.tts.TTSService
import java.awt.event.KeyEvent.*

class PatternBasedNlpProvider : NlpProvider {
    /**
     * @param utterance - the command as spoken
     */
    override fun processUtterance(utterance: String) {
        when {
            utterance == HI_IDEA -> TTSService.say("Hi, again!")
            utterance.startsWith(OPEN) -> routineOpen(utterance)
            utterance.startsWith(NAVIGATE) -> invokeAction("GotoDeclaration")
            utterance.startsWith(EXECUTE) -> invokeAction("Run")
            utterance == WHERE_AM_I -> WhereAmIAction()
            utterance.startsWith(FOCUS) -> routineFocus(utterance)
            utterance.startsWith(GOTO) -> routineGoto(utterance)
            utterance.startsWith(EXPAND) -> invokeAction(ACTION_EDITOR_SELECT_WORD_AT_CARET)
            utterance.startsWith(SHRINK) -> invokeAction(ACTION_EDITOR_UNSELECT_WORD_AT_CARET)
            utterance.startsWith(PRESS) -> routinePress(utterance)
            utterance.startsWith("release") -> routineReleaseKey(utterance)
            utterance.startsWith("following") -> routineFollowing(utterance)
            utterance.startsWith("extract this") -> routineExtract(utterance)
            utterance.startsWith("inspect code") -> invokeAction("CodeInspection.OnEditor")
            utterance.startsWith("speech pause") -> pauseSpeech()
            utterance == SHOW_USAGES -> invokeAction("ShowUsages")
//            u.startsWith(OKAY_IDEA) -> routineOkIdea()
//            u.startsWith(OKAY_GOOGLE) -> fireGoogleSearch()
            "break point" in utterance -> routineHandleBreakpoint(utterance)
            utterance.startsWith(DEBUG) -> IDEService.type(VK_CONTROL, VK_SHIFT, VK_F9)
            utterance.startsWith("step") -> routineStep(utterance)
            utterance.startsWith("resume") -> invokeAction("Resume")
            utterance.startsWith("tell me a joke") -> tellJoke()
            "check" in utterance -> routineCheck(utterance)
            "tell me about yourself" in utterance -> routineAbout()
//            "add new class" in u -> routineAddNewClass()
            "print line" in utterance -> routinePrintln()
//            "new string" in u -> routineNewString()
//            "enter " in u -> routineEnter(u)
            "public static void main" in utterance -> routinePsvm()
            utterance.endsWith("of line") -> routineOfLine(utterance)
            utterance.startsWith("find in") -> routineFind(utterance)
        }
    }
}