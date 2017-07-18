package org.openasr.idear.actions.recognition

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.psi.PsiElement
import com.intellij.usages.UsageTarget
import com.intellij.usages.UsageView
import org.openasr.idear.psi.PsiUtil.findContainingClass
import org.openasr.idear.psi.PsiUtil.findElementUnderCaret
import org.openasr.idear.tts.TTSService
import java.util.*

//runs only selected configuration
class FindUsagesActionRecognizer : ActionRecognizer {

    override fun isMatching(sentence: String) = sentence.contains("find")

    override fun getActionInfo(sentence: String, dataContext: DataContext): ActionCallInfo {
        val aci = ActionCallInfo("FindUsages")

        // Ok, that's lame
        val words = Arrays.asList(*sentence.split("\\W+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        val wordsSet = HashSet(words)

        val editor = CommonDataKeys.EDITOR.getData(dataContext)
        val project = CommonDataKeys.PROJECT.getData(dataContext)

        if (editor == null || project == null)
            return aci

        val klass = editor.findElementUnderCaret()!!.findContainingClass() ?: return aci

        var targets = arrayOf<PsiElement>()

        var targetName: String? = null
        var subject: String? = null

        if (wordsSet.contains("field")) {
            subject = "field"
            targetName = extractNameOf("field", words)

            if (targetName.isEmpty())
                return aci

            targets = arrayOf(klass.findFieldByName(targetName, /*checkBases*/ true)!!)
        } else if (wordsSet.contains("method")) {
            subject = "method"
            targetName = extractNameOf("method", words)

            if (targetName.isEmpty())
                return aci

            targets = arrayOf(*klass.findMethodsByName(targetName, /*checkBases*/ true))
        }

        // TODO(kudinkin): need to cure this pain someday

        aci.actionEvent = AnActionEvent(null,
                SimpleDataContext.getSimpleContext(UsageView.USAGE_TARGETS_KEY.name, prepare(targets[0]), dataContext),
                ActionPlaces.UNKNOWN, Presentation(), ActionManager.getInstance(), 0)

        // TODO(kudinkin): move it to appropriate place
        TTSService.say("Looking for usages of the $subject $targetName")

        return aci
    }

    private fun prepare(target: PsiElement): Array<UsageTarget> = arrayOf(PsiElement2UsageTargetAdapter(target))

    private fun extractNameOf(pivot: String, sentence: List<String>): String {
        val target = StringBuilder()

        for (i in sentence.indexOf(pivot) + 1 until sentence.size) {
            target.append(sentence[i])
        }

        return target.toString()
    }
}
