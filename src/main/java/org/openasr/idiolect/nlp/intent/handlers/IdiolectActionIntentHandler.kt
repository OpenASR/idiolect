package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.find.findUsages.PsiElement2UsageTargetAdapter
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.psi.PsiElement
import com.intellij.usages.UsageTarget
import com.intellij.usages.UsageView
import org.openasr.idiolect.actions.ActionRoutines
import org.openasr.idiolect.actions.recognition.*
import org.openasr.idiolect.ide.IdeService
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import org.openasr.idiolect.psi.PsiUtil.findContainingClass
import org.openasr.idiolect.psi.PsiUtil.findElementUnderCaret
import org.openasr.idiolect.tts.TtsService
import org.openasr.idiolect.utils.toCamelCase

class IdiolectActionIntentHandler : IntentHandler {
    companion object {
        private val INTENT_PREFIX = IntentResolver.INTENT_PREFIX_IDIOLECT_ACTION
    }

    override fun tryFulfillIntent(response: NlpResponse, dataContext: DataContext): ActionCallInfo? {
        if (!response.intentName.startsWith(INTENT_PREFIX)) {
            return null
        }

        return when (response.intentName) {
            ExtractFieldOrVariable.INTENT_NAME -> extractFieldOrVariable(response)
            FindUsagesActionRecognizer.INTENT_NAME -> findUsages(response, dataContext)
            RenameActionRecognizer.INTENT_NAME -> rename(response)
            FileNavigationRecognizer.INTENT_FOCUS -> fulfillWithSlot(response, "target", ActionRoutines::routineFocus)
            FileNavigationRecognizer.INTENT_GO_TO_LINE -> fulfillWithSlot(response, "line", ActionRoutines::routineGoto)
            FileNavigationRecognizer.INTENT_OF_LINE -> fulfillWithSlot(response, "position", ActionRoutines::routineOfLine)
            else -> return null
        }
    }

    private fun fulfillWithSlot(nlpResponse: NlpResponse, slotName: String, routine: (String) -> Unit ): ActionCallInfo {
        if (nlpResponse.slots != null) {
            routine(nlpResponse.slots[slotName]!!)
        }
        return ActionCallInfo(nlpResponse.intentName, true)
    }

    private fun rename(nlpResponse: NlpResponse): ActionCallInfo =
        ActionCallInfo(nlpResponse.intentName).apply {
            val name = nlpResponse.slots!!["name"]!!
            if (name.isNotEmpty()) {
                typeAfter = name.toCamelCase()
                hitTabAfter = true
            }
        }

    private fun extractFieldOrVariable(nlpResponse: NlpResponse): ActionCallInfo =
        ActionCallInfo(nlpResponse.slots!!["actionId"]!!).apply {
            val name = nlpResponse.slots!!["name"]!!
            if (name.isNotEmpty()) {
                typeAfter = name.toCamelCase()
                hitTabAfter = true
            }
        }

    private fun findUsages(nlpResponse: NlpResponse, dataContext: DataContext): ActionCallInfo {
        val info = ActionCallInfo(IdeActions.ACTION_FIND_USAGES)

        val editor = IdeService.getEditor(dataContext)
        val project = IdeService.getProject(dataContext)

        if (editor == null || project == null) return info

        val targetName = nlpResponse.slots!!["target"]
        if (targetName.isNullOrEmpty()) return info

        val klass = editor.findElementUnderCaret()!!.findContainingClass() ?: return info

        val subject = nlpResponse.slots["subject"]
        val target: PsiElement? = when (subject) {
            "field" -> klass.findFieldByName(targetName.toCamelCase(), true)
            "method" -> klass.findMethodsByName(targetName.toCamelCase(), true).first()
            else -> return info
        }

        if (target != null) {
            // TODO(kudinkin): need to cure this pain someday
            info.actionEvent = AnActionEvent(
                null,
                SimpleDataContext.getSimpleContext(UsageView.USAGE_TARGETS_KEY, prepare(target), dataContext),
                ActionPlaces.UNKNOWN, Presentation(), ActionManager.getInstance(), 0
            )

            // TODO(kudinkin): move it to appropriate place
            TtsService.say("Looking for usages of the $subject $targetName")
        }

        return info
    }

    private fun prepare(target: PsiElement): Array<UsageTarget> = arrayOf(PsiElement2UsageTargetAdapter(target, false))
}
