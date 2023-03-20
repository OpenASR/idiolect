package org.openasr.idiolect.ide

import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.java.PsiDeclarationStatementImpl
import com.intellij.psi.util.PsiTreeUtil

/**
 * JavaCodeContextType.isInContext() rejects Template commands at PsiWhiteSpace.
 * This class fibs about findElementAt() being PsiWhiteSpace
 */
class PsiFileNoWhiteSpace(private val actual: PsiFile) : PsiFile by actual {
    class PhantomElement(private val _parent: PsiElement) : PsiDeclarationStatementImpl() {
        override fun getParent() = _parent
        override fun getTextRange() = _parent.textRange
    }

    override fun findElementAt(offset: Int): PsiElement? {
        val element = actual.findElementAt(offset)

        if (element is PsiWhiteSpace) {
            val statement = PsiTreeUtil.getPrevSiblingOfType(element, PsiStatement::class.java)!!
            return PhantomElement(statement)
        }

        return element
    }
}
