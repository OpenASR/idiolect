package org.openasr.idiolect.ide

import com.intellij.openapi.util.Key
import com.intellij.psi.*
import com.intellij.psi.impl.source.tree.java.PsiDeclarationStatementImpl
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil

/**
 * JavaCodeContextType.isInContext() rejects Template commands at PsiWhiteSpace.
 * This class fibs about findElementAt() being PsiWhiteSpace
 */
class PsiFileNoWhiteSpace(private val actual: PsiFile) : PsiFile {
    class PhantomElement(val _parent: PsiElement) : PsiDeclarationStatementImpl() {
        override fun getParent() = _parent
        override fun getTextRange() = _parent.textRange
    }

    override fun findElementAt(offset: Int): PsiElement? {
        var element = actual.findElementAt(offset)

        if (element is PsiWhiteSpace) {
            val statement = PsiTreeUtil.getPrevSiblingOfType(element, PsiStatement::class.java)!!
            val notWhiteSpace = PhantomElement(statement)
            return notWhiteSpace
        }

        return element
    }

    override fun <T : Any?> getUserData(key: Key<T>) = actual.getUserData(key)

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) = actual.putUserData(key, value)

    override fun getIcon(flags: Int) = actual.getIcon(flags)

    override fun getProject() = actual.project

    override fun getLanguage() = actual.language

    override fun getManager() = actual.manager

    override fun getChildren() = actual.children

    override fun getParent() = actual.parent

    override fun getFirstChild() = actual.firstChild

    override fun getLastChild() = actual.lastChild

    override fun getNextSibling() = actual.nextSibling

    override fun getPrevSibling() = actual.prevSibling

    override fun getContainingFile() = actual.containingFile

    override fun getTextRange() = actual.textRange

    override fun getStartOffsetInParent() = actual.startOffsetInParent

    override fun getTextLength() = actual.textLength

    override fun findReferenceAt(offset: Int) = actual.findReferenceAt(offset)

    override fun getTextOffset() = actual.textOffset

    override fun getText() = actual.text

    override fun textToCharArray() = actual.textToCharArray()

    override fun getNavigationElement() = actual.navigationElement

    override fun getOriginalElement() = actual.originalElement

    override fun textMatches(text: CharSequence) = actual.textMatches(text)

    override fun textMatches(element: PsiElement) = actual.textMatches(element)

    override fun textContains(c: Char) = actual.textContains(c)

    override fun accept(visitor: PsiElementVisitor) = actual.accept(visitor)

    override fun acceptChildren(visitor: PsiElementVisitor) = actual.acceptChildren(visitor)

    override fun copy() = actual.copy()

    override fun add(element: PsiElement) = actual.add(element)

    override fun addBefore(element: PsiElement, anchor: PsiElement?) = actual.addBefore(element, anchor)

    override fun addAfter(element: PsiElement, anchor: PsiElement?) = actual.addAfter(element, anchor)

    override fun checkAdd(element: PsiElement)  = actual.checkAdd(element)

    override fun addRange(first: PsiElement?, last: PsiElement?) = actual.addRange(first, last)

    override fun addRangeBefore(first: PsiElement, last: PsiElement, anchor: PsiElement?) =
        actual.addRangeBefore(first, last, anchor)

    override fun addRangeAfter(first: PsiElement?, last: PsiElement?, anchor: PsiElement?) =
        actual.addRangeAfter(first, last, anchor)

    override fun delete() = actual.delete()

    override fun checkDelete() = actual.checkDelete()

    override fun deleteChildRange(first: PsiElement?, last: PsiElement?) = actual.deleteChildRange(first, last)

    override fun replace(newElement: PsiElement) = actual.replace(newElement)

    override fun isValid() = actual.isValid

    override fun isWritable() = actual.isWritable

    override fun getReference() = actual.reference

    override fun getReferences() = actual.references

    override fun <T : Any?> getCopyableUserData(key: Key<T>) = actual.getCopyableUserData(key)

    override fun <T : Any?> putCopyableUserData(key: Key<T>, value: T?) = actual.putCopyableUserData(key, value)

    override fun processDeclarations(
        processor: PsiScopeProcessor,
        state: ResolveState,
        lastParent: PsiElement?,
        place: PsiElement
    ) = actual.processDeclarations(processor, state, lastParent, place)

    override fun getContext() = actual.context

    override fun isPhysical() = actual.isPhysical

    override fun getResolveScope() = actual.resolveScope

    override fun getUseScope() = actual.useScope

    override fun getNode() = actual.node

    override fun isEquivalentTo(another: PsiElement?) = actual.isEquivalentTo(another)

    override fun getName() = actual.name

    override fun setName(name: String) = actual.setName(name)

    override fun checkSetName(name: String?) = actual.checkSetName(name)

    override fun navigate(requestFocus: Boolean) = actual.navigate(requestFocus)

    override fun canNavigate() = actual.canNavigate()

    override fun canNavigateToSource() = actual.canNavigateToSource()

    override fun getPresentation() = actual.presentation

    override fun isDirectory() = actual.isDirectory

    override fun getVirtualFile() = actual.virtualFile

    override fun processChildren(processor: PsiElementProcessor<in PsiFileSystemItem>) =
        actual.processChildren(processor)

    override fun getContainingDirectory() = actual.containingDirectory

    override fun getModificationStamp() = actual.modificationStamp

    override fun getOriginalFile() = actual.originalFile

    override fun getFileType() = actual.fileType

    override fun getPsiRoots() = actual.psiRoots

    override fun getViewProvider() = actual.viewProvider

    override fun subtreeChanged() = actual.subtreeChanged()
}
