package com.jetbrains.idear.jsgf.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.jetbrains.idear.jsgf.psi.JSpeechElementFactory;
import com.jetbrains.idear.jsgf.psi.JSpeechRuleDefinition;
import com.jetbrains.idear.jsgf.psi.JSpeechRulename;
import com.jetbrains.idear.jsgf.psi.JSpeechTypes;

/**
 * Created by breandan on 11/15/2015.
 */
public class JSpeechImplUtil {
    public static String getName(JSpeechRuleDefinition element) {
        return getKey(element);
    }

    public static PsiElement setName(JSpeechRuleDefinition element, String newName) {
        ASTNode keyNode = element.getNode().findChildByType(JSpeechTypes.RULENAME);
        if (keyNode != null) {

            JSpeechRulename property = JSpeechElementFactory.createProperty(element.getProject(), newName);
            ASTNode newKeyNode = property.getFirstChild().getNode();
            element.getNode().replaceChild(keyNode, newKeyNode);
        }
        return element;
    }

    public static String getKey(JSpeechRuleDefinition element) {
        ASTNode keyNode = element.getNode().findChildByType(JSpeechTypes.RULENAME);
        if (keyNode != null) {
            return keyNode.getText();
        } else {
            return null;
        }
    }

    public static PsiElement getNameIdentifier(JSpeechRuleDefinition element) {
        ASTNode keyNode = element.getNode().findChildByType(JSpeechTypes.RULENAME);
        if (keyNode != null) {
            return keyNode.getPsi();
        } else {
            return null;
        }
    }
}
