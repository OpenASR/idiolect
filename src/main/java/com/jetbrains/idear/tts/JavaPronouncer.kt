package com.jetbrains.idear.tts

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.java.JavaLanguage
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.util.IncorrectOperationException
import com.jetbrains.idear.CodeToTextConverter
import org.jetbrains.annotations.Nls

class JavaPronouncer : IntentionAction {

    @Nls
    override fun getText(): String {
        return "Pronounce"
    }

    @Nls
    override fun getFamilyName(): String {
        return "Pronounce"
    }

    override fun isAvailable(project: Project, editor: Editor, psiFile: PsiFile): Boolean {
        val selectionModel = editor.selectionModel
        return psiFile.language === JavaLanguage.INSTANCE && selectionModel.hasSelection()
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, psiFile: PsiFile) {
        val start = editor.selectionModel.selectionStart
        val end = editor.selectionModel.selectionEnd
        var range: TextRange? = null
        if (end > start) {
            range = TextRange(start, end)
        }
        val caretOffset = editor.caretModel.offset

        val converter = CodeToTextConverter(psiFile, range, caretOffset)

        val service = ServiceManager.getService(TTSService::class.java)
        service.say(converter.toText())
    }

    override fun startInWriteAction(): Boolean {
        return false
    }
}
