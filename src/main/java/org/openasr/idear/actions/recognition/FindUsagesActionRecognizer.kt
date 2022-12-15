package org.openasr.idear.actions.recognition

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.psi.PsiElement
import com.intellij.usages.*
import org.openasr.idear.ide.IDEService
import org.openasr.idear.psi.PsiUtil.findContainingClass
import org.openasr.idear.psi.PsiUtil.findElementUnderCaret
import org.openasr.idear.tts.TTSService
import java.util.*

//runs only selected configuration
class FindUsagesActionRecognizer : ActionRecognizer {

    override fun isMatching(sentence: String) = "find" in sentence

    override fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo {
        val aci = ActionCallInfo("FindUsages")

        // Ok, that's lame
        val words = listOf(*sentence.split("\\W+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val wordsSet = HashSet(words)

        val editor = IDEService.getEditor(dataContext)
        val project = IDEService.getProject(dataContext)

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

    private fun extractNameOf(pivot: String, sentence: List<String>): String {
        val target = StringBuilder()

        for (i in sentence.indexOf(pivot) + 1 until sentence.size) {
            target.append(sentence[i])
        }

        return target.toString()
    }
}
