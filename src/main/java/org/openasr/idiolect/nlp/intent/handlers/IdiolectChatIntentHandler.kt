package org.openasr.idiolect.nlp.intent.handlers

import com.intellij.openapi.components.service
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import org.openasr.idiolect.actions.recognition.*
import org.openasr.idiolect.nlp.NlpContext
import org.openasr.idiolect.nlp.NlpResponse
import org.openasr.idiolect.nlp.ai.AiService
import org.openasr.idiolect.nlp.intent.resolvers.IntentResolver
import org.openasr.idiolect.presentation.toolwindow.IdiolectToolWindowFactory


class IdiolectChatIntentHandler : IntentHandler {
    companion object {
        val INTENT_PREFIX = IntentResolver.INTENT_PREFIX_IDIOLECT_CHAT
    }

    override fun tryFulfillIntent(response: NlpResponse, nlpContext: NlpContext): ActionCallInfo? {
        if (!response.intentName.startsWith(INTENT_PREFIX)) {
            return null
        }

        return when (response.intentName) {
            AiChatRecognizer.INTENT_EXPLAIN_CLASS -> explainClass(nlpContext)
            AiChatRecognizer.INTENT_EXPLAIN_FUNCTION -> explainFunction(nlpContext)
            AiChatRecognizer.INTENT_TESTS_FOR_CLASS -> writeTestsForClass(nlpContext)
            AiChatRecognizer.INTENT_TESTS_FOR_FUNCTION -> writeTestsForFunction(nlpContext)
            else -> return null
        }
    }

    private fun explainFunction(nlpContext: NlpContext): ActionCallInfo {
        val instructions = ""
        completionForParent(nlpContext, instructions,"explain this function", PsiMethod::class.java)

        return ActionCallInfo(AiChatRecognizer.INTENT_EXPLAIN_FUNCTION, true)
    }

    private fun explainClass(nlpContext: NlpContext): ActionCallInfo {
        val instructions = ""
        completionForParent(nlpContext, instructions,"explain this class", PsiClass::class.java)

        return ActionCallInfo(AiChatRecognizer.INTENT_EXPLAIN_CLASS, true)
    }

    private fun writeTestsForFunction(nlpContext: NlpContext): ActionCallInfo {
        // TODO: get project-specific instructions for writing tests, add to instructions
        val instructions = "In the first line of your response you will provide the test file path," +
            "and then test code inside triple back-ticks."
        completionForParent(nlpContext, instructions,"write tests for this function", PsiMethod::class.java)

        return ActionCallInfo(AiChatRecognizer.INTENT_TESTS_FOR_FUNCTION, true)
    }

    private fun writeTestsForClass(nlpContext: NlpContext): ActionCallInfo {
        // TODO: get project-specific instructions for writing tests, add to instructions
        val instructions = "In the first line of your response you will provide the test file path," +
            "and then test code inside triple back-ticks."
        completionForParent(nlpContext, instructions,"write tests for this class", PsiClass::class.java)

        return ActionCallInfo(AiChatRecognizer.INTENT_TESTS_FOR_CLASS, true)
    }

    private fun <T : PsiElement> completionForParent(nlpContext: NlpContext, instructions: String, prompt: String, parentType: Class<T>) {
        val file = nlpContext.getCurrentFile()
        val element = getElementAtCursor(nlpContext, file)

        if (file != null && element != null) {
            val containingMethod = PsiTreeUtil.getParentOfType(element, parentType)

            val project = nlpContext.getProject()!!
            val relativePath = file.virtualFile.path.substring(project.basePath!!.length + 1)

            if (containingMethod != null) {
                val aiService = service<AiService>()
                aiService.sendChat("""
The user is viewing the following function in `${relativePath}`:
```
${containingMethod.text}
```
$instructions
""".trimIndent(),
                    prompt)

                IdiolectToolWindowFactory.showTab(IdiolectToolWindowFactory.Tab.CHAT)
            }
        }
    }

    private fun getElementAtCursor(nlpContext: NlpContext, psiFile: PsiFile?): PsiElement? {
        val editor = nlpContext.getEditor()

        return if (editor != null && psiFile != null) {
            psiFile.findElementAt(editor.caretModel.offset)
        } else null
    }
}
