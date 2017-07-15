// This is a generated file. Not intended for manual editing.
package org.openasr.idear.jsgf.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.openasr.idear.jsgf.*;
import org.openasr.idear.jsgf.psi.impl.*;

public interface JSpeechTypes {

    IElementType DECLARATION = new JSpeechElementType("DECLARATION");
    IElementType LITERAL = new JSpeechElementType("LITERAL");
    IElementType RULENAME = new JSpeechElementType("RULENAME");
    IElementType RULE_ALTERNATIVE = new JSpeechElementType("RULE_ALTERNATIVE");
    IElementType RULE_DEFINITION = new JSpeechElementType("RULE_DEFINITION");
    IElementType RULE_EXPANSION = new JSpeechElementType("RULE_EXPANSION");
    IElementType SCOPE = new JSpeechElementType("SCOPE");
    IElementType SELF_IDENT_HEADER =
        new JSpeechElementType("SELF_IDENT_HEADER");
    IElementType SEQUENCE_ELEMENT = new JSpeechElementType("SEQUENCE_ELEMENT");
    IElementType SUBEXPANSION = new JSpeechElementType("SUBEXPANSION");
    IElementType WEIGHT = new JSpeechElementType("WEIGHT");

    IElementType BANG = new JSpeechTokenType("!");
    IElementType BRACE1 = new JSpeechTokenType("{");
    IElementType BRACE2 = new JSpeechTokenType("}");
    IElementType BRACKET1 = new JSpeechTokenType("[");
    IElementType BRACKET2 = new JSpeechTokenType("]");
    IElementType BRACKET3 = new JSpeechTokenType("<");
    IElementType BRACKET4 = new JSpeechTokenType(">");
    IElementType COLON = new JSpeechTokenType(":");
    IElementType COMMA = new JSpeechTokenType(",");
    IElementType EQUALS = new JSpeechTokenType("=");
    IElementType FLOAT = new JSpeechTokenType("FLOAT");
    IElementType GRAMMAR = new JSpeechTokenType("grammar");
    IElementType HASH = new JSpeechTokenType("#");
    IElementType NUMBER = new JSpeechTokenType("NUMBER");
    IElementType OR = new JSpeechTokenType("|");
    IElementType PAREN1 = new JSpeechTokenType("(");
    IElementType PAREN2 = new JSpeechTokenType(")");
    IElementType PERIOD = new JSpeechTokenType(".");
    IElementType PRIVATE = new JSpeechTokenType("private");
    IElementType PUBLIC = new JSpeechTokenType("public");
    IElementType SEMICOLON = new JSpeechTokenType(";");
    IElementType SLASH = new JSpeechTokenType("/");
    IElementType STRING = new JSpeechTokenType("STRING");
    IElementType VERSION = new JSpeechTokenType("JSGF V1.0");

    class Factory {
        public static PsiElement createElement(ASTNode node) {
            IElementType type = node.getElementType();
            if (type == DECLARATION) {
                return new JSpeechDeclarationImpl(node);
            } else if (type == LITERAL) {
                return new JSpeechLiteralImpl(node);
            } else if (type == RULENAME) {
                return new JSpeechRulenameImpl(node);
            } else if (type == RULE_ALTERNATIVE) {
                return new JSpeechRuleAlternativeImpl(node);
            } else if (type == RULE_DEFINITION) {
                return new JSpeechRuleDefinitionImpl(node);
            } else if (type == RULE_EXPANSION) {
                return new JSpeechRuleExpansionImpl(node);
            } else if (type == SCOPE) {
                return new JSpeechScopeImpl(node);
            } else if (type == SELF_IDENT_HEADER) {
                return new JSpeechSelfIdentHeaderImpl(node);
            } else if (type == SEQUENCE_ELEMENT) {
                return new JSpeechSequenceElementImpl(node);
            } else if (type == SUBEXPANSION) {
                return new JSpeechSubexpansionImpl(node);
            } else if (type == WEIGHT) {
                return new JSpeechWeightImpl(node);
            }
            throw new AssertionError("Unknown element type: " + type);
        }
    }
}
