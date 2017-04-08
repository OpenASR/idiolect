package com.jetbrains.idear.actions.recognition

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.usages.UsageTarget
import com.intellij.usages.UsageView
import com.jetbrains.idear.psi.PsiUtil
import com.jetbrains.idear.tts.TTSService

import java.util.Arrays
import java.util.HashSet

//runs only selected configuration
class FindUsagesActionRecognizer : ActionRecognizer {

    override fun isMatching(sentence: String): Boolean {
        return sentence.contains("find") /* && (sentence.contains("usages") || sentence.contains("usage")) */
    }

    override fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo {
        val aci = ActionCallInfo("FindUsages")

        // Ok, that's lame
        val words = Arrays.asList(*sentence.split("\\W+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val wordsSet = HashSet(words)

        val editor = CommonDataKeys.EDITOR.getData(dataContext)
        val project = CommonDataKeys.PROJECT.getData(dataContext)

        if (editor == null || project == null)
            return aci

        val e = PsiUtil.findElementUnderCaret(editor, project)
        val klass = PsiUtil.findContainingClass(e!!) ?: return aci

        var targets = arrayOf<PsiElement>()

        var targetName: String? = null
        var subject: String? = null

        if (wordsSet.contains("field")) {
            subject = "field"
            targetName = extractNameOf("field", words)

            if (targetName.isEmpty())
                return aci

            targets = arrayOf<PsiElement>(klass.findFieldByName(targetName, /*checkBases*/ true)!!)
        } else if (wordsSet.contains("method")) {
            subject = "method"
            targetName = extractNameOf("method", words)

            if (targetName.isEmpty())
                return aci

            targets = arrayOf<PsiElement>(*klass.findMethodsByName(targetName, /*checkBases*/ true))
        }

        if (targets == null)
            return aci

        // TODO(kudinkin): need to cure this pain someday

        aci.actionEvent = AnActionEvent(null,
                SimpleDataContext.getSimpleContext(UsageView.USAGE_TARGETS_KEY.name, prepare(targets[0]), dataContext),
                ActionPlaces.UNKNOWN, Presentation(), ActionManager.getInstance(), 0)

        // TODO(kudinkin): move it to appropriate place
        ServiceManager.getService(TTSService::class.java).say("Looking for usages of the $subject $targetName")

        return aci
    }

    private fun prepare(target: PsiElement): Array<UsageTarget> {
        return arrayOf(PsiElement2UsageTargetAdapter(target))
    }

    private fun extractNameOf(pivot: String, sentence: List<String>): String {
        val target = StringBuilder()

        for (i in sentence.indexOf(pivot) + 1..sentence.size - 1) {
            target.append(sentence[i])
        }

        return target.toString()
    }
}
