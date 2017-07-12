package org.openasr.idear

import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile

class CodeToTextConverter(private val myFile: PsiFile,
                          private val myRange: TextRange?,
                          private val myCaretOffset: Int) {
    private val myProject: Project
    private val myDocument: Document?

    init {
        myProject = myFile.project
        myDocument = PsiDocumentManager.getInstance(myProject).getDocument(myFile)
    }

    fun toText(): String {
        if (myDocument == null) {
            return "Sorry"
        }

        if (myRange != null) {
            return myDocument.getText(myRange)
        }

        val currentLine = myDocument.getLineNumber(myCaretOffset)
        val lineStartOffset = myDocument.getLineStartOffset(currentLine)

        return myDocument.getText(TextRange(lineStartOffset, currentLine))
    }
}
