package org.openasr.idear.jsgf;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.openasr.idear.jsgf.psi.*;

import javax.swing.*;

/**
 * Created by breandan on 11/15/2015.
 */
public class JSpeechPsiImplUtil {
    public static String getName(JSpeechRuleDefinition element) {
        return getRule(element);
    }

    public static PsiElement setName(JSpeechRuleDefinition element,
                                     String newName) {
        ASTNode keyNode =
            element.getNode().findChildByType(JSpeechTypes.RULENAME);
        if (keyNode != null) {

            JSpeechRulename
                property =
                JSpeechElementFactory.createProperty(element.getProject(),
                                                     newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement setRule(JSpeechRuleDefinition element,
                                     String newName) {
        ASTNode keyNode =
            element.getNode().findChildByType(JSpeechTypes.RULENAME);
        if (keyNode != null) {

            JSpeechRulename property = JSpeechElementFactory.createProperty(
                element.getProject(),
                newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static String getRule(JSpeechRuleDefinition element) {
        ASTNode keyNode =
            element.getNode().findChildByType(JSpeechTypes.RULENAME);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    public static PsiElement getNameIdentifier(JSpeechRuleDefinition element) {
        ASTNode keyNode =
            element.getNode().findChildByType(JSpeechTypes.RULENAME);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }

    public static ItemPresentation getPresentation(final
                                                   JSpeechRuleDefinition
                                                       element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return element.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return JSpeechIcons.FILE;
            }
        };
    }
}
