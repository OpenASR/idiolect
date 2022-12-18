package org.openasr.idear.actions.recognition

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.psi.PsiElement
import com.intellij.usages.*
import org.openasr.idear.ide.IdeService
import org.openasr.idear.psi.PsiUtil.findContainingClass
import org.openasr.idear.psi.PsiUtil.findElementUnderCaret
import org.openasr.idear.tts.TTSService
import java.awt.Component
import java.util.*

class FindUsagesActionRecognizer : ActionRecognizer {
    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
    override fun isMatching(utterance: String) = "find" in utterance

    override fun getActionInfo(utterance: String, dataContext: DataContext): ActionCallInfo {
        val aci = ActionCallInfo(IdeActions.ACTION_FIND_USAGES)

        // Ok, that's lame
        val words = listOf(*utterance.split("\\W+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val wordsSet = HashSet(words)

        val editor = IdeService.getEditor(dataContext)
        val project = IdeService.getProject(dataContext)

        if (editor == null || project == null) return aci

        val klass = editor.findElementUnderCaret()!!.findContainingClass() ?: return aci

        var targets = arrayOf<PsiElement>()

        var targetName: String? = null
        var subject: String? = null

        if ("field" in wordsSet) {
            subject = "field"
            targetName = extractNameOf("field", words)

            if (targetName.isEmpty()) return aci
            targets = arrayOf(klass.findFieldByName(targetName, /*checkBases*/ true)!!)
        } else if ("method" in wordsSet) {
            subject = "method"
            targetName = extractNameOf("method", words)

            if (targetName.isEmpty()) return aci
            targets = arrayOf(*klass.findMethodsByName(targetName, /*checkBases*/ true))
        }

        // TODO(kudinkin): need to cure this pain someday

        aci.actionEvent = AnActionEvent(null,
                SimpleDataContext.getSimpleContext(UsageView.USAGE_TARGETS_KEY, prepare(targets[0]), dataContext),
                ActionPlaces.UNKNOWN, Presentation(), ActionManager.getInstance(), 0)

        // TODO(kudinkin): move it to appropriate place
        TTSService.say("Looking for usages of the $subject $targetName")

        return aci
    }

    private fun prepare(target: PsiElement): Array<UsageTarget> = arrayOf(PsiElement2UsageTargetAdapter(target, false))

    private fun extractNameOf(pivot: String, utterance: List<String>): String {
        val target = StringBuilder()

        for (i in utterance.indexOf(pivot) + 1 until utterance.size) {
            target.append(utterance[i])
        }

        return target.toString()
    }
}
