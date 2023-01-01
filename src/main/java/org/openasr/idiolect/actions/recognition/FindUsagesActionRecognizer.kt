package org.openasr.idiolect.actions.recognition

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.openapi.editor.impl.EditorComponentImpl
import com.intellij.psi.PsiElement
import com.intellij.usages.*
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpRegexGrammar
import org.openasr.idiolect.psi.PsiUtil.findContainingClass
import org.openasr.idiolect.psi.PsiUtil.findElementUnderCaret
import org.openasr.idiolect.tts.TtsService
import org.openasr.idiolect.utils.toCamelCase
import java.awt.Component

class FindUsagesActionRecognizer : ActionRecognizer("Find Usages", 500) {
    override val grammars = listOf(
        object : NlpRegexGrammar("Idiolect.FindUsages", "find usages of (field|method) ?(.*)?") {
            override fun createActionCallInfo(values: List<String>, dataContext: DataContext): ActionCallInfo {
                val info = ActionCallInfo(IdeActions.ACTION_FIND_USAGES)
                val editor = IdeService.getEditor(dataContext)
                val project = IdeService.getProject(dataContext)

                if (editor == null || project == null) return info

                val klass = editor.findElementUnderCaret()!!.findContainingClass() ?: return info

                val targetName = values[2]
                if (targetName.isEmpty()) return info

                val subject = values[1]
                val target: PsiElement = when (subject) {
                    "field" -> klass.findFieldByName(targetName.toCamelCase(), true)!!
                    "method" -> klass.findMethodsByName(targetName.toCamelCase(), true).first()
                    else -> return info
                }

                // TODO(kudinkin): need to cure this pain someday
                info.actionEvent = AnActionEvent(
                    null,
                    SimpleDataContext.getSimpleContext(UsageView.USAGE_TARGETS_KEY, prepare(target), dataContext),
                    ActionPlaces.UNKNOWN, Presentation(), ActionManager.getInstance(), 0
                )

                // TODO(kudinkin): move it to appropriate place
                TtsService.say("Looking for usages of the $subject $targetName")

                return info
            }
        }.withExamples(
            "find usages",
            "find usages of field 'my field'",
            "find usages of method 'my method'"
        )
    )

    override fun isSupported(dataContext: DataContext, component: Component?) = component is EditorComponentImpl
//    override fun isMatching(utterance: String) = "find" in utterance

    private fun prepare(target: PsiElement): Array<UsageTarget> = arrayOf(PsiElement2UsageTargetAdapter(target, false))
}
